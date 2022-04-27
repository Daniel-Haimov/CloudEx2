package catalog.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
public class ProductExistException extends RuntimeException {

    private static final long serialVersionUID = -910099900391921297L;

    public ProductExistException() {
        super();
    }

    public ProductExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProductExistException(String message) {
        super(message);
    }

    public ProductExistException(Throwable cause) {
        super(cause);
    }
}