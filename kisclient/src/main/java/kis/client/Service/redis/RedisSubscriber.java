package kis.client.Service.redis;

import kis.client.entity.Holiday;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
public class RedisSubscriber implements MessageListener {

    /**
     * Redis 메시지를 수신하고 처리하는 메서드입니다.
     * @param message 수신된 Redis 메시지
     * @param pattern 패턴 (사용되지 않음)
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String raw = new String(message.getBody()); // "\"20250606\""
        String topic = new String(message.getChannel());
        log.info("수신된 메시지: {} (채널: {})", raw, topic);

        try {
            // 1. 앞뒤 따옴표 제거
            String cleaned = raw.replaceAll("^\"|\"$", "");  // => 20250606

            // 2. 날짜로 파싱
            LocalDate date = LocalDate.parse(cleaned, DateTimeFormatter.ofPattern("yyyyMMdd"));
            Holiday.setDate(date);
            if (!Holiday.isContain(date)) {
                Holiday.setDate(date);
            }
        } catch (Exception e) {
            log.error("Redis 메시지 파싱 실패: {}", raw, e);
        }
    }
}
