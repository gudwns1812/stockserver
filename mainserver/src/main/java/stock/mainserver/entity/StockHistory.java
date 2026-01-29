package stock.mainserver.entity;

import jakarta.persistence.*;
import lombok.*;
import stock.mainserver.global.auditing.BaseTimeEntity;
import stock.mainserver.global.vo.DailyPrice;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class StockHistory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "stock_code", nullable = false)
    private String stockCode;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "date_type")
    private String type;

    @Embedded
    private DailyPrice price;

    @Column(name = "prev_price")
    private Long prevPrice;
}
