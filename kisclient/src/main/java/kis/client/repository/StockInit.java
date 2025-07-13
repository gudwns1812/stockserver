package kis.client.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import kis.client.dto.redis.StockInfoDto;
import kis.client.global.error.StockNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
@Getter
public class StockInit {

    private final RedisTemplate<String, Object> redisTemplate;
    private final List<StockInfoDto> stocks = new ArrayList<>();
    private final ObjectMapper objectMapper;

    @Value("${kis.clientId}")
    private String clientId; // 클라이언트 아이디는 application.properties에서 설정
    @PostConstruct
    public void init() {
        String id = clientId.split("-")[1];
        Set<Object> objects = redisTemplate.opsForSet().members("Client_ID:" + id);
        if (objects == null || objects.isEmpty()) {
            throw new StockNotFoundException("Client ID not found in Redis: " + id);
        }
        List<String> list = objects.stream()
                .map(Object::toString)
                .toList();
        stocks.addAll(list.stream()
                .map((s) -> {
                    Object o = redisTemplate.opsForValue().get("STOCK_INFO:" + s);
                    return objectMapper.convertValue(o, StockInfoDto.class);
                }).toList());
        log.info("Stock init: {}", stocks.size());
    }

    @Scheduled(cron = "0 0 9 * * *")
    public void refreshStockInfo() {
        stocks.clear();
        init(); // 재초기화
        log.info("Stock info refreshed: {}", stocks.size());
    }

}
