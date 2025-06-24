package kis.client.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import jakarta.annotation.PostConstruct;
import kis.client.dto.data.HolidayItem;
import kis.client.dto.data.Response;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Getter
public class HolidayService {

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    @Value("${data.service.key}")
    private String key;
    private List<LocalDate> holidays;

    @PostConstruct
    public void init() {
        getHolidayListFromApi();
    }

    public boolean isHoliday(LocalDate date) {
        if (holidays.isEmpty()) return false;
        return holidays.contains(date);
    }

    @Scheduled(cron = "0 0 0 1 * *")
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
            holidays = item.stream().map((it) -> {
                String localdate = it.getLocdate();
                LocalDate date = LocalDate.parse(localdate, DateTimeFormatter.ofPattern("yyyyMMdd"));
                return date;
            }).toList();

        } catch (Exception e) {
            throw new RuntimeException("공휴일 API 파싱 실패", e);
        }


    }


}
