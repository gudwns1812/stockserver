package stock.mainserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stock.mainserver.entity.Stock;

import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findByStockCode(String stockCode);
}
