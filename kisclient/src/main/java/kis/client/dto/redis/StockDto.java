package kis.client.dto.redis;

import kis.client.dto.kis.KisStockDto;
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

    public StockDto(String code, KisStockDto dto) {
        stockCode = code;
        categoryName = dto.getCategoryName();
        price = dto.getPrice();
        marketName = dto.getMarketName();
        changeAmount = dto.getChangeAmount();
        sign = dto.getSign();
        changeRate = dto.getChangeRate();
        volume = dto.getVolume();
    }

}
