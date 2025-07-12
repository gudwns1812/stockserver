package kis.client.repository;

import kis.client.entity.StockHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface StockHistoryRepository extends JpaRepository<StockHistory, Long> {

    boolean existsByStockCodeAndTypeAndDate(String stockCode, String type, LocalDate parse);

    Optional<StockHistory> findByStockCodeAndTypeAndDate(String stockCode, String type, LocalDate date);

    int deleteByStockCode(String stockCode);

    @Query("SELECT MIN(sh.date) FROM StockHistory sh WHERE sh.stockCode = :stockCode")
    Optional<LocalDate> findEarliestDateByStockCode(@Param("stockCode") String stockCode);

    Optional<StockHistory> findByStockCodeAndTypeAndDateBetween(String code, String type, LocalDate start, LocalDate today);
}
