package stock.mainserver.dto.redis;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "과거 가격 이력 응답 DTO")
public class KisFxPastInfoDto {

    @JsonProperty("stck_bsop_date")
    @Schema(description = "날짜")
    private String date;

    @JsonProperty("ovrs_nmix_prpr")
    @Schema(description = "현재가")
    private String currentPrice;

    @JsonProperty("ovrs_nmix_oprc")
    @Schema(description = "시가")
    private String openPrice;

    @JsonProperty("ovrs_nmix_hgpr")
    @Schema(description = "고가")
    private String highPrice;

    @JsonProperty("ovrs_nmix_lwpr")
    @Schema(description = "저가")
    private String lowPrice;
}
