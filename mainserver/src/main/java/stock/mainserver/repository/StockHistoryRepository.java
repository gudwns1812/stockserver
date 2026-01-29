package stock.mainserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stock.mainserver.entity.StockHistory;

import java.time.LocalDate;
import java.util.List;

public interface StockHistoryRepository extends JpaRepository<StockHistory, Long> {
    List<StockHistory> findByStockCodeAndTypeAndDateBetween(String stockCode, String type, LocalDate dateAfter, LocalDate dateBefore);
}
