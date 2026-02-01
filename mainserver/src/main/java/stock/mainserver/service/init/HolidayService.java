package stock.mainserver.service.init;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import stock.mainserver.dto.data.HolidayItem;
import stock.mainserver.dto.data.Response;
import stock.mainserver.service.redis.HolidayToRedis;

@Service
@RequiredArgsConstructor
@Slf4j
@Getter
public class HolidayService {

    private final ObjectMapper objectMapper;
    private final RestClient restClient;
    private final HolidayToRedis publisher;
    private final XmlMapper mapper;

    @Value("${data.service.key}")
    private String key;
    private List<LocalDate> holidays = new ArrayList<>();

    @PostConstruct
    public void init() {
        getHolidayListFromApi();
    }

    @Scheduled(cron = "0 5 0 * * *")
    private void getHolidayListFromApi() {
        LocalDate today = LocalDate.now();

        String xml = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("apis.data.go.kr")
                        .path("B090041/openapi/service/SpcdeInfoService/getRestDeInfo")
                        .queryParam("serviceKey", key)
                        .queryParam("solYear", today.getYear())
                        .queryParam("solMonth", String.format("%02d", today.getMonthValue()))
                        .build()
                )
                .retrieve()
                .body(String.class);

        Response response;
        try {
            response = mapper.readValue(xml, Response.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        if (response.getBody() == null || response.getBody().getItems() == null) {
            log.error("공휴일을 추가하는데 에러 발생했습니다.");
            return;
        }
        List<HolidayItem> item = response.getBody().getItems().getItem();
        if (item == null || item.isEmpty()) {
            log.warn("공휴일 API 응답이 비어있음");
            return; // 혹은 예외 발생
        }
        List<LocalDate> addedDates = item.stream()
                .map(it -> LocalDate.parse(it.getLocdate(), DateTimeFormatter.ofPattern("yyyyMMdd")))
                .toList();

        for (LocalDate added : addedDates) {
            String dateStr = added.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            publisher.publish(dateStr);
            log.info("공휴일 추가 및 publish: {}", dateStr);
        }

        holidays = addedDates;
    }
}
