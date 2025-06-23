package stock.mainserver.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stock.mainserver.dto.request.StockCountRequestDto;
import stock.mainserver.dto.response.CategoriesResponseDto;
import stock.mainserver.dto.response.CategoryPageResponseDto;
import stock.mainserver.dto.response.SearchResponseDto;
import stock.mainserver.global.response.SuccessResponse;
import stock.mainserver.service.StockService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "주식 API")
@RequestMapping("/api/v2/stocks")
public class StockApiController {

    private final StockService stockService;

    @Operation(
            summary = "카테고리 조회",
            description = "주식 카테고리를 조회합니다. 다양한 카테고리를 지원합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "카테고리 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CategoriesResponseDto.class)
                            )
                    )
            }
    )
    @GetMapping("/category")
    public ResponseEntity<?> getCategories() {
        List<CategoriesResponseDto> dto = stockService.AllCategories();
        return ResponseEntity.ok(new SuccessResponse<>(true,"모든 카테고리 조회에 성공했습니다.",dto));
    }

    @Operation(
            summary = "카테고리별 종목 조회",
            description = "특정 카테고리에 속하는 주식 종목을 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "카테고리별 종목 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CategoryPageResponseDto.class)
                            )
                    )
            }
    )
    @GetMapping("/category/{categoryName}")
    public ResponseEntity<?> getCategoryStock(@PathVariable String categoryName,
                                              @RequestParam(defaultValue = "1") int page) {
        CategoryPageResponseDto categoryPageResponseDto = stockService.CategoryStocks(categoryName, page);
        return ResponseEntity.ok(new SuccessResponse<>(true,"카테고리 종목 조회에 성공하였습니다.", categoryPageResponseDto));

    }

    @Operation(
            summary = "주식 검색",
            description = "주식 종목을 검색합니다. 키워드가 없으면 인기 검색 종목을 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "주식 검색 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SearchResponseDto.class)
                            )
                    )
            }
    )

    @GetMapping("/search")
    public ResponseEntity<?> searchStocks(@RequestParam(required = false) String keyword) {
        log.info("searchStocks called with keyword: {}", keyword);
        List<SearchResponseDto> searchResponseDtos = stockService.getSearchStock(keyword);
        if (keyword == null || keyword.isEmpty()) {
            return ResponseEntity.ok(new SuccessResponse<>(false, "검색어가 비어있습니다.", searchResponseDtos));
        }
        return ResponseEntity.ok(new SuccessResponse<>(true, "검색 결과 조회에 성공하였습니다.", searchResponseDtos));
    }

    @Operation(
            summary = "주식 검색 Count 증가",
            description = "주식 검색 Count를 +1 증가시킵니다.",

            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "주식 검색 Count 증가 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SuccessResponse.class)
                            )
                    )
            }
    )
    @PostMapping("/search")
    public ResponseEntity<?> StockCounter(@RequestBody StockCountRequestDto dto) {
        stockService.stockSearchCounter(dto.getStockCode());
        return ResponseEntity.ok(new SuccessResponse<>(true, "주식 검색 Count + 1 성공", null));
    }
}
