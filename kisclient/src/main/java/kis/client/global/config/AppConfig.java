package kis.client.global.config;

import com.google.common.util.concurrent.RateLimiter;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
@Slf4j
public class AppConfig {

    @Value("${kis.appkey}")
    private String appKey;
    @Value("${kis.appsecret}")
    private String appSecret;


    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);    // 병렬 최대 수
        executor.setMaxPoolSize(4);     // 더 이상 늘어나면 API 터짐
        executor.setQueueCapacity(500); // 👉 충분히 큐 확보! (500 이상)
        executor.setThreadNamePrefix("stock-");
        executor.initialize();
        return executor;
    }
    @Bean
    public WebClient webClient() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                .responseTimeout(Duration.ofSeconds(5))
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(5))
                        .addHandlerLast(new WriteTimeoutHandler(5)));
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl("https://openapi.koreainvestment.com:9443/uapi/domestic-stock/v1/quotations") // KIS API의 기본 URL
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("appKey", appKey) // 여기에 실제 appKey를 넣어주세요.
                .defaultHeader("appSecret", appSecret) // 여기에 실제 appSecret을 넣어주세요.
                .filter(errorHandlingFilter())
                .build();
    }

    private ExchangeFilterFunction errorHandlingFilter() {
        return ExchangeFilterFunction.ofResponseProcessor(response -> {
            if (response.statusCode().isError()) {
                return response.bodyToMono(String.class)
                        .flatMap(body -> {
                            log.error("KIS API 에러 발생. status: {}, body: {}", response.statusCode(), body);
                            return Mono.error(new RuntimeException("KIS API 오류: " + body));
                        });
            }
            return Mono.just(response);
        });
    }

    @Bean
    public JPAQueryFactory jpaQueryFactory(EntityManager em) {
        return new JPAQueryFactory(em);
    }

    @Bean
    public RateLimiter rateLimiter() {
        return RateLimiter.create(19.0); // 초당 5개
    }

}
