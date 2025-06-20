package kis.client.dto.kis;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KisIndiceInfoDto {
    @JsonProperty("bstp_nmix_prdy_vrss")
    private String prev;

    @JsonProperty("prdy_vrss_sign")
    private String sign;

    @JsonProperty("bstp_nmix_prdy_ctrt")
    private String prev_rate;
}
