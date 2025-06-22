package stock.mainserver.global.error;

public class TokenFetchException extends RuntimeException {

    public TokenFetchException() {
        super();
    }

    public TokenFetchException(String message) {
        super(message);
    }

    public TokenFetchException(String message, Throwable cause) {
        super(message, cause);
    }
}
