package kis.client.dto.kis.output;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KisApiResponseDto<T1,T2> {
    @JsonProperty("output1")
    private T1 output1;

    @JsonProperty("output2")
    private T2 output2;
}
