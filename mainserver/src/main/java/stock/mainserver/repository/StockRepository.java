package stock.mainserver.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import stock.mainserver.entity.Stock;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> , StockSearchRepository{
    Optional<Stock> findByStockCode(String stockCode);

    @Query("SELECT DISTINCT s.category FROM Stock s order by s.category")
    List<String> findCategoryAll();

    @Query("SELECT s.stockCode FROM Stock s where s.category = :category")
    List<String> findAllStockCodesByCategory(String category);

    Integer countByCategory(String category);

    @Query("SELECT s FROM Stock s order by s.stockSearchCount DESC LIMIT 5")
    List<Stock> findStockByStockSearchCount();

    Page<Stock> findByCategory(String category, Pageable pageable);

    List<Stock> findByCategory(String categoryName);

    @Query(
            value = "SELECT * FROM stock WHERE category = :category ORDER BY CAST(REPLACE(volume, ',', '') AS UNSIGNED) DESC",
            countQuery = "SELECT COUNT(*) FROM stock WHERE category = :category",
            nativeQuery = true
    )
    Page<Stock> findByCategoryOrderByVolumeAsNumberDesc(@Param("category") String category, Pageable pageable);

//    @Query("SELECT s FROM Stock s WHERE s.category = :category")
//    Page<Stock> findStockCodeByCategory(@Param("category") String category, Pageable pageable);
}
