package kis.client.repository;

import jakarta.annotation.PostConstruct;
import kis.client.entity.Stock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class StockInit {

    private final StockRepository stockRepository;
    private List<Stock> stocks = new ArrayList<>();

    @PostConstruct
    public void init() {
        List<Stock> findStocks = stockRepository.findStockOrderByIdDESC();
        stocks.addAll(findStocks);
        log.info("Stocks found: {}" , stocks.size());
    }

}
