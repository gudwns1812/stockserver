package kis.client.Service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FailStock {
    private String stockCode;
    private String type;
    private String startDate;
    private String endDate;

}
