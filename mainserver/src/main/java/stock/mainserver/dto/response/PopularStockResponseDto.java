package stock.mainserver.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import stock.mainserver.dto.redis.KisPopularRedisDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "인기 종목 응답 DTO")
public class PopularStockResponseDto {

    @Schema(description = "종목명", example = "삼성전자")
    private String stockName;

    @Schema(description = "종목 코드", example = "005930")
    private String stockCode;

    @Schema(description = "인기 순위", example = "1")
    private String rank;

    @Schema(description = "주식 현재가", example = "59000")
    private String price;

    @Schema(description = "전일 대비 상승/하락 표시", example = "+")
    private String sign;

    @Schema(description = "전일 대비 상승/하락 금액", example = "500")
    private String changeAmount;

    @Schema(description = "전일 대비 상승/하락 비율", example = "0.5")
    private String changeRate;

    private String stockImage;

    public PopularStockResponseDto(KisPopularRedisDto dto) {
        stockName = dto.getStockName();
        stockCode = dto.getStockCode();
        rank = dto.getRank();
        price = dto.getPrice();
        sign = dto.getSign();
        changeAmount = dto.getChangeAmount();
        changeRate = dto.getChangeRate();
        stockImage = dto.getStockImage();
    }
}
