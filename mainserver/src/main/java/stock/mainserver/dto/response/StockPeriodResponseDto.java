package stock.mainserver.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import stock.mainserver.entity.StockHistory;

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
    private String open;
    private String high;
    private String low;
    private String close;
    private String volume;
    private String volumeAmount;
    private Integer prevPrice;

    private Integer openFromPrev;
    private Integer closeFromPrev;
    private Integer highFromPrev;
    private Integer lowFromPrev;

    public StockPeriodResponseDto(StockHistory stockHistory) {
        stockCode = stockHistory.getStockCode();
        date = stockHistory.getDate();
        open = stockHistory.getOpen();
        high = stockHistory.getHigh();
        low = stockHistory.getLow();
        close = stockHistory.getClose();
        volume = stockHistory.getVolume();
        volumeAmount = stockHistory.getVolumeAmount();
        prevPrice = Integer.parseInt(close) - stockHistory.getPrevPrice();

        openFromPrev = Integer.parseInt(open) - prevPrice;
        closeFromPrev = stockHistory.getPrevPrice();
        highFromPrev = Integer.parseInt(high) - prevPrice;
        lowFromPrev = Integer.parseInt(low) - prevPrice;
    }
}
