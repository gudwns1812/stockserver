package kis.client.dto.redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IndicesDto {
    private String prev;
    private String sign;
    private String prev_rate;
    private List<IndicePriceDto> indices;
}
