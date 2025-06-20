package kis.client.repository;

import kis.client.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> , StockQuerydsl {

    Optional<Stock> findByStockCode(String stockCode);
}
