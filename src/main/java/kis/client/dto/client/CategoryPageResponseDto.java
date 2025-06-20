package kis.client.dto.client;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "카테고리 페이지 응답 DTO")
public class CategoryPageResponseDto {

    @Schema(description = "전체 페이지 수")
    private int totalPages;

    @Schema(description = "현재 페이지 종목")
    private List<CategoryStockResponseDto> stocks;

}
