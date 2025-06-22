package stock.mainserver.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import stock.mainserver.dto.redis.StockDto;
import stock.mainserver.dto.response.StockPeriodResponseDto;
import stock.mainserver.entity.Stock;
import stock.mainserver.entity.StockHistory;
import stock.mainserver.global.error.StockNotFoundException;
import stock.mainserver.repository.StockHistoryRepository;
import stock.mainserver.repository.StockRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final StockRepository stockRepository;
    private final StockHistoryRepository stockHistoryRepository;

    public StockDto getStockInfo(String stockCode) {
        String stockKey = "STOCK:" + stockCode;
        Object object = redisTemplate.opsForValue().get(stockKey);
        StockDto stockDto;
        if (object == null) {
            Stock stock = stockRepository.findByStockCode(stockCode).orElseThrow(() -> new StockNotFoundException("주식을 찾지 못했습니다."));
            stockDto = new StockDto(stock.getStockCode(), stock.getCategory(), stock.getPrice(), stock.getMarketName(),
                    stock.getChangeAmount(), stock.getSign(), stock.getChangeRate(), stock.getVolume(),
                    stock.getVolumeValue(), stock.getStockImage());
        } else {
            stockDto = objectMapper.convertValue(object, StockDto.class);
        }
        return stockDto;
    }

    public List<StockPeriodResponseDto> getStockPeriodInfo(String stockCode, String type) {
        List<StockHistory> list = stockHistoryRepository.findByStockCodeAndType(stockCode, type);
        return list.stream().map(StockPeriodResponseDto::new).toList();
    }
}
