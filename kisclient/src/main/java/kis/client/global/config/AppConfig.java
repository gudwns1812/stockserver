package kis.client.global.config;

import com.google.common.util.concurrent.RateLimiter;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Configuration
public class AppConfig {
    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);    // 병렬 최대 수
        executor.setMaxPoolSize(10);     // 더 이상 늘어나면 API 터짐
        executor.setQueueCapacity(1000); // 👉 충분히 큐 확보! (500 이상)
        executor.setThreadNamePrefix("stock-");
        executor.initialize();
        return executor;
    }
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

//        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
//            @Override
//            public boolean hasError(ClientHttpResponse response) throws IOException {
//                return false;
//            }
//        });

        return restTemplate;
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
