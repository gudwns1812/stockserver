package kis.client.Service;

import kis.client.dto.kis.KisPeriodStockDto;
import kis.client.dto.kis.KisStockDto;
import kis.client.dto.kis.output.KisApiResponseDto;
import kis.client.dto.kis.output.KisOutputDto;
import kis.client.dto.redis.StockDto;
import kis.client.entity.Stock;
import kis.client.global.error.StockNotFoundException;
import kis.client.global.token.KisTokenManager;
import kis.client.global.token.KisTokenProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetStockReactiveWeb {
    private final KisTokenProperties kisProperties;
    private final KisTokenManager kisTokenManager;
    private final RestTemplate restTemplate;
    private final WebClient webClient;
    private final RedisTemplate<String, Object> redisTemplate;

    public void getStockInfo(String token, List<Stock> stocks) {
        Flux.fromIterable(stocks)
                .delayElements(Duration.ofMillis(200))
                .flatMap(stock ->
                    webClient.get()
                            .uri(uriBuilder ->
                                uriBuilder
                                        .path("/inquire-price")
                                        .queryParam("FID_COND_MRKT_DIV_CODE", "J")
                                        .queryParam("FID_INPUT_ISCD",stock.getStockCode())
                                        .build())
                            .header("authorization", "Bearer " + token)
                            .header("tr_id", "FHKST01010100")
                            .header("custtype", "P")
                            .retrieve()
                            .bodyToMono(KisStockDto.class)
                            .doOnNext(dto -> {
                                StockDto stockDto = new StockDto(stock.getName(),stock.getStockCode(),dto);
                                redisTemplate.opsForValue().set("STOCK:" + stock.getStockCode(), stockDto);
                                log.info("✅ 저장 성공: {} - {}", stock.getStockCode(), stockDto.getStockName());
                            })
                            .retryWhen(Retry.fixedDelay(3, Duration.ofMillis(200))
                                    .filter(this::shouldRetry)
                                    .onRetryExhaustedThrow((spec,signal ) -> signal.failure()))
                            .doOnError(e -> log.error(e.getMessage()))
                        ,4)
                        .subscribe(
                                result -> log.info("모든 주식 정보 저장 완료"),
                                error -> log.error("주식 정보 저장 중 오류 발생: {}", error.getMessage())
                        );

    }

    private boolean shouldRetry(Throwable throwable) {
        log.warn("재시도 조건 확인 중... {}", throwable.toString());
        return throwable instanceof IOException
                || throwable instanceof TimeoutException
                || throwable instanceof RuntimeException;
    }

//    public List<KisPeriodStockDto> getStockInfoByPeriod(String token, String stockCode, String period, String startDate, String endDate) {
//        String url = "/inquire-daily-itemchartprice";
//
//        webClient.get()
//                .uri(uriBuilder ->
//                    uriBuilder
//                            .path(url)
//                            .queryParam("FID_COND_MRKT_DIV_CODE", "J")
//                            .queryParam("FID_INPUT_ISCD", stockCode)
//                            .queryParam("FID_PERIOD_DIV_CODE", period)
//                            .queryParam("FID_INPUT_DATE_1", startDate)
//                            .queryParam("FID_INPUT_DATE_2", endDate)
//                            .build())
//                .header("authorization", "Bearer " + token)
//                .header("tr_id", "FHKST03010100")
//                .header("custtype", "P")
//                .retrieve()
//                .bodyToMono(KisPeriodStockDto.class)
//                .doOnNext())
}
