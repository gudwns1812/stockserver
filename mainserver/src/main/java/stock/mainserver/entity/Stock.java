package stock.mainserver.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import stock.mainserver.global.auditing.BaseTimeEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Stock extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_id")
    private Long id;

    private String stockCode;
    private String stockName;
    private String volume;
    private String volumeValue;
    private String marketName;
    private String stockImage;
    private Integer stockSearchCount;
    private String category;

    @OneToOne(mappedBy = "stock")
    private StockPrice stockPrice;

    //== 비즈니스 로직 ==//
    public void incrementStockSearchCount() {
        if (stockSearchCount == null) {
            stockSearchCount = 0;
        }
        stockSearchCount++;
    }
}
