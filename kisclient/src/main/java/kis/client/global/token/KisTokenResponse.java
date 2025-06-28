package kis.client.global.token;

import lombok.Data;

@Data
public class KisTokenResponse {
    private String access_token;
    private String token_type;
    private Integer expires_in;
    private String access_token_token_expired;
}
