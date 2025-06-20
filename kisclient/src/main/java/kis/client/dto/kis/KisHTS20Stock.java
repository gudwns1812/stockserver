package kis.client.dto.kis;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class KisHTS20Stock {

    @JsonProperty("mrkt_div_cls_code")
    private String marketCode;

    @JsonProperty("mksc_shrn_iscd")
    private String stockCode;
}
