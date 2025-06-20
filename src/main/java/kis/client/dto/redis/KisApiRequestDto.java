package kis.client.dto.redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KisApiRequestDto {
    private String type; // "stock" or "fx"
    private Object data;
}