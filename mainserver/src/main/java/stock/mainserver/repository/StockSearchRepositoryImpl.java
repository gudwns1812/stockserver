package stock.mainserver.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import stock.mainserver.entity.Stock;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static stock.mainserver.entity.QStock.stock;


@Repository
@RequiredArgsConstructor
public class StockSearchRepositoryImpl implements StockSearchRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Stock> searchStock(String query) {
        if (query == null || query.isEmpty()) {
            return queryFactory
                    .selectFrom(stock)
                    .orderBy(stock.stockSearchCount.desc())
                    .limit(5)
                    .fetch();
        }

        boolean isNumeric = query.matches("\\d+");
        StringPath field = isNumeric ? stock.stockCode : stock.stockName;

        Set<Long> seenIds = new HashSet<>();
        List<Stock> result = new ArrayList<>();

        // 1. 정확 일치
        List<Stock> exactMatches = queryFactory
                .selectFrom(stock)
                .where(field.eq(query))
                .orderBy(castVolumeDesc(), stock.stockSearchCount.desc())
                .limit(5)
                .fetch();

        for (Stock s : exactMatches) {
            if (seenIds.add(s.getId())) result.add(s);
        }

        if (result.size() < 5) {
            // 2. startsWith
            List<Stock> startsWithMatches = queryFactory
                    .selectFrom(stock)
                    .where(field.startsWith(query).and(field.ne(query))) // 중복 제거
                    .orderBy(castVolumeDesc(), stock.stockSearchCount.desc())
                    .limit(5 - result.size())
                    .fetch();

            for (Stock s : startsWithMatches) {
                if (seenIds.add(s.getId())) result.add(s);
            }
        }

        if (result.size() < 5) {
            // 3. contains
            List<Stock> containsMatches = queryFactory
                    .selectFrom(stock)
                    .where(field.contains(query)
                            .and(field.ne(query)) // 정확 일치 제외
                            .and(field.startsWith(query).not())) // startsWith 제외
                    .orderBy(castVolumeDesc(), stock.stockSearchCount.desc())
                    .limit(5 - result.size())
                    .fetch();

            for (Stock s : containsMatches) {
                if (seenIds.add(s.getId())) result.add(s);
            }
        }

        return result;
    }

    private OrderSpecifier<Long> castVolumeDesc() {
        return stock.volumeValue.castToNum(Long.class).desc();
    }
}
