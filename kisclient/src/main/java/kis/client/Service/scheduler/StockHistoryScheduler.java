package kis.client.Service.scheduler;

import kis.client.dto.kis.KisPeriodStockDto;
import kis.client.entity.Stock;
import kis.client.entity.StockHistory;
import kis.client.entity.Holiday;
import kis.client.repository.StockHistoryRepository;
import kis.client.repository.StockInit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class StockHistoryScheduler {

    private final StockHistoryRepository stockHistoryRepository;
    private final StockInit stockInit;
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");


    @Scheduled(cron = "0 50 23 * * *")
    @Transactional
    public void batchToStockHistory() {
        LocalDate today = LocalDate.now();
        DayOfWeek day = today.getDayOfWeek();
        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            log.info("주말입니다. 스케줄러 동작 안 함.");
            return;
        } else if (Holiday.isContain(today)) {
            log.info("공휴일입니다. 스케줄러도 쉴게요~");
            return;
        }

        String todayStr = today.format(FORMAT);
        LocalDate yesterday = today.minusDays(1);
        List<Stock> stocks = stockInit.getStocks();
        for (Stock stock : stocks) {
            String code = stock.getStockCode();

            Optional<StockHistory> daily = stockHistoryRepository.findByStockCodeAndTypeAndDate(code, "D", LocalDate.parse(todayStr, FORMAT));
            if (daily.isEmpty()) {
                saveHistory(code, "D", todayStr, stock, yesterday);
            } else {
                log.debug("일간 데이터 이미 존재: {}", code);
            }

            // 주간
            LocalDate mondayOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            Optional<StockHistory> thisWeekData = stockHistoryRepository.findByStockCodeAndTypeAndDateBetween(code, "W", mondayOfWeek,today);
            if (thisWeekData.isEmpty()) {
                saveHistory(code, "W", todayStr, stock, mondayOfWeek);
            } else {
                updateHistoryIfExists(code, "W", stock, mondayOfWeek);
            }

            // 월간
            LocalDate firstDayOfMonth = today.with(TemporalAdjusters.firstDayOfMonth());
            Optional<StockHistory> monthlyData = stockHistoryRepository
                    .findByStockCodeAndTypeAndDateBetween(code, "M", firstDayOfMonth, today);
            if (monthlyData.isEmpty()) {
                saveHistory(code, "M", todayStr, stock, today);
            } else {
                updateHistoryIfExists(code, "M", stock, today);
            }

            // 연간
            LocalDate firstDayOfYear = today.with(TemporalAdjusters.firstDayOfYear());
            Optional<StockHistory> yearlyData = stockHistoryRepository
                    .findByStockCodeAndTypeAndDateBetween(code, "Y", firstDayOfYear, today);
            if (yearlyData.isEmpty()) {
                saveHistory(code, "Y", todayStr, stock, today);
            } else {
                updateHistoryIfExists(code, "Y", stock, today);
            }

        }

    }
    private void updateHistoryIfExists(String code, String type, Stock stock, LocalDate baseDate) {
        Optional<StockHistory> optional = stockHistoryRepository.findByStockCodeAndTypeAndDate(code, type, baseDate);
        if (optional.isEmpty()) return;

        StockHistory history = optional.get();

        int newHigh = parseIntSafe(stock.getHighPrice());
        int newLow = parseIntSafe(stock.getLowPrice());
        int oldHigh = parseIntSafe(history.getHigh());
        int oldLow = parseIntSafe(history.getLow());

        if (newHigh > oldHigh) {
            history.updateHigh(stock.getHighPrice());
        }
        if (newLow < oldLow || oldLow == 0) {
            history.updateLow(stock.getLowPrice());
        }
        // ✅ 기존 volume 누적
        long prevVolume = parseLongSafe(history.getVolume());
        long newVolume = parseLongSafe(stock.getVolume());
        String totalVolume = String.valueOf(prevVolume + newVolume);

        long prevVolumeValue = parseLongSafe(history.getVolumeAmount());
        long newVolumeValue = parseLongSafe(stock.getVolumeValue());
        String totalVolumeValue = String.valueOf(prevVolumeValue + newVolumeValue);
        history.updateDate(baseDate);
        // ✅ close는 그대로 덮어씀
        history.updateClose(stock.getPrice(), totalVolume, totalVolumeValue);
    }

    private void saveHistory(String code, String type, String todayStr, Stock stock, LocalDate prevDate) {

        // 이전 데이터 조회 (종가)
        String prevClose = stockHistoryRepository
                .findByStockCodeAndTypeAndDate(code, type, prevDate)
                .map(StockHistory::getClose)
                .orElse("0");

        KisPeriodStockDto dto = new KisPeriodStockDto(
                todayStr,
                stock.getPrice(),
                stock.getOpenPrice(),
                stock.getHighPrice(),
                stock.getLowPrice(),
                stock.getVolume(),
                stock.getVolumeValue(),
                prevClose,
                null
        );

        StockHistory newHistory = StockHistory.createHistory(code, type, dto);
        stockHistoryRepository.save(newHistory);
    }
    private int parseIntSafe(String value) {
        return Integer.parseInt(value == null || value.isBlank() ? "0" : value);
    }
    private long parseLongSafe(String s) {
        try {
            return s == null ? 0L : Long.parseLong(s);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
}
