package kis.client.dto.client;

import kis.client.dto.kis.KisFxInfoDto;
import kis.client.dto.kis.KisFxPastInfoDto;
import kis.client.dto.kis.output.KisFxDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class FxResponseDto {

    private String changePrice;

    private String changeSign;

    private String changeRate;

    private String prevPrice;

    private String highPrice;

    private String lowPrice;

    private String openPrice;

    private String currentPrice;

    private List<KisFxPastInfoDto> pastInfo;

    public FxResponseDto(KisFxDto kisFxDto) {
        KisFxInfoDto fx = kisFxDto.getOutput1();
        changePrice = fx.getChangePrice();
        changeSign = fx.getChangeSign();
        changeRate = fx.getChangeRate();
        prevPrice = fx.getPrevPrice();
        highPrice = fx.getHighPrice();
        lowPrice = fx.getLowPrice();
        openPrice = fx.getOpenPrice();
        currentPrice = fx.getCurrentPrice();
        pastInfo = kisFxDto.getOutput2();
    }
}
