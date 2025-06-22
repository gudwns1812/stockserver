package kis.client.repository;

import kis.client.entity.StockHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface StockHistoryRepository extends JpaRepository<StockHistory, Long> {

    boolean existsByStockCodeAndTypeAndDate(String stockCode, String type, LocalDate parse);
}
