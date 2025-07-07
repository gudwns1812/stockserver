//package kis.client.repository;
//
//import com.google.common.util.concurrent.RateLimiter;
//import kis.client.Service.GetStockClient;
//import kis.client.dto.kis.KisPeriodStockDto;
//import kis.client.dto.kis.KisStockDto;
//import kis.client.entity.Stock;
//import kis.client.entity.StockHistory;
//import kis.client.global.token.KisTokenManager;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//import java.util.Optional;
//import java.util.concurrent.CountDownLatch;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//@Transactional
//public class HistoryInit implements CommandLineRunner {
//
//    private final StockHistoryRepository stockHistoryRepository;
//    private final StockInit stockInit;
//    private final GetStockClient getStockClient;
//    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;
//    private final KisTokenManager kisTokenManager;
//    private final StockRepository stockRepository;
//
//    @Override
//    public void run(String... args) throws Exception {
//        List<Stock> stocks = stockInit.getStocks();
//        String token = kisTokenManager.getToken();
//
//        final int batchSize = 20;
//        int total = stocks.size();
//        int batchCount = (int) Math.ceil((double) total / batchSize);
//
//        for (int batch = 0; batch < batchCount; batch++) {
//            int start = batch * batchSize;
//            int end = Math.min(start + batchSize, total);
//            List<Stock> batchStocks = stocks.subList(start, end);
//
//            for (Stock stock : batchStocks) {
//                    try {
//                        LocalDate today = LocalDate.now();
//                        LocalDate yesterday = LocalDate.now().minusDays(1);
//
//                        Optional<StockHistory> historyOptional =
//                                stockHistoryRepository.findByStockCodeAndTypeAndDate(stock.getStockCode(), "D", yesterday);
//                        KisStockDto stockInfo = getStockClient.getStockInfo(token, stock.getStockCode());
//                        if (historyOptional.isEmpty()) {
//                            log.info("D타입 히스토리 저장 시작: {}", stock.getStockCode());
//                            saveStock(stock, yesterday, token, stockInfo);
//                        }
//
//                        log.info("✅ D타입 히스토리 저장 완료: {}", stock.getStockCode());
//                    } catch (Exception e) {
//                        log.error("🚨 오류 발생: {}", stock.getStockCode(), e);
//                    }
//            }
//
//            Thread.sleep(1000);
//            log.info("▶️ Batch {} 처리 완료 ({}/{})", batch + 1, end, total);
//        }
//
//        log.info("🎉 모든 데이터 저장 완료");
//    }
//
//
//    private void saveStock(Stock stock, LocalDate today, String token , KisStockDto stockInfo) {
//        int prevPrice = Integer.parseInt(stockInfo.getPrice()) - Integer.parseInt(stockInfo.getChangeAmount());
//        KisPeriodStockDto dto = new KisPeriodStockDto(today.format(DateTimeFormatter.ofPattern("yyyyMMdd")),
//                stockInfo.getPrice(),stockInfo.getOpenPrice(),stockInfo.getHighPrice(),stockInfo.getLowPrice(),stockInfo.getVolume(), stockInfo.getVolumeValue(),
//                String.valueOf(prevPrice) , stockInfo.getSign());
//        StockHistory newHistory = StockHistory.createHistory(stock.getStockCode(), "D", dto);
//        stockHistoryRepository.save(newHistory);
//    }
//
//    public void updateStock(Stock stock, LocalDate today, String token, KisStockDto stockInfo) {
//        stock.updateStockPrice(stockInfo.getPrice(), stockInfo.getOpenPrice(), stockInfo.getHighPrice(),
//                stockInfo.getLowPrice(), stockInfo.getChangeAmount(), stockInfo.getSign(),
//                stockInfo.getChangeRate(), stockInfo.getVolume(), stockInfo.getVolumeValue());
//        stockRepository.save(stock);
//    }
//
//}
