package stock.mainserver.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import stock.mainserver.entity.Stock;

import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> , StockSearchRepository{
    @Query("select s from Stock s join fetch s.stockPrice where s.stockCode = :stockCode")
    Optional<Stock> findByStockCode(@Param("stockCode") String stockCode);

    @Query("SELECT DISTINCT s.category FROM Stock s order by s.category")
    List<String> findCategoryAll();

    @Query("select s from Stock s where s.category = :category order by s.volume desc")
    Page<Stock> findByCategoryOrderByVolume(@Param("category") String category, Pageable pageable);
}
