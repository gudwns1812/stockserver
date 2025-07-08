package kis.client.dto.redis;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StockInfoDto {
    String stockCode;
    String stockImage;
    String stockName;
}
