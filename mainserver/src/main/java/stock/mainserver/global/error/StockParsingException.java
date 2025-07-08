package stock.mainserver.global.error;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StockParsingException extends RuntimeException {

    public StockParsingException(String message) {
        super(message);
    }

    public StockParsingException(String message, Throwable cause) {
        super(message, cause);
    }

    public StockParsingException(JsonProcessingException e) {
        log.error(e.getMessage(), e);
    }
}
