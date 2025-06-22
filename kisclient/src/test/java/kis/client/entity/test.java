package kis.client.entity;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "spring.config.location=classpath:/application-test.yml"
})
public class test {

    @Test
    public void test() {

        System.out.println("test");
    }
}
