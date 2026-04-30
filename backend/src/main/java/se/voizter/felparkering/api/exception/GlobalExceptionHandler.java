package se.voizter.felparkering.api.exception;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import se.voizter.felparkering.api.exception.exceptions.AlreadyAssignedException;
import se.voizter.felparkering.api.exception.exceptions.InvalidCredentialsException;
import se.voizter.felparkering.api.exception.exceptions.MissingCredentialsException;
import se.voizter.felparkering.api.exception.exceptions.NotFoundException;
import se.voizter.felparkering.api.exception.exceptions.PasswordMismatchException;
import se.voizter.felparkering.api.exception.exceptions.UserConflictException;
import se.voizter.felparkering.api.dto.ErrorBody;
import se.voizter.felparkering.api.dto.ErrorResponse;
import se.voizter.felparkering.api.dto.FieldErrorDto;
import se.voizter.felparkering.api.dto.ValidationErrorResponse;
import se.voizter.felparkering.api.enums.Message;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException.class) 
    public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException exception) {
        List<FieldErrorDto> errors = exception.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> new FieldErrorDto(
                error.getField(),
                error.getDefaultMessage()
            ))
            .toList();

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ValidationErrorResponse(errors));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleEnumMismatch(MethodArgumentTypeMismatchException exception) {
        Class<?> requiredType = exception.getRequiredType();

        if (requiredType != null && requiredType.isEnum()) {
            String enumValues = String.join(", ", Arrays.stream(requiredType.getEnumConstants()).map(Object::toString).toArray(String[]::new));

            String message = String.format("Invalid value '%s' for enum %s. Allowed values are: [%s]", exception.getValue(), requiredType.getSimpleName(), enumValues);
            
            return error(HttpStatus.BAD_REQUEST, "BAD_REQUEST", message);
        }

        return error(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "Invalid request parameter or value");
    }

    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<ErrorResponse> handlePasswordMismatch(PasswordMismatchException exception) {
        return error(HttpStatus.BAD_REQUEST, "BAD_REQUEST", exception.getMessage());
    }

    @ExceptionHandler(UserConflictException.class)
    public ResponseEntity<ErrorResponse> handleUserConflict(UserConflictException exception) {
        return error(HttpStatus.CONFLICT, "CONFLICT", exception.getMessage());
    }

    @ExceptionHandler(MissingCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleMissingCredentials(MissingCredentialsException exception) {
        return error(HttpStatus.BAD_REQUEST, "BAD_REQUEST", exception.getMessage());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException exception) {
        boolean isLoginFailure = Message.INVALID_CREDENTIALS.toString().equals(exception.getMessage());

        HttpStatus status = isLoginFailure
            ? HttpStatus.UNAUTHORIZED
            : HttpStatus.FORBIDDEN;

        String code = isLoginFailure
            ? "INVALID_CREDENTIALS"
            : "ACCESS_DENIED";

        return error(status, code, exception.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException exception) {
        return error(HttpStatus.NOT_FOUND, "NOT_FOUND", exception.getMessage());
    }

    @ExceptionHandler(AlreadyAssignedException.class)
    public ResponseEntity<?> handleAlreadyAssigned(AlreadyAssignedException exception) {
        return error(HttpStatus.CONFLICT, "CONFLICT", exception.getMessage());
    }

    private ResponseEntity<ErrorResponse> error(HttpStatus status, String code, String message) {
        return ResponseEntity
            .status(status)
            .body(new ErrorResponse(new ErrorBody(code, message)));
    }
}
