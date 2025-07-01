package kis.client.Service.scheduler;

import kis.client.entity.Holiday;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class WarmUpScheduler {

    @Scheduled(cron = "1 0 0 * * * ")
    public void warmUp() {
        log.info("Warm-up scheduler started.");
        LocalDate today = LocalDate.now();
        if (today.getDayOfMonth() == 1) {
            log.info("오늘은 매월 1일입니다.");
            Holiday.clear();
        } else {
            log.info("Today is not the first day of the month. Skipping warm-up tasks.");
        }
    }



}
