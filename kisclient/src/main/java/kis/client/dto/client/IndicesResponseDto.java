package kis.client.dto.client;

import kis.client.dto.kis.KisIndicePriceDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IndicesResponseDto {

    private String prev;

    private String sign;

    private String prev_rate;

    private List<KisIndicePriceDto> indices;

}
