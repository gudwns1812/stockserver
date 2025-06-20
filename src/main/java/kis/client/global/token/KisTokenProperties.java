package kis.client.global.token;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "kis")
@Getter @Setter
public class KisTokenProperties {
    private String clientId;
    private String appkey;
    private String appsecret;
    private String grantType;
    private String tokenUrl;
}
