package kis.client.global.config;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.IdleConnectionEvictor;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

@Configuration
public class RestConfig {

    @Bean
    public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(100); // 최대 전체 연결 수
        connectionManager.setDefaultMaxPerRoute(20); // 호스트당 최대 연결 수
        return connectionManager;
    }

    // 유휴/만료된 연결을 주기적으로 제거하는 IdleConnectionEvictor 빈 정의
    // destroyMethod를 사용하여 애플리케이션 종료 시 스레드가 안전하게 종료되도록 합니다.
    @Bean(destroyMethod = "shutdown")
    public IdleConnectionEvictor idleConnectionEvictor(PoolingHttpClientConnectionManager connectionManager) {
        // 첫 번째 인자: 연결 관리자
        // 두 번째 인자: 유휴 연결을 제거할 주기 (예: 30초마다 검사)
        // 세 번째 인자: 시간 단위
        // 네 번째 인자: 연결이 유휴 상태로 간주되는 최소 시간 (예: 5초 이상 유휴 시 제거 대상)
        // 다섯 번째 인자: 시간 단위
        IdleConnectionEvictor connectionEvictor = new IdleConnectionEvictor(
                connectionManager,
                TimeValue.ofSeconds(30), // 주기적으로 유휴 연결을 검사하는 간격
                TimeValue.ofSeconds(30)   // 연결이 유휴 상태로 간주되는 최소 시간
        );
        connectionEvictor.start(); // 스레드 시작
        return connectionEvictor;
    }

    @Bean
    public HttpClient httpClient(PoolingHttpClientConnectionManager connectionManager) {

        // 요청 설정을 정의합니다.
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofSeconds(3)) // 연결 요청 타임아웃
                .setResponseTimeout(Timeout.ofSeconds(5)) // 응답 타임아웃
                .build();

        return HttpClientBuilder.create()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .build();
    }

    @Bean
    public RestTemplate restTemplate() {
//
//        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
//        requestFactory.setConnectTimeout(3000); // 연결 타임아웃 설정

        return new RestTemplate();
    }
}
