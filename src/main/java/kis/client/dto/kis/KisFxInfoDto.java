package kis.client.dto.kis;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KisFxInfoDto {

    @JsonProperty("ovrs_nmix_prdy_vrss")
    private String changePrice;

    @JsonProperty("prdy_vrss_sign")
    private String changeSign;

    @JsonProperty("prdy_ctrt")
    private String changeRate;

    @JsonProperty("ovrs_nmix_prdy_clpr")
    private String prevPrice;

    @JsonProperty("ovrs_prod_hgpr")
    private String highPrice;

    @JsonProperty("ovrs_prod_lwpr")
    private String lowPrice;

    @JsonProperty("ovrs_prod_oprc")
    private String openPrice;

    @JsonProperty("ovrs_nmix_prpr")
    private String currentPrice;

}
