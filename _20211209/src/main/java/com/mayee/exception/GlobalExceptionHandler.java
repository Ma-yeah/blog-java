package com.mayee.exception;

import com.mayee.model.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BindException.class)
    public R bindExceptionHandler(BindException e) {
        log.debug("bindExcept: {}", e.getMessage());
        FieldError fieldError = e.getBindingResult().getFieldError();
        assert fieldError != null;
        return R.fail(String.format("%s %s", fieldError.getField(), fieldError.getDefaultMessage()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public R validationExceptionHandler(ConstraintViolationException e) {
        log.debug("validationException: {}", e.getMessage());
        ConstraintViolation<?> constraintViolation = e.getConstraintViolations().stream().findFirst().orElse(null);
        assert constraintViolation != null;
        String[] path = constraintViolation.getPropertyPath().toString().split("\\.");
        String field = path[path.length - 1];
        String message = constraintViolation.getMessage();
        return R.fail(String.format("%s %s", field, message));
    }

    @ExceptionHandler(Exception.class)
    public R exceptionHandler(Exception e) {
        log.debug("exception: {}", e.getMessage());
        return R.fail(e.getMessage());
    }
}
