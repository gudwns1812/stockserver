package kis.client.global.token;

import kis.client.global.error.TokenFetchException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KisTokenManagerTest {

    private KisTokenManager kisTokenManager;

    @Mock
    private KisTokenClient kisTokenClient;

    @Mock
    private KisTokenProperties kisTokenProperties;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private final String clientId = "testClientId";
    private final String redisKey = "KIS_TOKEN::" + clientId;

    @BeforeEach
    void setUp() {
        kisTokenManager = new KisTokenManager(kisTokenClient, kisTokenProperties, redisTemplate);
        given(kisTokenProperties.getClientId()).willReturn(clientId);
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
    }

    @Test
    @DisplayName("Redis에 토큰이 있으면 API를 호출하지 않고 Redis 토큰을 반환한다")
    void getToken_fromRedis() {
        // given
        String existingToken = "existing-token";
        given(valueOperations.get(redisKey)).willReturn(existingToken);

        // when
        String result = kisTokenManager.getToken();

        // then
        assertThat(result).isEqualTo(existingToken);
        verify(kisTokenClient, never()).fetchToken();
    }

    @Test
    @DisplayName("Redis에 토큰이 없으면 API를 호출하여 새 토큰을 발급받고 Redis에 저장한다")
    void getToken_fromApi() {
        // given
        given(valueOperations.get(redisKey)).willReturn(null);

        KisTokenResponse response = new KisTokenResponse();
        response.setAccess_token("new-token");
        response.setExpires_in(24 * 60 * 60); // 24시간
        given(kisTokenClient.fetchToken()).willReturn(response);

        ArgumentCaptor<Duration> ttlCaptor = ArgumentCaptor.forClass(Duration.class);

        // when
        String result = kisTokenManager.getToken();

        // then
        assertThat(result).isEqualTo("new-token");
        verify(valueOperations).set(eq(redisKey), eq("new-token"), ttlCaptor.capture());

        Duration ttl = ttlCaptor.getValue();
        assertThat(ttl).isEqualTo(Duration.ofSeconds(23 * 60 * 60)); // 23시간
    }

    @Test
    @DisplayName("토큰 발급 중 예외가 발생하면 TokenFetchException을 던진다")
    void getToken_exception() {
        // given
        given(valueOperations.get(redisKey)).willThrow(new RuntimeException("Redis error"));

        // when & then
        assertThatThrownBy(() -> kisTokenManager.getToken())
                .isInstanceOf(TokenFetchException.class)
                .hasMessageContaining("KIS 토큰 발급 실패");
    }
}
