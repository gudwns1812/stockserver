package kis.client.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kis.client.dto.redis.StockDto;
import kis.client.entity.Stock;
import kis.client.repository.StockInit;
import kis.client.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RedisToDbBatchService {

    private final StockRepository stockRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final StockInit stockInit;
    private final ObjectMapper objectMapper;


    @Scheduled(cron = "0 0 16 * * 1-5")
    public void RedisToDb() {
        List<Stock> stocks = stockRepository.findStockOrderByIdDESC(stockInit.getPageIndex(),stockInit.getPageSize());
        for (Stock stock : stocks) {
            String stockCode = stock.getStockCode();
            Object object = redisTemplate.opsForValue().get("STOCK:" + stockCode);
            if (object == null) {
                continue;
            }
            StockDto stockDto = objectMapper.convertValue(object, StockDto.class);
            stock.updateStockPrice(stockDto.getPrice(),stockDto.getChangeAmount(),stockDto.getSign(),stockDto.getChangeRate());
        }
    }

}
