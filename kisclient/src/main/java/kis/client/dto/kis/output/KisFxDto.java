package kis.client.dto.kis.output;

import com.fasterxml.jackson.annotation.JsonProperty;
import kis.client.dto.kis.KisFxPastInfoDto;
import kis.client.dto.kis.KisFxInfoDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KisFxDto {

    @JsonProperty("output1")
    private KisFxInfoDto output1;

    @JsonProperty("output2")
    private List<KisFxPastInfoDto> output2;

}
