package stock.mainserver.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import stock.mainserver.dto.redis.KisPopularRedisDto;
import stock.mainserver.dto.response.PopularStockResponseDto;
import stock.mainserver.global.error.PopularNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PopularService {
    private static final String POPULAR_KEY = "POPULAR";

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public  List<PopularStockResponseDto> getPopularTop6Stock() {
        Object object = redisTemplate.opsForValue().get(POPULAR_KEY);
        if (object == null) {
            throw new PopularNotFoundException("인기종목을 찾지 못했습니다.");
        }

        List<KisPopularRedisDto> list = objectMapper.convertValue(object, new TypeReference<>() {});
        return list.stream().map(PopularStockResponseDto::new).toList();
    }

}
