package kis.client.global.config;

import com.google.common.util.concurrent.RateLimiter;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AppConfig {


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
    public JPAQueryFactory jpaQueryFactory(EntityManager em) {
        return new JPAQueryFactory(em);
    }

    @Bean
    public RateLimiter rateLimiter() {
        return RateLimiter.create(19.0); // 초당 5개
    }

}
