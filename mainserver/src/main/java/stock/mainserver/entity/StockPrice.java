package stock.mainserver.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import stock.mainserver.global.auditing.BaseTimeEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockPrice extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id")
    private Stock stock;

    @Column(name = "stock_price")
    private String price;
    private String openPrice;
    private String highPrice;
    private String lowPrice;
    private String changePrice;
    private String changeRate;
    private String sign;

    public void updateStockPrice(String price, String changePrice, String sign, String changeRate) {
        this.price = price;
        this.changeRate = changeRate;
        this.changePrice = changePrice;
        this.sign = sign;
    }
}
