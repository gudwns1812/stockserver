package stock.mainserver.global.vo;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 스펙상 필수
@AllArgsConstructor
public class DailyPrice {

    private Long open;
    private Long high;
    private Long low;
    private Long close;
    private Long volume;
    private Long volumeAmount;
}