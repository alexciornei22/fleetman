package ro.upb.acs.fleetman;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ro.upb.acs.fleetman.exception.BatchQueueFullException;
import ro.upb.acs.fleetman.exception.BatchQueueFullExceptionDto;
import ro.upb.acs.fleetman.exception.FieldConflictException;
import ro.upb.acs.fleetman.exception.FieldConflictExceptionDto;
import ro.upb.acs.fleetman.exception.InvalidResourceReferenceException;
import ro.upb.acs.fleetman.exception.InvalidResourceReferenceExceptionDto;
import ro.upb.acs.fleetman.exception.MultipleArgumentsNotValidDto;
import ro.upb.acs.fleetman.exception.ResourceNotFoundException;
import ro.upb.acs.fleetman.exception.ResourceNotFoundExceptionDto;
import ro.upb.acs.fleetman.exception.SingleArgumentNotValidDto;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FieldConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public FieldConflictExceptionDto handleFieldConflictException(FieldConflictException ex) {
        return new FieldConflictExceptionDto(ex.getErrorField(), ex.getMessage());
    }

    @ExceptionHandler(InvalidResourceReferenceException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
    public InvalidResourceReferenceExceptionDto handleInvalidResourceReferenceException(InvalidResourceReferenceException ex) {
        return new InvalidResourceReferenceExceptionDto(ex.getResourceType(), ex.getResourceId(), ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResourceNotFoundExceptionDto handleResourceNotFoundException(ResourceNotFoundException ex) {
        return new ResourceNotFoundExceptionDto(ex.getResourceType(), ex.getResourceId(), ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public MultipleArgumentsNotValidDto handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<SingleArgumentNotValidDto> errors = ex.getFieldErrors().stream()
            .map(error -> new SingleArgumentNotValidDto(error.getField(), error.getDefaultMessage()))
            .toList();

        return new MultipleArgumentsNotValidDto(errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public SingleArgumentNotValidDto handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        return new SingleArgumentNotValidDto("requestBody", "Malformed JSON request");
    }

    @ExceptionHandler(BatchQueueFullException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public BatchQueueFullExceptionDto handleBatchQueueFullException(BatchQueueFullException ex) {
        return new BatchQueueFullExceptionDto(ex.getCapacity(), ex.getMessage());
    }
}
