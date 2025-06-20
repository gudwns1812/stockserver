package kis.client.Service;

import kis.client.dto.client.FxResponseDto;
import kis.client.dto.client.IndicesResponseDto;
import kis.client.dto.kis.KisPopularDto;
import kis.client.dto.kis.KisStockDto;
import kis.client.dto.redis.StockDto;
import kis.client.entity.FxEncoder;
import kis.client.entity.Stock;
import kis.client.repository.StockInit;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

    @Value("${kis.clientId}")
    private String clientId;

    @Scheduled(fixedRate = 40_000)
    public void Refresh() throws Exception {
        LocalTime now = LocalTime.now();
        LocalTime startTime = LocalTime.of(8, 40);
        LocalTime endTime = LocalTime.of(16, 10);

        DayOfWeek day = LocalDate.now().getDayOfWeek();
        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            log.info("주말엔 장이 쉽니다~");
            return; // 주말 제외
        }
        if (now.isBefore(startTime) || now.isAfter(endTime)) {
            log.info("시장 운영 시간이 아님: 현재 시간 = {}", now);
            return;
        }
        List<Stock> stocks = stockInit.getStocks();
        ExecutorService executor = Executors.newFixedThreadPool(4); // 병렬 스레드 수 조절

        for (Stock stock : stocks) {

            executor.submit(() -> {
                try {
                    String stockCode = stock.getStockCode();
                    long start = System.currentTimeMillis();


                    KisStockDto stockInfo = getStockClient.getStockInfo(stockCode);
                    StockDto stockDto = new StockDto(stockCode, stockInfo);
                    redisTemplate.opsForValue().set("STOCK:" + stockCode, stockDto, Duration.ofHours(8));

                    long end = System.currentTimeMillis();
                    log.info("stockCode: {}, 경과 시간: {}ms", stockCode, (end - start));
                    Thread.sleep(120); // 과도한 호출 방지를 위해 잠깐 대기

                } catch (Exception e) {
                    log.error("stockCode 처리 중 에러 발생", e);
                }
            });


        }
        if (clientId.equals("client-8")) {
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
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES); // 종료 대기 (필수)

    }

    public void FxRefresh() {
        List<List<String>> allFx = fxEncoder.getAllFx();
        for (List<String> fx : allFx) {
            String type = fx.get(0);
            String code = fx.get(1);
            FxResponseDto fxResponseDto = getFxClient.FxInfo(type, code);
            redisTemplate.opsForValue().set("FX:" + code , fxResponseDto);
        }
    }
}
