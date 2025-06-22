package stock.mainserver.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import stock.mainserver.dto.redis.IndicesRedisDto;
import stock.mainserver.global.error.IndicesNotFoundException;

@Service
@RequiredArgsConstructor
@Slf4j
public class IndicesService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public IndicesRedisDto getIndicesInfo(String market) {
        String indicesKey = "INDICES_INFO:" + market;
        Object object = redisTemplate.opsForValue().get(indicesKey);
        if (object == null) {
            throw new IndicesNotFoundException("지수 관련 정보를 찾지 못했습니다.");
        }
        return objectMapper.convertValue(object, IndicesRedisDto.class);

    }

}
