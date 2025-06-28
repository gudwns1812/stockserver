//package kis.client.repository;
//
//import kis.client.Service.GetStockClient;
//import kis.client.dto.kis.KisPeriodStockDto;
//import kis.client.dto.kis.KisStockDto;
//import kis.client.entity.Stock;
//import kis.client.entity.StockHistory;
//import kis.client.global.token.KisTokenManager;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.cglib.core.Local;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//import java.util.Optional;
//import java.util.concurrent.atomic.AtomicInteger;
//
//@Component
//@Slf4j
//@RequiredArgsConstructor
//public class test implements CommandLineRunner {
//
//    private final StockInit stockInit;
//    private final StockHistoryRepository stockHistoryRepository;
//    private final StockRepository stockRepository;
//    private final GetStockClient getStockClient;
//    private final KisTokenManager kisTokenManager;
//    private final AtomicInteger counter = new AtomicInteger(0);
//    @Override
//    @Transactional
//    public void run(String... args) throws Exception {
//        List<Stock> stocks = stockInit.getStocks();
//        LocalDate today = LocalDate.now().minusDays(1);
//        LocalDate yesterday = today.minusDays(1);
//        for (Stock stock : stocks) {
//            KisStockDto stockInfo = getStockClient.getStockInfo(kisTokenManager.getToken(), stock.getStockCode());
//            int count = counter.incrementAndGet();
//            if (count % 20 == 0) {
//                Thread.sleep(1000);
//            }
//            if (stockInfo == null) {
//                log.warn("주식 정보가 없습니다. stockCode: {}", stock.getStockCode());
//
////                // StockHistory 삭제
////                int deletedHistoryCount = stockHistoryRepository.deleteByStockCode(stock.getStockCode());
////                log.info("삭제된 StockHistory 개수: {}", deletedHistoryCount);
////
////                // Stock 삭제
////                stockRepository.delete(stock); // stockInit에서 repository를 노출하거나 직접 주입
////                log.info("삭제된 Stock: {}", stock.getStockCode());
//
//                continue;
//            }
//            String type = "D";
//            String prevClose = stockHistoryRepository
//                    .findByStockCodeAndTypeAndDate(stock.getStockCode(), type, yesterday)
//                    .map(StockHistory::getClose)
//                    .orElse("0");
//
//            Optional<StockHistory> stockHistory = stockHistoryRepository.findByStockCodeAndTypeAndDate(stock.getStockCode(), type, today);
//            if (stockHistory.isEmpty()) {
//                StockHistory newStockToday = StockHistory.createHistory(
//                        stock.getStockCode(),
//                        type,
//                        new KisPeriodStockDto(
//                                today.format(DateTimeFormatter.ofPattern("yyyyMMdd")),
//                                stockInfo.getPrice(),
//                                stockInfo.getOpenPrice(),
//                                stockInfo.getHighPrice(),
//                                stockInfo.getLowPrice(),
//                                stockInfo.getVolume(),
//                                stockInfo.getVolumeValue(),
//                                prevClose,
//                                null
//                        )
//                );
//                stockHistoryRepository.save(newStockToday);
//                log.info("신규 일간 데이터 저장: {}", stock.getStockCode());
//                continue;
//            }
//            StockHistory stockHistoryToday = stockHistory.get();
//            stockHistoryToday.updateHistory(type,stockInfo.getOpenPrice(),
//                    stockInfo.getHighPrice(), stockInfo.getLowPrice(),
//                    stockInfo.getPrice(), stockInfo.getVolume(),
//                    stockInfo.getVolumeValue(), Integer.parseInt(prevClose));
//            log.info("기존 일간 데이터 업데이트: {}", stock.getStockCode());
//        }
//    }
//}
