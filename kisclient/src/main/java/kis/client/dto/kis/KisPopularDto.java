package kis.client.dto.kis;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "KIS 인기 종목 가격 정보 DTO")
public class KisPopularDto {

    @JsonProperty("hts_kor_isnm")
    @Schema(description = "종목명", example = "삼성전자")
    private String stockName;

    @JsonProperty("mksc_shrn_iscd")
    @Schema(description = "종목 코드", example = "005930")
    private String marketCode;

    @JsonProperty("data_rank")
    @Schema(description = "인기 순위", example = "1")
    private String rank;

    @JsonProperty("stck_prpr")
    @Schema(description = "주식 현재가", example = "59000")
    private String price;

    @JsonProperty("prdy_vrss_sign")
    @Schema(description = "전일 대비 상승/하락 표시", example = "+")
    private String sign;

    @JsonProperty("prdy_vrss")
    @Schema(description = "전일 대비 상승/하락 금액", example = "500")
    private String changeAmount;

    @JsonProperty("prdy_ctrt")
    @Schema(description = "전일 대비 상승/하락 비율", example = "0.5")
    private String changeRate;

    private String stockImage;


}
