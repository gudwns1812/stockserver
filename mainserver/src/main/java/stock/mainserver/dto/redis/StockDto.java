package stock.mainserver.dto.redis;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "주식 현재 실시간 정보 조회")
public class StockDto {
    @Schema(description = "주식 코드")
    private String stockCode;
    @Schema(description = "주식 한글명")
    private String stockName;
    @Schema(description = "주식 카테고리")
    private String categoryName;
    @Schema(description = "주식 현재가(종가)")
    private String price;
    @Schema(description = "주식이 속해있는 시장")
    private String marketName;
    @Schema(description = "주식 변화값(전날 종가 대비)")
    private String changeAmount;
    @Schema(description = "주식 변화 부호")
    private String sign;
    @Schema(description = "주식 변화량")
    private String changeRate;
    @Schema(description = "주식 거래량")
    private String volume;
    @Schema(description = "주식 거래대금")
    private String volumeValue;
    @Schema(description = "주식 이미지")
    private String stockImage;
}
