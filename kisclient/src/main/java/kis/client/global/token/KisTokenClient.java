package kis.client.global.token;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class KisTokenClient {

    private final RestClient kisClient;
    private final KisTokenProperties props;

    public KisTokenResponse fetchToken() {
        log.debug("KIS 토큰 발급 요청 중... URL: {}", props.getTokenUrl());

        Map<String, String> body = Map.of(
                "grant_type", props.getGrantType(),
                "appkey", props.getAppkey(),
                "appsecret", props.getAppsecret()
        );

        return kisClient.post()
                .uri(props.getTokenUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(KisTokenResponse.class);
    }

}
