package kis.client.dto.kis;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "KIS 지수 가격 정보 DTO")
public class KisIndicePriceDto {

    @JsonProperty("stck_bsop_date")
    @Schema(description = "거래일자", example = "20231001")
    private String indice_date;

    @JsonProperty("bstp_nmix_prpr")
    @Schema(description = "지수 가격", example = "3000.00")
    private String cur_price;

    @JsonProperty("bstp_nmix_hgpr")
    @Schema(description = "지수 고가", example = "3100.00")
    private String high_price;

    @JsonProperty("bstp_nmix_lwpr")
    @Schema(description = "지수 저가", example = "2900.00")
    private String low_price;

    @JsonProperty("acml_vol")
    @Schema(description = "거래량", example = "1500000")
    private String acml_vol;

    @JsonProperty("acml_tr_pbmn")
    @Schema(description = "거래대금", example = "4500000000")
    private String acml_vol_price;
}
