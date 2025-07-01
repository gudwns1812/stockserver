package stock.mainserver.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import stock.mainserver.dto.data.FxEncoder;
import stock.mainserver.dto.redis.FxRedisDto;
import stock.mainserver.global.error.FxNotFoundException;

@Service
@RequiredArgsConstructor
public class FxService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final FxEncoder fxEncoder;
    private final ObjectMapper objectMapper;

    public FxRedisDto getFxInfo(String type, String code) {
        String fxCode;
        if (type.equals("FX")) fxCode = fxEncoder.fxConvert(code).get(1);
        else if (type.equals("Feed")) fxCode = fxEncoder.feedConvert(code).get(1);
        else fxCode = fxEncoder.bondsConvert(code).get(1);

        String redisKey = "FX:" + fxCode;
        Object object = redisTemplate.opsForValue().get(redisKey);
        if (object == null) {
            throw new FxNotFoundException("해당 FX 정보를 찾을 수 없습니다: " + fxCode);
        }
        return objectMapper.convertValue(object, FxRedisDto.class);

    }
}
