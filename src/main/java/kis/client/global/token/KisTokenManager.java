package kis.client.global.token;

import kis.client.global.error.TokenFetchException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class KisTokenManager {

    private final KisTokenClient kisTokenClient;
    private final KisTokenProperties kisTokenProperties;
    private final RedisTemplate<String, String> redisTemplate;

    public String getToken() {
        String clientId = kisTokenProperties.getClientId();
        String clientKey = "KIS_TOKEN::" + clientId;
        try {
            String token = redisTemplate.opsForValue().get(clientId);
            if (token != null) {
                log.info("기존 토큰이 있습니다.");
                return token;
            }

            log.info("새 토큰을 발급합니다.");
            KisTokenResponse response = kisTokenClient.fetchToken();
            String newToken = response.getAccess_token();
            int expiresIn = response.getExpires_in();
            long ttl = Math.max(expiresIn - 60, 30);

            redisTemplate.opsForValue().set(clientKey, newToken, Duration.ofSeconds(ttl));
            return newToken;

        } catch (Exception e) {
            log.error("KIS 토큰 발급 실패", e);
            throw new TokenFetchException("KIS 토큰 발급 실패", e);
        }

    }

}

