package stock.mainserver.dto.request;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StockCountRequestDto {
    private String stockCode;
}