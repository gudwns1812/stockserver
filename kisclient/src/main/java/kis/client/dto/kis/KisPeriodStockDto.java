package kis.client.dto.kis;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KisPeriodStockDto {

    @JsonProperty("stck_bsop_date")
    private String stockDate;

    @JsonProperty("stck_clpr")
    private String stockPrice;

    @JsonProperty("stck_oprc")
    private String stockOpenPrice;

    @JsonProperty("stck_hgpr")
    private String stockHighPrice;

    @JsonProperty("stck_lwpr")
    private String stockLowPrice;

    @JsonProperty("acml_vol")
    private String accumulatedVolume;

    @JsonProperty("acml_tr_pbmn")
    private String accumulatedTradeAmount;

    @JsonProperty("prdy_vrss")
    private String previousPrice;

    @JsonProperty("prdy_vrss_sign")
    private String previousPriceSign;
}
