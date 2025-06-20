package kis.client.entity;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Getter

public class FxEncoder {

    private final Map<String, List<String>> FxEncoder = new ConcurrentHashMap<>();
    private final Map<String, List<String>> FeedstockEncoder = new ConcurrentHashMap<>();
    private final Map<String, List<String>> BondsEncoder = new ConcurrentHashMap<>();

    @PostConstruct
    public void CodeInit() {
        FxEncoder.put("dollar",List.of("X","FX@KRW"));
        FxEncoder.put("yen",List.of("X","FX@KRWJS"));
        FxEncoder.put("DOW", List.of("N",".DJI"));
        FxEncoder.put("NQ", List.of("N","COMP"));
        FxEncoder.put("SP500", List.of("N","SPX"));
        FxEncoder.put("Nikkei", List.of("N","JP#NI225"));
        FxEncoder.put("HangSeng", List.of("N","HK#HS"));
        FxEncoder.put("ShangHai", List.of("N","SHANG"));

        FeedstockEncoder.put("GOLD",List.of("N","NYGOLD"));
        FeedstockEncoder.put("SILVER",List.of("N","NYSILV"));
        FeedstockEncoder.put("WTI",List.of("N","WTIF"));
        FeedstockEncoder.put("CORN",List.of("N","CHICORN"));
        FeedstockEncoder.put("COFFEE",List.of("N","COFFE"));
        FeedstockEncoder.put("COTTON",List.of("N","COTTON"));

        BondsEncoder.put("KRBONDS1", List.of("I","Y0104"));
        BondsEncoder.put("KRBONDS3", List.of("I","Y0101"));
        BondsEncoder.put("KRBONDS5", List.of("I","Y0105"));
        BondsEncoder.put("KRBONDS10", List.of("I","Y0106"));
        BondsEncoder.put("KRBONDS20", List.of("I","Y0116"));
        BondsEncoder.put("KRBONDS30", List.of("I","Y0117"));
        BondsEncoder.put("USBONDS1", List.of("I","Y0203"));
        BondsEncoder.put("USBONDS10", List.of("I","Y0202"));
        BondsEncoder.put("USBONDS30", List.of("I","Y0201"));
    }
    public List<String> fxConvert(String code) {
        return FxEncoder.get(code);
    }
    public List<String> feedConvert(String code) {
        return FeedstockEncoder.get(code);
    }
    public List<String> bondsConvert(String code) {
        return BondsEncoder.get(code);
    }

    public List<List<String>> getAllFx() {
        List<List<String>> values = new ArrayList<>(FxEncoder.values());
        values.addAll(FeedstockEncoder.values());
        values.addAll(BondsEncoder.values());
        return values;
    }
}
