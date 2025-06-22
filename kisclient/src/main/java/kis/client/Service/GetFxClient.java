package kis.client.Service;

import kis.client.dto.client.FxResponseDto;
import kis.client.dto.kis.output.KisFxDto;
import kis.client.global.error.StockNotFoundException;
import kis.client.global.token.KisTokenManager;
import kis.client.global.token.KisTokenProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
@Slf4j
public class GetFxClient  {

    private final KisTokenProperties kisProperties;
    private final KisTokenManager kisTokenManager;
    private final RestTemplate restTemplate;

    public FxResponseDto FxInfo(String token ,String code,String symbol) {

        String startDate = LocalDate.now().minusMonths(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String endDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        String url = "https://openapi.koreainvestment.com:9443/uapi/overseas-price/v1/quotations/inquire-daily-chartprice";

        HttpHeaders headers = new HttpHeaders();
        headers.set("authorization", "Bearer " + token);
        headers.set("appkey", kisProperties.getAppkey());
        headers.set("appsecret", kisProperties.getAppsecret());
        headers.set("tr_id", "FHKST03030100");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        UriComponentsBuilder uri = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("FID_COND_MRKT_DIV_CODE", code)
                .queryParam("FID_INPUT_ISCD", symbol)
                .queryParam("FID_INPUT_DATE_1", startDate)
                .queryParam("FID_INPUT_DATE_2", endDate)
                .queryParam("FID_PERIOD_DIV_CODE", "D");
        URI finalUri = uri.build().encode().toUri();
        try {
            ResponseEntity<KisFxDto> response = restTemplate.exchange(
                    finalUri,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {}
            );
            if (response.getBody() != null) {
                log.info("response = {}", response);
                return new FxResponseDto(response.getBody());
            } else {
                log.error("KIS API 응답이 null입니다.");
                return null;
            }
        } catch (HttpServerErrorException e) {
            throw new StockNotFoundException("Fx not found.",e);
        }
    }
}
