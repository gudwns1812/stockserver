package kis.client.dto.redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IndicePriceDto {
    private String indice_date;
    private String cur_price;
    private String high_price;
    private String low_price;
    private String acml_vol;
    private String acml_vol_price;
}
