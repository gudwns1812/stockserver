package stock.mainserver.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stock.mainserver.dto.redis.StockDto;
import stock.mainserver.dto.response.*;
import stock.mainserver.entity.Stock;
import stock.mainserver.entity.StockHistory;
import stock.mainserver.global.error.StockNotFoundException;
import stock.mainserver.repository.StockHistoryRepository;
import stock.mainserver.repository.StockRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

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
        return findStockInfo(stockKey);
    }

    public List<StockPeriodResponseDto> getStockPeriodInfo(String stockCode, String type) {
        List<StockHistory> list = stockHistoryRepository.findByStockCodeAndType(stockCode, type);
        return list.stream().map(StockPeriodResponseDto::new).toList();
    }


    public List<CategoriesResponseDto> AllCategories() {
        return stockRepository.findCategoryAll().stream()
                .map(CategoriesResponseDto::new).toList();
    }

    public CategoryPageResponseDto CategoryStocks(String category, int page) {
        int pageSize = 10;
        int totalCount = stockRepository.countByCategory(category);
        int totalPage = (totalCount + pageSize - 1) / pageSize;
        List<String> stockCodes = stockRepository.findAllStockCodesByCategory(category);
        List<StockDto> stockList = stockCodes.stream()
                .map(this::findStockInfo)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(StockDto::getVolumeValue).reversed()) // 3. 내림차순 정렬
                .toList();

        // 4. 페이징
        int fromIndex = (page-1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, stockList.size());
        if (fromIndex >= stockList.size()) return null; // 범위 초과시 빈 리스트 반환

        List<StockDto> paged = stockList.subList(fromIndex, toIndex);
        List<CategoryStockResponseDto> list = paged.stream()
                .map(CategoryStockResponseDto::new)
                .toList();
        return new CategoryPageResponseDto(totalPage,list);
    }

    public List<SearchResponseDto> getSearchStock(String keyword) {
        if (keyword != null) {
            keyword = keyword.toUpperCase();
        }
        List<Stock> stocks = stockRepository.searchStock(keyword);
        if (keyword == null || keyword.isEmpty() || stocks.isEmpty()) {
            List<Stock> popularStocks = searchPopular();
            return popularStocks.stream()
                    .map(stock -> {
                        StockDto stockInfo = findStockInfo(stock.getStockCode());
                        return new SearchResponseDto(
                                stock.getName(),
                                stock.getStockCode(),
                                stockInfo.getPrice(),
                                stockInfo.getSign(),
                                stockInfo.getChangeAmount(),
                                stockInfo.getChangeRate(),
                                stock.getStockImage()
                        );
                    }).toList();
        } else {
            return stocks.stream()
                    .map(stock -> {
                        StockDto stockInfo;
                        try {
                            stockInfo = findStockInfo(stock.getStockCode());
                        } catch (StockNotFoundException e) {
                            log.warn("검색 결과 StockDto 없음: {}", stock.getStockCode());
                            return null; // 이후 null 필터링
                        }
                        return new SearchResponseDto(
                                stock.getName(),
                                stock.getStockCode(),
                                stockInfo.getPrice(),
                                stockInfo.getSign(),
                                stockInfo.getChangeAmount(),
                                stockInfo.getChangeRate(),
                                stock.getStockImage()
                        );
                    }).toList();
        }
    }

    private StockDto findStockInfo(String stockCode) {
        StockDto stockDto;
        Object object = redisTemplate.opsForValue().get(stockCode);
        if (object == null) {
            Stock stock = stockRepository.findByStockCode(stockCode).orElseThrow(() -> new StockNotFoundException("주식을 찾지 못했습니다."));
            stockDto = new StockDto(stock.getStockCode(),stock.getName(), stock.getCategory(), stock.getPrice(), stock.getMarketName(),
                    stock.getChangeAmount(), stock.getSign(), stock.getChangeRate(), stock.getVolume(),
                    stock.getVolumeValue(), stock.getStockImage());
        } else {
            stockDto = objectMapper.convertValue(object, StockDto.class);
        }
        return stockDto;
    }

    public List<Stock> searchPopular() {
        return stockRepository.findStockByStockSearchCount();
    }

    @Transactional
    public void stockSearchCounter(String stockCode) {
        stockRepository.findByStockCode(stockCode).ifPresent(Stock::incrementStockSearchCount);
    }
}
