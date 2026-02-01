package stock.mainserver.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stock.mainserver.dto.redis.StockDto;
import stock.mainserver.dto.response.CategoryPageResponseDto;
import stock.mainserver.dto.response.CategoryStockResponseDto;
import stock.mainserver.dto.response.SearchResponseDto;
import stock.mainserver.dto.response.StockPeriodResponseDto;
import stock.mainserver.entity.Stock;
import stock.mainserver.entity.StockHistory;
import stock.mainserver.global.error.StockNotFoundException;
import stock.mainserver.repository.StockHistoryRepository;
import stock.mainserver.repository.StockRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        if (object == null) {
            Stock stock = stockRepository.findByStockCode(stockCode).orElseThrow(() -> new StockNotFoundException("주식 코드가 존재하지 않습니다: " + stockCode));
            return new StockDto(stock);
        }
        return objectMapper.convertValue(object, StockDto.class);
    }

    public List<StockPeriodResponseDto> getStockPeriodInfo(String stockCode, String type, LocalDate startDate, LocalDate endDate) {
        List<StockHistory> stockHistories = stockHistoryRepository.findByStockCodeAndTypeAndDateBetween(stockCode, type, startDate, endDate);
        return stockHistories.stream()
                .map(StockPeriodResponseDto::new)
                .toList();
    }


    @Transactional
    public void stockSearchCounter(String stockCode) {
        stockRepository.findByStockCode(stockCode).ifPresent(Stock::incrementStockSearchCount);
    }

    public List<String> getAllCategories() {
        return stockRepository.findCategoryAll();
    }

    public CategoryPageResponseDto CategoryStocks(String categoryName, int page) {
        Pageable pageable = PageRequest.of(page - 1, 10);
        Page<Stock> pageStock = stockRepository.findByCategoryOrderByVolume(categoryName, pageable);

        List<CategoryStockResponseDto> list = pageStock.stream()
                .map(s -> new CategoryStockResponseDto(getStockInfo(s.getStockCode()))
                ).toList();

        return new CategoryPageResponseDto(pageStock.getTotalPages(), list);
    }

    public List<SearchResponseDto> getSearchStock(String keyword) {
        List<Stock> stocks = stockRepository.searchStock(keyword);

        List<SearchResponseDto> result = new ArrayList<>();
        for (Stock stock : stocks) {
            StockDto stockInfo = getStockInfo(stock.getStockCode());
            result.add(new SearchResponseDto(stockInfo));
        }
        return List.copyOf(result);
    }
}
