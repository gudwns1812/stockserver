package kis.client.repository;

import jakarta.annotation.PostConstruct;
import kis.client.entity.Stock;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Getter
public class StockInit {

    private final StockRepository stockRepository;
    private final List<Stock> stocks = new ArrayList<>();
    private int pageIndex;
    private final int pageSize = 1000;

    @PostConstruct
    public void init() {
        String clientId = System.getenv("KIS_CLIENT_ID");
        if (clientId != null) {
             pageIndex = Integer.parseInt(clientId.split("-")[1]);
        }
        log.info("클라이언트 아이디 : {} 클라이언트 번호 : {}" , clientId , pageIndex);
        List<Stock> findStocks = stockRepository.findStockOrderByIdDESC(pageIndex, pageSize);
        stocks.addAll(findStocks);
        log.info("Stocks found: {}" , stocks.size());
    }



}
