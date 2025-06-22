package stock.mainserver.dto.redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockDto {
    private String stockCode;
    private String categoryName;
    private String price;
    private String marketName;
    private String changeAmount;
    private String sign;
    private String changeRate;
    private String volume;
    private String volumeValue;
    private String stockImage;
}
