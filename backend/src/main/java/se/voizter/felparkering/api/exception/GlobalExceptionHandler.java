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
import se.voizter.felparkering.api.enums.Message;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException.class) 
    public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException exception) {
        List<Map<String,String>> errors = exception.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> Map.of(
                "field", error.getField(),
                "message", error.getDefaultMessage()
            ))
            .toList();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("errors", errors));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleEnumMismatch(MethodArgumentTypeMismatchException exception) {
        Class<?> requiredType = exception.getRequiredType();

        if (requiredType != null && requiredType.isEnum()) {
            String enumValues = String.join(", ", Arrays.stream(requiredType.getEnumConstants()).map(Object::toString).toArray(String[]::new));

            String message = String.format("Invalid value '%s' for enum %s. Allowed values are: [%s]", exception.getValue(), requiredType.getSimpleName(), enumValues);

            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", message));
        }

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(Map.of("error", "Invalid request parameter or value"));
    }

    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<?> handlePasswordMismatch(PasswordMismatchException exception) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(UserConflictException.class)
    public ResponseEntity<?> handleUserConflict(UserConflictException exception) {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(MissingCredentialsException.class)
    public ResponseEntity<?> handleMissingCredentials(MissingCredentialsException exception) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<?> handleInvalidCredentials(InvalidCredentialsException exception) {
        HttpStatus status = Message.INVALID_CREDENTIALS.toString().equals(exception.getMessage())
            ? HttpStatus.UNAUTHORIZED
            : HttpStatus.FORBIDDEN;

        return ResponseEntity
            .status(status)
            .body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFound(NotFoundException exception) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(AlreadyAssignedException.class)
    public ResponseEntity<?> handleAlreadyAssigned(AlreadyAssignedException exception) {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(Map.of("error", exception.getMessage()));
    }
}
