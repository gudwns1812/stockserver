package stock.mainserver.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import stock.mainserver.dto.redis.IndicesRedisDto;
import stock.mainserver.dto.response.PopularStockResponseDto;
import stock.mainserver.dto.response.StockPeriodResponseDto;
import stock.mainserver.global.response.SuccessResponse;
import stock.mainserver.service.IndicesService;
import stock.mainserver.service.PopularService;
import stock.mainserver.service.StockService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StockController {

    private final IndicesService indicesService;
    private final PopularService popularService;
    private final StockService stockService;

    @Operation(summary = "주요 지수 일자별 조회",
            description = "특정 시장의 주요 지수를 일자별로 조회합니다. KOSPI와 KOSDAQ 시장을 지원합니다. " +
                    "시작 날짜와 종료 날짜는 String 형식으로 입력해야 합니다.ex) 20250220",
            responses = {
                    @ApiResponse(responseCode = "200", description = "지수 조회 성공",
                            content = @Content(schema = @Schema(implementation = IndicesRedisDto.class)))
            })
    @Parameter(name = "market", example = "KOSPI , KOSDAQ")
    @GetMapping("/indices/{market}")
    public ResponseEntity<?> getIndicesByMarket(@PathVariable String market) {
        log.info("getIndicesByMarket called with market: {}", market);
        IndicesRedisDto indicesInfo = indicesService.getIndicesInfo(market);
        return ResponseEntity.ok(new SuccessResponse<>(true,"지수 조회에 성공하였습니다.",indicesInfo));
    }

    @Operation(summary = "상위 6개 인기 종목 조회",
            description = "인기 종목을 조회합니다. 인기 종목은 KIS API를 통해 제공됩니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "인기 종목 조회 성공",
                            content = @Content(schema = @Schema(implementation = PopularStockResponseDto.class)))
            })
    @GetMapping("/popular")
    public ResponseEntity<?> getPopularStocks() {
        List<PopularStockResponseDto> response = popularService.getPopularTop6Stock();
        return ResponseEntity.ok(new SuccessResponse<>(true,"인기 종목 조회에 성공하였습니다.",response));
    }

    @Operation(summary = "주식 기간별 과거 데이터 조회",
            description = "특정 주식의 현재 가격 또는 기간별 가격을 조회합니다. " +
                    "기간을 지정하지 않으면 현재 가격을 반환하며, 기간을 지정하면 해당 기간의 주식 가격을 반환합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "주식 가격 조회 성공",
                            content = @Content(schema = @Schema(implementation = StockPeriodResponseDto.class)))
            })
    @Parameter(name = "period" ,example = "D,W,M,Y")
    @GetMapping("/{stockCode}")
    public ResponseEntity<?> StockPrice(@PathVariable String stockCode,
                                        @RequestParam String period) {
        List<StockPeriodResponseDto> response = stockService.getStockPeriodInfo(stockCode, period);
        return ResponseEntity.ok(new SuccessResponse<>(true,"주식 기간별 과거 데이터 조회에 성공하였습니다",response));
    }

}
