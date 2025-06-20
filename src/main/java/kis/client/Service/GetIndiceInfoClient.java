package kis.client.Service;


import kis.client.dto.client.IndicesResponseDto;
import kis.client.dto.kis.KisIndiceInfoDto;
import kis.client.dto.kis.KisIndicePriceDto;
import kis.client.dto.kis.output.KisApiResponseDto;
import kis.client.global.token.KisTokenManager;
import kis.client.global.token.KisTokenProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class GetIndiceInfoClient {
    private final KisTokenManager kisTokenManager;
    private final KisTokenProperties kisTokenProperties;
    private final RestTemplate restTemplate;

    public KisApiResponseDto<KisIndiceInfoDto,List<KisIndicePriceDto>> getIndiceInfo(String market, String startDate, String endDate) {
        String marketCode;

        if (market.equals("KOSPI")) {
            marketCode = "0001";
        } else{
            marketCode = "1001";
        }
        log.info("MARKETCODE: {}", marketCode);
        String token = kisTokenManager.getToken();
        String url = "https://openapi.koreainvestment.com:9443/uapi/domestic-stock/v1/quotations/inquire-daily-indexchartprice";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("authorization", "Bearer " + token);
        headers.set("appkey", kisTokenProperties.getAppkey());
        headers.set("appsecret", kisTokenProperties.getAppsecret());
        headers.set("tr_id", "FHKUP03500100");

        HttpEntity<String> entity = new HttpEntity<String>(headers);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("FID_COND_MRKT_DIV_CODE", "U")
                .queryParam("FID_INPUT_ISCD", marketCode)           // 코스피
                .queryParam("FID_INPUT_DATE_1", startDate)
                .queryParam("FID_INPUT_DATE_2", endDate)
                .queryParam("FID_PERIOD_DIV_CODE", "D");         // 일간
        URI finalUri = builder.build().encode().toUri();
        try {
            ResponseEntity<KisApiResponseDto<KisIndiceInfoDto,List<KisIndicePriceDto>>> response = restTemplate.exchange(
                    finalUri,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {}
            );
            KisIndiceInfoDto output1 = response.getBody().getOutput1();
            List<KisIndicePriceDto> output2 = response.getBody().getOutput2();
            IndicesResponseDto indicesResponseDto = new IndicesResponseDto(
                    output1.getPrev(),
                    output1.getSign(),
                    output1.getPrev_rate(),
                    output2
            );
            return response.getBody();
        } catch (HttpServerErrorException e) {
            log.error(e.getMessage(),e);
            return null;
        } catch (RestClientException e) {
            log.error(e.getMessage());
            return null;
        }

    }


}
