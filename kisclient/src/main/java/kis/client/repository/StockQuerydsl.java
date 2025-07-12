package kis.client.repository;

import kis.client.dto.redis.StockInfoDto;
import kis.client.entity.Stock;

import java.util.List;
import java.util.Optional;

public interface StockQuerydsl {
    List<Stock> findStockOrderByIdDESC(int pageIndex, int pageSize);

    Optional<StockInfoDto> findStockInfoByStockCode(String s);
}
