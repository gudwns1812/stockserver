package kis.client.dto.client;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "카테고리별 주식 응답 DTO")
public class CategoryStockResponseDto {

    @Schema(description = "주식 이름")
    private String StockName;

    @Schema(description = "주식 코드")
    private String StockCode;

    @Schema(description = "주식 현재가")
    private String CurrentPrice;

    @Schema(description = "주식 전일 대비")
    private String ChangeRate;

    @Schema(description = "주식 전일 대비 기호")
    private String Sign;

    @Schema(description = "주식 전일 대비 가격")
    private String ChangeAmount;

    private String stockImage;
}
