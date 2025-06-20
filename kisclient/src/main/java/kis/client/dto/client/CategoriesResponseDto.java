package kis.client.dto.client;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "카테고리 응답 DTO")
public class CategoriesResponseDto {

    @Schema(description = "카테고리 이름")
    private String categoryName;

}
