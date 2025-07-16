package kis.client.Service;

import com.google.common.util.concurrent.RateLimiter;
import kis.client.dto.client.FxResponseDto;
import kis.client.dto.client.IndicesResponseDto;
import kis.client.dto.kis.KisPopularDto;
import kis.client.dto.kis.KisStockDto;
import kis.client.dto.redis.StockDto;
import kis.client.dto.redis.StockInfoDto;
import kis.client.entity.FxEncoder;
import kis.client.global.error.StockNotFoundException;
import kis.client.global.token.KisTokenManager;
import kis.client.repository.StockInit;
import kis.client.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j

public class RefreshService {

    private final StockInit stockInit;
    private final RedisTemplate<String, Object> redisTemplate;
    private final GetStockClient getStockClient;
    private final GetPopularStockService getPopularStockService;
    private final GetIndiceInfoClient getIndiceInfoClient;
    private final FxEncoder fxEncoder;
    private final GetFxClient getFxClient;
    private static final String FAILED_STOCK_KEY = "FAILED:STOCKS";
    private final StockRepository stockRepository;
    private final KisTokenManager kisTokenManager;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;
    private final RateLimiter rateLimiter =  RateLimiter.create(19.0, 1, TimeUnit.SECONDS); // 초당 10개 처리

    @Value("${kis.clientId}")
    private String clientId;

    @Scheduled(fixedRate = 30_000)
    public void Refresh() throws Exception {
//        ZoneId koreaZone = ZoneId.of("Asia/Seoul");
//        LocalTime now = LocalTime.now(koreaZone);
//        LocalTime startTime = LocalTime.of(8, 50);
//        LocalTime endTime = LocalTime.of(16, 0);
//
//        DayOfWeek day = LocalDate.now(koreaZone).getDayOfWeek();
//        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
//            log.info("주말엔 장이 쉽니다~ 월요일에 만나요~ 오늘은 {}입니다", day);
//            return; // 주말 제외
//        } else if (Holiday.isContain(LocalDate.now(koreaZone))) {
//            log.info("오늘은 공휴일이라 장이 쉽니다.");
//            return;
//        }
//        if (now.isBefore(startTime) || now.isAfter(endTime)) {
//            log.info("시장 운영 시간이 아님: 현재 시간 = {}", now);
//            return;
//        }
        List<StockInfoDto> stocks = stockInit.getStocks();
        int batchSize = 20;
        //토큰 한번에 한번만 발급
        String token = kisTokenManager.getToken();

        for (int i = 0; i < stocks.size(); i+= batchSize) {
            stockProcessing(token,stocks, i, batchSize);
        }
        //재처리
        List<String> failedCodes = new ArrayList<>();



        if (clientId.equals("client-9")) {
            log.info("마지막 클라이언트입니다. 나머지 정보 refresh");
            List<KisPopularDto> popularStock = getPopularStockService.getPopularStock(token);
            redisTemplate.opsForValue().set("POPULAR" , popularStock);
            IndicesResponseDto kospiInfo = getIndiceInfoClient.getIndiceInfo(token,"KOSPI",
                    LocalDateTime.now().minusDays(100).format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
            redisTemplate.opsForValue().set("INDICES_INFO:KOSPI" , kospiInfo);
            IndicesResponseDto kosdaqInfo = getIndiceInfoClient.getIndiceInfo(token,"KOSDAQ",
                    LocalDateTime.now().minusDays(100).format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
            redisTemplate.opsForValue().set("INDICES_INFO:KOSDAQ" , kosdaqInfo);
            FxRefresh();
        }
        String failedCode;
        while ((failedCode = (String) redisTemplate.opsForList().leftPop(FAILED_STOCK_KEY)) != null) {
            log.warn("재처리 코드 발견! : {}" , failedCode);
            failedCodes.add(failedCode);
        }
        //재처리가 있을떄만
        if (!failedCodes.isEmpty()) {
            List<StockInfoDto> failedStocks = failedCodes.stream()
                    .map(s -> stockRepository.findStockInfoByStockCode(s).orElseThrow(() -> new StockNotFoundException("주식 발견 실패"))).toList();
            for (int i = 0; i < failedStocks.size(); i += batchSize) {
                stockProcessing(token, failedStocks, i, batchSize);
            }

        }
        log.info("주식 정보 refresh 완료");
    }

    private void stockProcessing(String token, List<StockInfoDto> stocks, int i, int batchSize) throws InterruptedException {
        List<StockInfoDto> batch = stocks.subList(i, Math.min(i + batchSize, stocks.size()));
        CountDownLatch latch = new CountDownLatch(batch.size());
        log.info("처리중~~~");
        for (StockInfoDto stock : batch) {
            threadPoolTaskExecutor.submit(() -> {
//                rateLimiter.acquire();
                try {
                    String stockCode = stock.getStockCode();
                    KisStockDto stockInfo = getStockClient.getStockInfo(token,stockCode);

                    if (stockInfo == null) {
                        redisTemplate.opsForList().rightPush(FAILED_STOCK_KEY, stockCode);
//                        cloudWatchMetricService.putLatencyMetric(stockCode, durationMs);
//                        cloudWatchMetricService.putStockInfoNullMetric(stockCode);
                        log.warn("❌ [주식 정보 조회 실패] {} → 재처리 대상 등록", stockCode);
                        return;
                    }
                    StockDto stockDto = new StockDto(stock.getStockName(),stockCode, stockInfo);
                    stockDto.setStockImage(stock.getStockImage());
                    redisTemplate.opsForValue().set("STOCK:" + stockCode, stockDto);
                } catch (Exception e) {
                    log.error("stockCode 처리 중 에러 발생", e);
                } finally {
                    latch.countDown();
                }
            });


        }

        latch.await(); // 실제 끝나는 지점
        Thread.sleep(900);
    }


    public void FxRefresh() throws InterruptedException {
        String token = kisTokenManager.getToken();
        log.info("환율 정보를 refresh 합니다.");
        List<List<String>> allFx = fxEncoder.getAllFx();
        for (List<String> fx : allFx) {
            String type = fx.get(0);
            String code = fx.get(1);
            FxResponseDto fxResponseDto = getFxClient.FxInfo(token,type, code);
            redisTemplate.opsForValue().set("FX:" + code , fxResponseDto);
            Thread.sleep(20);
        }
    }

}
