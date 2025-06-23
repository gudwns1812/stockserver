package kis.client.dto.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class HolidayItem {
    private String locdate; // yyyyMMdd
}