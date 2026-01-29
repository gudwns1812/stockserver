package stock.mainserver.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import stock.mainserver.entity.StockHistory;
import stock.mainserver.global.vo.DailyPrice;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "주식 기간별 과거 데이터 응답 DTO")
public class StockPeriodResponseDto {
    @Schema(description = "주식 코드", example = "005930")
    private String stockCode;

    private LocalDate date;
    private String type;
    private DailyPrice price;
    private Long prevPrice;

    private Long openFromPrev;
    private Long closeFromPrev;
    private Long highFromPrev;
    private Long lowFromPrev;

    public StockPeriodResponseDto(StockHistory stockHistory) {
        stockCode = stockHistory.getStockCode();
        date = stockHistory.getDate();
        price = stockHistory.getPrice();
        prevPrice = price.getClose() - stockHistory.getPrevPrice();

        openFromPrev = price.getOpen() - prevPrice;
        closeFromPrev = stockHistory.getPrevPrice();
        highFromPrev = price.getHigh() - prevPrice;
        lowFromPrev = price.getLow() - prevPrice;
    }
}
