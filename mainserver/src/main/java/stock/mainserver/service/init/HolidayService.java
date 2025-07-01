package stock.mainserver.service.init;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import stock.mainserver.dto.data.HolidayItem;
import stock.mainserver.dto.data.Response;
import stock.mainserver.service.redis.HolidayToRedis;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Getter
public class HolidayService {

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final HolidayToRedis publisher;

    @Value("${data.service.key}")
    private String key;
    private List<LocalDate> holidays = new ArrayList<>();

    @PostConstruct
    public void init() {
        getHolidayListFromApi();
    }

    @Scheduled(cron = "0 5 0 * * *")
    private void getHolidayListFromApi() {
        String url = "https://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getRestDeInfo";
        LocalDate today = LocalDate.now();

        String furi = String.format(
                "%s?serviceKey=%s&solYear=%d&solMonth=%02d",
                url, key, today.getYear(), today.getMonthValue()
        );
        URI uri = URI.create(furi);
        String xml = restTemplate.getForObject(uri, String.class);
        try {
            XmlMapper mapper = new XmlMapper();
            Response response = mapper.readValue(xml, Response.class);

            if (response.getBody() == null || response.getBody().getItems() == null) {
                log.error("공휴일을 추가하는데 에러 발생했습니다.");
                return;
            }
            List<HolidayItem> item = response.getBody().getItems().getItem();
            if (item == null || item.isEmpty()) {
                log.warn("공휴일 API 응답이 비어있음");
                return; // 혹은 예외 발생
            }
            List<LocalDate> newDates = item.stream()
                    .map(it -> LocalDate.parse(it.getLocdate(), DateTimeFormatter.ofPattern("yyyyMMdd")))
                    .toList();

            List<LocalDate> addedDates = newDates.stream()
                    .filter(date -> !holidays.contains(date))
                    .toList();

            for (LocalDate added : addedDates) {
                String dateStr = added.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                publisher.publish(dateStr);
                log.info("공휴일 추가 및 publish: {}", dateStr);
            }

            holidays = newDates;

        } catch (Exception e) {
            throw new RuntimeException("공휴일 API 파싱 실패", e);
        }


    }


}
