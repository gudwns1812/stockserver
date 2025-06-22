package stock.mainserver.global.response;

/**
 * 성공 응답 메세지에 대한 DTO 클래스이다.<br>
 * return ResponseEntity.ok(new SuccessResponse<>(true, "성공 메세지", data)); <br> 이렇게 작성하면 된다.
 *
 * @author Hyeongjun
 * @param Success
 * @param message
 * @param data
 * @param <T>
 */
public record SuccessResponse<T>(
        boolean Success,
        String message,
        T data
) {}
