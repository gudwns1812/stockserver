package stock.mainserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableJpaAuditing
@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class MainserverApplication {

    public static void main(String[] args) {
        SpringApplication.run(MainserverApplication.class, args);
    }

}
