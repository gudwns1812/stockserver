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
    private String stockName;
    private String categoryName;
    private String price;
    private String marketName;
    private String changeAmount;
    private String sign;
    private String changeRate;
    private String volume;
    private String volumeValue;

    public StockDto(String name, String code, KisStockDto dto) {
        stockCode = code;
        stockName = name;
        categoryName = dto.getCategoryName();
        price = dto.getPrice();
        marketName = dto.getMarketName();
        changeAmount = dto.getChangeAmount();
        sign = dto.getSign();
        changeRate = dto.getChangeRate();
        volume = dto.getVolume();
        volumeValue = dto.getVolumeValue();
    }

}
