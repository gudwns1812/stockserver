package stock.mainserver.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import stock.mainserver.dto.redis.StockDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "검색 응답 DTO")
public class SearchResponseDto {

    @Schema(description = "종목 이름")
    private String stockName;

    @Schema(description = "종목 코드")
    private String stockCode;

    @Schema(description = "현재가")
    private String currentPrice;

    @Schema(description = "전일 대비 상승/하락 표시")
    private String sign;

    @Schema(description = "전일 대비 상승/하락 금액")
    private String changeAmount;

    @Schema(description = "전일 대비 상승/하락 비율")
    private String changeRate;

    private String stockImage;

    public SearchResponseDto(StockDto stock) {
        stockName = stock.getStockName();
        stockCode = stock.getStockCode();
        currentPrice = stock.getPrice();
        sign = stock.getSign();
        changeAmount = stock.getChangeAmount();
        changeRate = stock.getChangeRate();
        stockImage = stock.getStockImage();
    }
}