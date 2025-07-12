package kis.client.Service.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import kis.client.dto.redis.StockDto;
import kis.client.dto.redis.StockInfoDto;
import kis.client.entity.Holiday;
import kis.client.entity.Stock;
import kis.client.repository.StockInit;
import kis.client.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RedisToDbBatchService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final StockInit stockInit;
    private final ObjectMapper objectMapper;
    private final StockRepository stockRepository;

    @Scheduled(cron = "0 10 16 * * 1-5")
    public void RedisToDb() {
        if (Holiday.isContain(LocalDate.now())) return; // 공휴일이면 실행 안 함
        List<StockInfoDto> stocks = stockInit.getStocks();
        for (StockInfoDto stock : stocks) {
            String stockCode = stock.getStockCode();
            Object object = redisTemplate.opsForValue().get("STOCK:" + stockCode);
            if (object == null) {
                continue;
            }
            Stock updateStock = stockRepository.findByStockCode(stockCode).orElse(null);
            if (updateStock == null) {
                continue;
            }
            StockDto stockDto = objectMapper.convertValue(object, StockDto.class);
            updateStock.updateStockPrice(stockDto.getPrice(),stockDto.getOpenPrice(),stockDto.getHighPrice(),stockDto.getLowPrice(),
                    stockDto.getChangeAmount(),stockDto.getSign(),stockDto.getChangeRate(),stockDto.getVolume(),stockDto.getVolumeValue());
        }
    }

}
