package kis.client.repository;

import kis.client.entity.Stock;

import java.util.List;

public interface StockQuerydsl {
    List<Stock> findStockOrderByIdDESC(int pageIndex, int pageSize);
}
