package kis.client.repository;

import kis.client.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {
    @Query("SELECT s FROM Stock s ORDER BY s.id DESC LIMIT 650")
    List<Stock> findStockOrderByIdDESC(Long id);

    Optional<Stock> findByStockCode(String stockCode);
}
