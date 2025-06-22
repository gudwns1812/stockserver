package kis.client.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kis.client.entity.Stock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static kis.client.entity.QStock.stock;

@Repository
@RequiredArgsConstructor
public class StockQuerydslImpl implements StockQuerydsl {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Stock> findStockOrderByIdDESC(int pageIndex, int pageSize) {
        return queryFactory
                .selectFrom(stock)
                .orderBy(stock.id.desc())
                .limit(650)
                .fetch();
    }
}
