package com.jobly.util;

import com.jobly.gen.model.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionHandlerUtils {

    public static BadRequestMessage getBadRequestError(String message) {
        var error = new BadRequestMessage();
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setMessage(message);
        error.setTimestamp(DateUtils.getCurrentUtcTime());
        return error;
    }

    public static UnauthorizedMessage getUnauthorizedError(String message) {
        var error = new UnauthorizedMessage();
        error.setStatus(HttpStatus.UNAUTHORIZED.value());
        error.setMessage(message);
        error.setTimestamp(DateUtils.getCurrentUtcTime());
        return error;
    }

    public static ForbiddenMessage getForbiddenError(String message) {
        var error = new ForbiddenMessage();
        error.setStatus(HttpStatus.FORBIDDEN.value());
        error.setMessage(message);
        error.setTimestamp(DateUtils.getCurrentUtcTime());
        return error;
    }

    public static NotFoundMessage getNotFoundError(String message) {
        var error = new NotFoundMessage();
        error.setStatus(HttpStatus.NOT_FOUND.value());
        error.setMessage(message);
        error.setTimestamp(DateUtils.getCurrentUtcTime());
        return error;
    }

    public static ConflictMessage getConflictError(String message) {
        var error = new ConflictMessage();
        error.setStatus(HttpStatus.CONFLICT.value());
        error.setMessage(message);
        error.setTimestamp(DateUtils.getCurrentUtcTime());
        return error;
    }

    public static InternalServerErrorMessage getInternalServerError(String s) {
        var error = new InternalServerErrorMessage();
        error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.setMessage(s);
        error.setTimestamp(DateUtils.getCurrentUtcTime());
        return error;
    }
}
