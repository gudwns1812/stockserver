package kis.client.entity;

import jakarta.persistence.*;
import kis.client.dto.kis.KisPeriodStockDto;
import kis.client.global.auditing.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
@Getter
@Table(name = "stock_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockHistory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "stock_code", nullable = false)
    private String stockCode;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "data_type")
    private String type;

    @Column(name = "open_price")
    private String open;

    @Column(name = "high_price")
    private String high;

    @Column(name = "low_price")
    private String low;

    @Column(name = "close_price")
    private String close;

    @Column(name = "volume")
    private String volume;

    @Column(name = "volume_amount")
    private String volumeAmount;


    @Column(name = "prev_price")
    private Integer prevPrice;

    //== 생성 메서드 ==//
    public static StockHistory createHistory(String stockCode, String type, KisPeriodStockDto dto) {
        StockHistory history = new StockHistory();
        history.stockCode = stockCode;
        history.date = LocalDate.parse(dto.getStockDate(), DateTimeFormatter.ofPattern("yyyyMMdd"));
        history.type = type;
        history.open = dto.getStockOpenPrice();
        history.high = dto.getStockHighPrice();
        history.low = dto.getStockLowPrice();
        history.close = dto.getStockPrice();
        history.volume = dto.getAccumulatedVolume();
        history.volumeAmount = dto.getAccumulatedTradeAmount();
        history.prevPrice = Integer.parseInt(dto.getStockPrice()) - Integer.parseInt(dto.getPreviousPrice());
        return history;
    }

    //== 비즈니스 메서드 ==//
    public void updateHistory(String type, String open, String high, String low, String close, String volume, String volumeAmount, Integer prevPrice) {
        this.type = type;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
        this.volumeAmount = volumeAmount;
        this.prevPrice = prevPrice;
    }

}
