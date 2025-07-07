package kis.client.entity;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class Holiday {
    private static final Set<LocalDate> dates = ConcurrentHashMap.newKeySet();

    public static void setDate(LocalDate date) {
        log.info("Holiday 추가 : {}", date);
        Holiday.dates.add(date);
    }

    public static boolean isContain(LocalDate date) {
        return Holiday.dates.contains(date);
    }

    public static void clear() {
        Holiday.dates.clear();
    }
}
