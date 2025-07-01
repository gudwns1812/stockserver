package stock.mainserver.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stock.mainserver.dto.redis.FxRedisDto;
import stock.mainserver.dto.redis.IndicesRedisDto;
import stock.mainserver.dto.redis.StockDto;
import stock.mainserver.dto.response.PopularStockResponseDto;
import stock.mainserver.dto.response.StockPeriodResponseDto;
import stock.mainserver.global.response.SuccessResponse;
import stock.mainserver.service.FxService;
import stock.mainserver.service.IndicesService;
import stock.mainserver.service.PopularService;
import stock.mainserver.service.StockService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "주식 서버 API")
@RequestMapping("/api/v2/stocks")
public class StockRealTimeController {

    private final IndicesService indicesService;
    private final PopularService popularService;
    private final StockService stockService;
    private final FxService fxService;

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
            description = "해당 기간의 주식 가격을 반환합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "주식 가격 조회 성공",
                            content = @Content(schema = @Schema(implementation = StockPeriodResponseDto.class)))
            })
    @Parameter(name = "period" ,example = "D,W,M,Y")
    @GetMapping("/{stockCode}")
    public ResponseEntity<?> StockPrice(@PathVariable String stockCode,
                                        @RequestParam String period,
                                        @RequestParam LocalDate startDate,
                                        @RequestParam LocalDate endDate) {
        List<StockPeriodResponseDto> response = stockService.getStockPeriodInfo(stockCode, period, startDate, endDate);
        return ResponseEntity.ok(new SuccessResponse<>(true,"주식 기간별 과거 데이터 조회에 성공하였습니다",response));
    }

    @Operation(summary = "주식 실시간 정보 조회",
            description = "특정 주식의 현재 가격을 조회합니다. ",
            responses = {
                    @ApiResponse(responseCode = "200", description = "주식 가격 조회 성공",
                            content = @Content(schema = @Schema(implementation = StockDto.class)))
            })
    @GetMapping("/info/{stockCode}")
    public ResponseEntity<?> getStockPrice(@PathVariable String stockCode) {
        StockDto stockInfo = stockService.getStockInfo(stockCode);
        return ResponseEntity.ok(new SuccessResponse<>(true,"주식 현재 정보 조회 성공",stockInfo));
    }

    @Operation(
            summary = "FX, 원자재, 채권 조회",
            description = "FX, 원자재, 채권 조회합니다. type 에 종류를 적고, symbol에 조회할 종목이름을 적습니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "카테고리 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = FxRedisDto.class)
                            )
                    )
            }
    )
    @Parameters({
            @Parameter(name = "type", description = "조회할 주식 타입", example = "FX , Feed, Bonds"),
            @Parameter(name = "symbol", description = "조회할 코드 이름", example = "FX: dollar, yen, DOW, NQ, SP500, Nikkei, HangSeng"
                    + ", ShangHai , Feed: GOLD, SILVER, WTI, CORN, COFFEE ,COTTON , Bonds: KRBONDS 1,3,5,10,20,30 USBONDS 1,10,30")
    })
    @GetMapping("/FX")
    public ResponseEntity<?> getFX(@RequestParam String type,
                                   @RequestParam String symbol) {
        FxRedisDto fxInfo = fxService.getFxInfo(type, symbol);
        return ResponseEntity.ok(new SuccessResponse<>(true,"외환 지수 조회에 성공하였습니다.", fxInfo));
    }

}
