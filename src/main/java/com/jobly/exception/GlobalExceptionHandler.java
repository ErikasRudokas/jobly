package com.jobly.exception;

import com.jobly.exception.general.NotFoundException;
import com.jobly.exception.specific.*;
import com.jobly.gen.model.*;
import com.jobly.util.ExceptionHandlerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({InvalidCredentialsException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<BadRequestMessage> handleBadRequestExceptions(Exception ex) {
        log.error(ex.getMessage());
        var badRequestMessage = ExceptionHandlerUtils.getBadRequestError(ex.getMessage());
        return new ResponseEntity<>(badRequestMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({IncorrectTokenUsageException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<UnauthorizedMessage> handleUnauthorizedExceptions(Exception ex) {
        log.error(ex.getMessage());
        var unauthorizedMessage = ExceptionHandlerUtils.getUnauthorizedError(ex.getMessage());
        return new ResponseEntity<>(unauthorizedMessage, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({UserNotFoundException.class, NotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<NotFoundMessage> handleNotFoundExceptions(Exception ex) {
        log.error(ex.getMessage());
        var notFoundMessage = ExceptionHandlerUtils.getNotFoundError(ex.getMessage());
        return new ResponseEntity<>(notFoundMessage, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({NotUniqueEmailException.class, NotUniqueUsernameException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ConflictMessage> handleConflictExceptions(Exception ex) {
        log.error(ex.getMessage());
        var conflictMessage = ExceptionHandlerUtils.getConflictError(ex.getMessage());
        return new ResponseEntity<>(conflictMessage, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<InternalServerErrorMessage> handleAllUncaughtException(Exception ex) {
        log.error("Unknown error occurred: ", ex);
        var internalServerError = ExceptionHandlerUtils.getInternalServerError("An unknown error occurred.");
        return new ResponseEntity<>(internalServerError, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
