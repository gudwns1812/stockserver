//package kis.client.Service;
//
//import kis.client.entity.Stock;
//import kis.client.global.token.KisTokenManager;
//import kis.client.repository.StockInit;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.context.event.ApplicationReadyEvent;
//import org.springframework.context.event.EventListener;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class RefreshReactive {
//
//    private final GetStockReactiveWeb getStockReactiveWeb;
//    private final StockInit stockInit;
//    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(4);
//    private final KisTokenManager kisTokenManager;
//
//    @Scheduled(fixedRate = 25000)
//    public void refresh() {
//        List<Stock> stocks = stockInit.getStocks();
//        String token = kisTokenManager.getToken();
//        getStockReactiveWeb.getStockInfo(token, stocks);
//
//    }
//
//}
