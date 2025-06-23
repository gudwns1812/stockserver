package stock.mainserver.repository;

import stock.mainserver.entity.Stock;

import java.util.List;

public interface StockSearchRepository {
    List<Stock> searchStock(String query);
}
