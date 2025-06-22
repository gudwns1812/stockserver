package kis.client;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test") // 이 부분을 추가합니다.
class SseServerApplicationTests {

	@Test
	void contextLoads() {
	}

}
