package stock.mainserver.global.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import stock.mainserver.global.error.IndicesNotFoundException;
import stock.mainserver.global.error.PopularNotFoundException;

@RestControllerAdvice
@Slf4j
public class StockGlobalExceptionHandler {

    @ExceptionHandler(IndicesNotFoundException.class)
    public ResponseEntity<?> handleIndicesNotFoundException(IndicesNotFoundException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(PopularNotFoundException.class)
    public ResponseEntity<?> handlePopularNotFoundException(PopularNotFoundException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}
