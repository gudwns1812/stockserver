package kis.client.dto.client;

import io.swagger.v3.oas.annotations.media.Schema;
import kis.client.dto.kis.KisFxPastInfoDto;
import kis.client.dto.kis.KisFxInfoDto;
import kis.client.dto.kis.output.KisFxDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Schema(description = "FX, 원자재, 채권 응답 DTO")
public class FxResponseDto {

    @Schema(description = "가격 변화량")
    private String changePrice;

    @Schema(description = "가격 변화 부호", example = "2 = 상승 ,등등 1~5까지의 숫자")
    private String changeSign;

    @Schema(description = "가격 변화 비율" , example = "0.19")
    private String changeRate;

    @Schema(description = "이전 종가")
    private String prevPrice;

    @Schema(description = "고가")
    private String highPrice;

    @Schema(description = "저가")
    private String lowPrice;

    @Schema(description = "시가")
    private String openPrice;

    @Schema(description = "현재가")
    private String currentPrice;

    @Schema(description = "과거 가격 이력")
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
