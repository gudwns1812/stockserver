package kis.client.Service;


import kis.client.dto.client.IndicesResponseDto;
import kis.client.dto.kis.KisIndiceInfoDto;
import kis.client.dto.kis.KisIndicePriceDto;
import kis.client.dto.kis.output.KisApiResponseDto;
import kis.client.global.token.KisTokenProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class GetIndiceInfoClient {
    private final KisTokenProperties kisTokenProperties;
    private final RestTemplate restTemplate;
    private final RestClient kisClient;

    public IndicesResponseDto getIndiceInfo(String token, String market, String startDate, String endDate) {
        String marketCode;

        if (market.equals("KOSPI")) {
            marketCode = "0001";
        } else{
            marketCode = "1001";
        }
        try {
            ResponseEntity<KisApiResponseDto<KisIndiceInfoDto,List<KisIndicePriceDto>>> response = kisClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/uapi/domestic-stock/v1/quotations/inquire-daily-indexchartprice")
                            .queryParam("FID_COND_MRKT_DIV_CODE", "U")
                            .queryParam("FID_INPUT_ISCD", marketCode)           // 코스피
                            .queryParam("FID_INPUT_DATE_1", startDate)
                            .queryParam("FID_INPUT_DATE_2", endDate)
                            .queryParam("FID_PERIOD_DIV_CODE", "D")
                            .build()
                    )
                    .header(HttpHeaders.AUTHORIZATION,"Bearer " + token)
                    .header("tr_id", "FHKUP03500100")
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<>() {
                    });

            KisIndiceInfoDto output1 = response.getBody().getOutput1();
            List<KisIndicePriceDto> output2 = response.getBody().getOutput2();
            return new IndicesResponseDto(
                    output1.getPrev(),
                    output1.getSign(),
                    output1.getPrev_rate(),
                    output2
            );
        } catch (HttpServerErrorException e) {
            log.error(e.getMessage(),e);
            return null;
        } catch (RestClientException e) {
            log.error(e.getMessage());
            return null;
        }

    }


}
