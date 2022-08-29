package foodhelper.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class NutrientValueNotFoundException extends RuntimeException{
    public NutrientValueNotFoundException(String message) {
        super(message);
    }
}
