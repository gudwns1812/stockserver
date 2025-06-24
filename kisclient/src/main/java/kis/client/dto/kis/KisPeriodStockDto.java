package kis.client.dto.kis;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "KIS 기간 주식 DTO")
public class KisPeriodStockDto {

    @JsonProperty("stck_bsop_date")
    @Schema(description = "주식 날짜", example = "20230330")
    private String stockDate;

    @JsonProperty("stck_clpr")
    @Schema(description = "주식 가격", example = "12345")
    private String stockPrice;

    @JsonProperty("stck_oprc")
    @Schema(description = "주식 시가", example = "12000")
    private String stockOpenPrice;

    @JsonProperty("stck_hgpr")
    @Schema(description = "주식 고가", example = "12500")
    private String stockHighPrice;

    @JsonProperty("stck_lwpr")
    @Schema(description = "주식 저가", example = "11900")
    private String stockLowPrice;

    @JsonProperty("acml_vol")
    @Schema(description = "누적 거래량", example = "1000000")
    private String accumulatedVolume;

    @JsonProperty("acml_tr_pbmn")
    @Schema(description = "누적 거래대금", example = "5000000000")
    private String accumulatedTradeAmount;

    @JsonProperty("prdy_vrss")
    @Schema(description = "주식 전일 종가", example = "12200")
    private String previousPrice;

    @JsonProperty("prdy_vrss_sign")
    @Schema(description = "전일 종가 부호", example = "+")
    private String previousPriceSign;
}
