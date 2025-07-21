package kis.client.Service;

import kis.client.dto.kis.KisPeriodStockDto;
import kis.client.dto.kis.KisStockDto;
import kis.client.dto.kis.output.KisApiResponseDto;
import kis.client.dto.kis.output.KisOutputDto;
import kis.client.global.error.StockNotFoundException;
import kis.client.global.token.KisTokenManager;
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
public class GetStockClient {

    private final RestClient kisClient;

    public KisStockDto getStockInfo(String token,String stockCode) {
        try {
            ResponseEntity<KisOutputDto<KisStockDto>> response = kisClient.get()
                    .uri(uriBuilder ->
                        uriBuilder.path("/uapi/domestic-stock/v1/quotations/inquire-price")
                                .queryParam("FID_COND_MRKT_DIV_CODE", "J")
                                .queryParam("FID_INPUT_ISCD", stockCode)
                                .build()
                    )
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .header("tr_id", "FHKST01010100")
                    .header("custtype", "P")
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<>() {
                    });
            if (response.getBody() != null) {
                return response.getBody().getOutput();
            } else {
                log.error("KIS API 응답이 null입니다.");
                return null;
            }
        } catch (HttpServerErrorException e) {
            log.error(e.getMessage(),e);
            return null;
        } catch (RestClientException e) {
            log.error(e.getMessage());
            return null;
        }

    }

    public List<KisPeriodStockDto> getStockInfoByPeriod(String token, String stockCode, String period, String startDate, String endDate) {

        try {
            ResponseEntity<KisApiResponseDto<KisStockDto,List<KisPeriodStockDto>>> response = kisClient.get()
                    .uri(uriBuilder ->
                            uriBuilder.path("/uapi/domestic-stock/v1/quotations/inquire-daily-itemchartprice")
                                    .queryParam("FID_COND_MRKT_DIV_CODE", "J")
                                    .queryParam("FID_INPUT_ISCD", stockCode)
                                    .queryParam("FID_INPUT_DATE_1", startDate)
                                    .queryParam("FID_INPUT_DATE_2", endDate)
                                    .queryParam("FID_PERIOD_DIV_CODE", period)
                                    .queryParam("FID_ORG_ADJ_PRC", "1") // 1: 수정주가, 0: 수정주가 미적용
                                    .build()
                    )
                    .header("tr_id", "FHKST01010100")
                    .header("custtype", "P")
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<>() {
                    });
            return response.getBody().getOutput2();
        } catch (HttpServerErrorException e) {
            log.error(e.getMessage(),e);
            return null;
        } catch (RestClientException e) {
            log.error(e.getMessage());
            return null;
        } catch (NullPointerException e) {
            throw new StockNotFoundException("KIS에서 주식을 불러오지 못했습니다.", e);
        }
    }

}
