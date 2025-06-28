package stock.mainserver.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import stock.mainserver.entity.Stock;

import java.util.List;

import static stock.mainserver.entity.QStock.stock;


@Repository
@RequiredArgsConstructor
public class StockSearchRepositoryImpl implements StockSearchRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Stock> searchStock(String query) {
        BooleanExpression condition;
        if (query.matches("\\d+")) {
            // 숫자: 주식 코드 검색
            condition = stock.stockCode.contains(query);
        } else {
            // 문자: 주식 이름 검색
            condition = stock.name.contains(query);
        }
        return queryFactory
                .selectFrom(stock)
                .where(condition)
                .orderBy(stock.stockSearchCount.desc())
                .limit(5)
                .fetch();
    }
}
