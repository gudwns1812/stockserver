package kis.client.repository;

import kis.client.Service.FailStock;
import kis.client.Service.GetStockClient;
import kis.client.dto.kis.KisPeriodStockDto;
import kis.client.entity.Stock;
import kis.client.entity.StockHistory;
import kis.client.global.token.KisTokenManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
public class StockHistoryInit implements CommandLineRunner {

    private final StockInit stockInit;
    private final List<String> types = List.of("D","W","M");
    private final GetStockClient getStockClient;
    private final List<FailStock> failStocks = new ArrayList<>();
    private final List<FailStock> finalFailed = new ArrayList<>();
    private final KisTokenManager kisTokenManager;
    private final StockHistoryRepository stockHistoryRepository;
    private AtomicInteger counter = new AtomicInteger(0);

    @Value("${init.enabled:false}") // 기본은 실행
    private boolean enable;

    @Override
    public void run(String... args) throws Exception {
        if (!enable) {
            return;
        }
        List<Stock> stocks = stockInit.getStocks();
        String token = kisTokenManager.getToken();
        LocalDate endDate = LocalDate.now();
        LocalDate limitDate = LocalDate.of(2017, 1, 1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate startDate;
        // startdate 만들기

        for (Stock stock : stocks) {
            LocalDate currentEndDate = endDate;
            for (String type : types) {
                while (true) {
                    startDate = switch (type) {
                        case "D" -> currentEndDate.minusDays(100);
                        case "W" -> currentEndDate.minusWeeks(100);
                        case "M" -> currentEndDate.minusMonths(100);
                        default -> throw new IllegalArgumentException("Invalid type: " + type);
                    };
                    String strStartDate = startDate.format(formatter);
                    try {
                        List<KisPeriodStockDto> stockDtos = getStockClient.getStockInfoByPeriod(token,stock.getStockCode(), type, strStartDate, endDate.format(formatter));
                        if (stockDtos == null || stockDtos.isEmpty()) {
                            if (counter.incrementAndGet() % 20 == 0) {
                                Thread.sleep(1000);
                            }
                            currentEndDate = startDate.minusDays(1);
                            if (startDate.isBefore(limitDate)) break;
                            continue;
                        }
                        processByType(stock.getStockCode(), type, stockDtos, strStartDate,endDate.format(formatter));
                    } catch (Exception e) {
                        log.info("api 호출 실패 주식코드: {} , 타입 : {}, strStartDate : {}, endDate: {}" ,stock.getStockCode(),type,strStartDate,endDate.format(formatter));
                        failStocks.add(new FailStock(stock.getStockCode(), type, strStartDate, endDate.format(formatter)));
                    }
                    if (counter.incrementAndGet() % 20 == 0) {
                        Thread.sleep(1500);
                    }
                    currentEndDate = startDate.minusDays(1);
                    if (startDate.isBefore(limitDate)) {
                        break;
                    }


                }
            }
        }

        for (FailStock failStock : failStocks) {

            String stockCode = failStock.getStockCode();
            String type = failStock.getType();
            String startDate1 = failStock.getStartDate();
            String endDate1 = failStock.getEndDate();

            log.info("재처리 시작 → stockCode: {}, type: {}, 기간: {} ~ {}", stockCode, type, startDate1, endDate1);
            List<KisPeriodStockDto> stockDtos = getStockClient.getStockInfoByPeriod(token,stockCode, type, startDate1, endDate1);
            if (stockDtos == null || stockDtos.isEmpty()) {
                log.warn("재처리도 실패 : 주식코드: {} , 타입 : {}, strStartDate : {}, endDate: {}" ,stockCode,type,startDate1,endDate1);
                finalFailed.add(failStock);
                continue;
            }
            processByType(stockCode, type, stockDtos, startDate1, endDate1);
        }

        // 최종 실패 항목을 파일로 저장
        if (!finalFailed.isEmpty()) {
            try (PrintWriter writer = new PrintWriter(new FileWriter("failStocks_retry_fail.csv"))) {
                writer.println("stockCode,type,startDate,endDate");
                for (FailStock fail : finalFailed) {
                    writer.printf("%s,%s,%s,%s%n",
                            fail.getStockCode(),
                            fail.getType(),
                            fail.getStartDate(),
                            fail.getEndDate()
                    );
                }
                log.info("최종 실패 항목 {}건을 failStocks_retry_fail.csv에 저장했습니다.", finalFailed.size());
            } catch (Exception e) {
                log.error("최종 실패 항목 저장 중 오류 발생", e);
            }
        }

    }
    private void processByType(String stockCode, String type , List<KisPeriodStockDto> stockDtos, String startDate, String endDate) throws Exception {
        // 기준 날짜: 2017년 1월 1일
        CountDownLatch latch = new CountDownLatch(stockDtos.size());
        ExecutorService executor = Executors.newFixedThreadPool(100);

        //여기서 100개 처리
        for (KisPeriodStockDto stockDto : stockDtos) {
            executor.submit(() -> {
                try {
                    LocalDate date = LocalDate.parse(stockDto.getStockDate(), DateTimeFormatter.ofPattern("yyyyMMdd")); // 포맷 다르면 formatter 지정
                    if (!stockHistoryRepository.existsByStockCodeAndTypeAndDate(stockCode, type, date)) {
                        StockHistory stockHistory = StockHistory.createHistory(stockCode, type, stockDto);
                        stockHistoryRepository.save(stockHistory);
                    }
                } catch (Exception e) {
                    log.error("병렬 처리 중 오류 발생", e);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executor.shutdown();
        if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
            log.warn("일부 쓰레드가 30초 내 종료되지 않았습니다.");
        }
    }

}
