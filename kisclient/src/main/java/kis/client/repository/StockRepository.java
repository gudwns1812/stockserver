package kis.client.repository;

import kis.client.entity.Stock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> , StockQuerydsl {

    Optional<Stock> findByStockCode(String stockCode);
    boolean existsByStockCode(String stockCode);

    @Query("SELECT s FROM Stock s")
    Page<Stock> findAll(Pageable pageable);


    List<Stock> findByPrice(String price);

    LocalDate findMinDateByStockCode(String stockCode);


}
