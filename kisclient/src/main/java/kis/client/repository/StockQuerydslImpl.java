package kis.client.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kis.client.dto.redis.StockInfoDto;
import kis.client.entity.Stock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static kis.client.entity.QStock.stock;

@Repository
@RequiredArgsConstructor
public class StockQuerydslImpl implements StockQuerydsl {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Stock> findStockOrderByIdDESC(int pageIndex, int pageSize) {
        return queryFactory
                .selectFrom(stock)
                .offset((long) pageIndex * pageSize)
                .orderBy(stock.id.asc())
                .limit(pageSize)
                .fetch();
    }

    @Override
    public Optional<StockInfoDto> findStockInfoByStockCode(String s) {
        Tuple tuple = queryFactory.select(stock.stockCode, stock.stockImage, stock.name)
                .from(stock)
                .where(stock.stockCode.eq(s))
                .fetchOne();
        if (tuple == null) {
            return null;
        }
        return Optional.of(new StockInfoDto(
                tuple.get(stock.stockCode),
                tuple.get(stock.stockImage),
                tuple.get(stock.name)
        ));
    }


}
