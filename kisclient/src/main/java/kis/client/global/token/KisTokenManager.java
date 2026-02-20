package kis.client.global.token;

import kis.client.global.error.TokenFetchException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
@Slf4j
public class KisTokenManager {

    private static final String TOKEN_KEY_PREFIX = "KIS_TOKEN::";
    private static final long SAFE_DURATION = 60 * 60;

    private final KisTokenClient kisTokenClient;
    private final KisTokenProperties kisTokenProperties;
    private final RedisTemplate<String, String> redisTemplate;

    public KisTokenManager(
            KisTokenClient tokenClient,
            KisTokenProperties properties,
            @Qualifier("tokenRedisTemplate") RedisTemplate<String, String> template
    ) {
        this.kisTokenClient = tokenClient;
        this.kisTokenProperties = properties;
        this.redisTemplate = template;
    }

    public String getToken() {
        String cacheKey = TOKEN_KEY_PREFIX + kisTokenProperties.getClientId();

        try {
            String token = redisTemplate.opsForValue().get(cacheKey);
            if (token == null) {
                token = fetchAndCacheNewToken(cacheKey);
            }

            return token;
        } catch (Exception e) {
            log.error("KIS 토큰 관리 프로세스 중 오류 발생: {}", e.getMessage(), e);
            throw new TokenFetchException("KIS 토큰 발급 실패", e);
        }
    }

    private String fetchAndCacheNewToken(String cacheKey) {
        log.info("만료되었거나 유효한 토큰이 없어 새 토큰을 발급합니다.");
        
        KisTokenResponse response = kisTokenClient.fetchToken();
        String newToken = response.getAccess_token();
        
        long expiresIn = response.getExpires_in();
        Duration ttl = Duration.ofSeconds(expiresIn - SAFE_DURATION);

        redisTemplate.opsForValue().set(cacheKey, newToken, ttl);
        log.info("새 토큰 발급 완료 및 Redis 저장 (TTL: {}초)", ttl.getSeconds());
        
        return newToken;
    }

}

