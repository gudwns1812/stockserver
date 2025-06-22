package kis.client.Service;

import kis.client.dto.client.FxResponseDto;
import kis.client.dto.client.IndicesResponseDto;
import kis.client.dto.kis.KisPopularDto;
import kis.client.dto.kis.KisStockDto;
import kis.client.dto.redis.StockDto;
import kis.client.entity.FxEncoder;
import kis.client.entity.Stock;
import kis.client.global.error.StockNotFoundException;
import kis.client.global.token.KisTokenManager;
import kis.client.repository.StockInit;
import kis.client.repository.StockRepository;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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


    @Value("${kis.clientId}")
    private String clientId;

    @Scheduled(fixedRate = 40_000)
    public void Refresh() throws Exception {
        LocalTime now = LocalTime.now();
        LocalTime startTime = LocalTime.of(8, 40);
        LocalTime endTime = LocalTime.of(16, 10);

        DayOfWeek day = LocalDate.now().getDayOfWeek();
        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            log.info("주말엔 장이 쉽니다~ 월요일에 만나요~ 오늘은 {}입니다", day);
            return; // 주말 제외
        }
        if (now.isBefore(startTime) || now.isAfter(endTime)) {
            log.info("시장 운영 시간이 아님: 현재 시간 = {}", now);
            return;
        }
        List<Stock> stocks = stockInit.getStocks();
        int batchSize = 20;
        //토큰 한번에 한번만 발급
        String token = kisTokenManager.getToken();

        for (int i = 0; i < stocks.size(); i+= batchSize) {
            stockProcessing(token,stocks, i, batchSize);
        }
        //재처리
        List<String> failedCodes = new ArrayList<>();
        String failedCode;
        while ((failedCode = (String) redisTemplate.opsForList().leftPop(FAILED_STOCK_KEY)) != null) {
            log.warn("재처리 코드 발견! : {}" , failedCode);
            failedCodes.add(failedCode);
        }
        //재처리가 있을떄만
        if (!failedCodes.isEmpty()) {
            List<Stock> failedStocks = failedCodes.stream()
                    .map(s -> stockRepository.findByStockCode(s).orElseThrow(() -> new StockNotFoundException("주식 발견 실패"))).toList();
            for (int i = 0; i < failedStocks.size(); i += batchSize) {
                stockProcessing(token, failedStocks, i, batchSize);
            }
        }

        if (clientId.equals("client-8")) {
            log.info("마지막 클라이언트입니다. 나머지 정보 refresh");
            List<KisPopularDto> popularStock = getPopularStockService.getPopularStock();
            redisTemplate.opsForValue().set("POPULAR" , popularStock);
            IndicesResponseDto kospiInfo = getIndiceInfoClient.getIndiceInfo("KOSPI",
                    LocalDateTime.now().minusDays(100).format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
            redisTemplate.opsForValue().set("INDICE_INFO:KOSPI" , kospiInfo);
            IndicesResponseDto kosdaqInfo = getIndiceInfoClient.getIndiceInfo("KOSPI",
                    LocalDateTime.now().minusDays(100).format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
            redisTemplate.opsForValue().set("INDICE_INFO:KOSDAQ" , kosdaqInfo);
            FxRefresh();
        }


    }

    private void stockProcessing(String token, List<Stock> stocks, int i, int batchSize) throws InterruptedException {
        long start = System.currentTimeMillis();
        List<Stock> batch = stocks.subList(i, Math.min(i + batchSize, stocks.size()));
        ExecutorService executor = Executors.newFixedThreadPool(batchSize); // 병렬 스레드 수 조절
        CountDownLatch latch = new CountDownLatch(batch.size());

        for (Stock stock : batch) {

            executor.submit(() -> {
                try {
                    String stockCode = stock.getStockCode();

                    KisStockDto stockInfo = getStockClient.getStockInfo(token,stockCode);
                    if (stockInfo == null) {
                        redisTemplate.opsForList().rightPush(FAILED_STOCK_KEY, stockCode);
                        log.warn("❌ [주식 정보 조회 실패] {} → 재처리 대상 등록", stockCode);
                        return;
                    }
                    StockDto stockDto = new StockDto(stockCode, stockInfo);
                    redisTemplate.opsForValue().set("STOCK:" + stockCode, stockDto, Duration.ofHours(8));

                } catch (Exception e) {
                    log.error("stockCode 처리 중 에러 발생", e);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executor.shutdown();

        long elapsed = System.currentTimeMillis() - start;
        long sleepTime = Math.max(0, 1000 - elapsed);
        Thread.sleep(sleepTime);
    }

    public void FxRefresh() throws InterruptedException {
        log.info("환율 정보를 refresh 합니다.");
        List<List<String>> allFx = fxEncoder.getAllFx();
        for (List<String> fx : allFx) {
            String type = fx.get(0);
            String code = fx.get(1);
            FxResponseDto fxResponseDto = getFxClient.FxInfo(type, code);
            redisTemplate.opsForValue().set("FX:" + code , fxResponseDto);
            Thread.sleep(20);
        }
    }
}
