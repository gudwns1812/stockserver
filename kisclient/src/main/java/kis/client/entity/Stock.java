package kis.client.entity;

import jakarta.persistence.*;
import kis.client.global.auditing.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_id")
    private Long id;

    @Column(name = "stock_code")
    private String stockCode;

    @Column(name = "stock_name")
    private String name;

    @Column(name = "stock_price")
    private String price;

    @Column(name = "open_price")
    private String openPrice;

    @Column(name = "high_price")
    private String highPrice;

    @Column(name = "low_price")
    private String lowPrice;

    @Column(name = "market_name")
    private String marketName;

    @Column(name = "stock_image")
    private String stockImage;

    @Column(name = "change_price")
    private String changeAmount;

    @Column(name = "sign")
    private String sign;

    @Column(name = "change_rate")
    private String changeRate;

    private String volume;

    @Column(name = "volume_value")
    private String volumeValue;

    @Column(name = "stock_search_count")
    private Integer stockSearchCount;

    private String category;

    //== 생성 메서드 ==//
    public static Stock createStock(String stockCode, String name, Integer price, String marketName, String category) {
        Stock stock = new Stock();
        stock.stockCode = stockCode;
        stock.name = name;
        stock.marketName = marketName;
        stock.category = category;
        stock.stockSearchCount = 0; // 초기 검색 횟수는 0으로 설정
        return stock;
    }


    //== 비즈니스 로직 ==//
    public void incrementStockSearchCount() {
        if (stockSearchCount == null) {
            stockSearchCount = 0;
        }
        stockSearchCount++;
    }

    public void updateStockPrice(String price, String openPrice, String highPrice, String lowPrice, String changePrice, String sign, String changeRate , String volume, String volumeValue) {
        this.openPrice = openPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.price = price;
        this.changeRate = changeRate.contains(".")
                ? changeRate.substring(0, Math.min(changeRate.indexOf(".") + 3, changeRate.length()))
                : changeRate;
        this.changeAmount = changePrice;
        this.sign = sign;
        this.volume = volume;
        this.volumeValue = volumeValue;
    }
}
