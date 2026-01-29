package stock.mainserver.service.init;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import stock.mainserver.entity.Stock;
import stock.mainserver.repository.StockRepository;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class StockInit {

    private final RedisTemplate<String, Object> redisTemplate;
    private final StockRepository stockRepository;
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        List<Stock> stocks = stockRepository.findAll();
        log.info("Stocks found: {}", stocks.size());
        saveStockInfosToRedis(stocks, 0);
    }

    private void saveStockInfosToRedis(List<Stock> stocks, int index) {
        for (Stock stock : stocks) {
            String stockKey = "STOCK_INFO:" + stock.getStockCode();
            String stockImage = stock.getStockImage() == null ? "" : stock.getStockImage();
            Map<String, String> stockInfo = Map.of(
                    "stockCode", stock.getStockCode(),
                    "stockName", stock.getStockName(),
                    "stockImage", stockImage
            );
            redisTemplate.opsForValue().set(stockKey, stockInfo);
            redisTemplate.opsForSet().add("Client_ID:" + index, stock.getStockCode());
        }
    }

}
