package kis.client.dto.kis.output;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class KisOutputDto<T> {

    @JsonProperty("output")
    private T output;
}
