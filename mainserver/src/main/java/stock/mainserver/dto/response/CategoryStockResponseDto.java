package stock.mainserver.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import stock.mainserver.dto.redis.StockDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "카테고리별 주식 응답 DTO")
public class CategoryStockResponseDto {

    @Schema(description = "주식 이름")
    private String stockName;

    @Schema(description = "주식 코드")
    private String stockCode;

    @Schema(description = "주식 현재가")
    private String currentPrice;

    @Schema(description = "주식 전일 대비")
    private String changeRate;

    @Schema(description = "주식 전일 대비 기호")
    private String sign;

    @Schema(description = "주식 전일 대비 가격")
    private String changeAmount;

    private String stockImage;

    public CategoryStockResponseDto(StockDto stockDto) {
        stockName = stockDto.getStockName();
        stockCode = stockDto.getStockCode();
        currentPrice = stockDto.getPrice();
        changeRate = stockDto.getChangeRate();
        sign = stockDto.getSign();
        changeAmount = stockDto.getChangeAmount();
        stockImage = stockDto.getStockImage();
    }
}