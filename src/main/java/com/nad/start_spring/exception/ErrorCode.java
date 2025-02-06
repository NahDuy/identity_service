package com.nad.start_spring.exception;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {

    UNCATEGORIZED_EXCEPTION(9999,"UNCATEGORIZED EXCEPTION", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001,"INVALID KEY", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002,"Username exists",HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003,"Username must be at least 3 character",HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1004,"Password must be at least 8 character",HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005,"Username not exists",HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006,"Unauthenticated",HttpStatus.UNAUTHORIZED),
    INVALID_JWS_HEADER(1007,"Invalid JWS header",HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(1008, "You do not have permission", HttpStatus.FORBIDDEN),
    ;
    private int code;
    private String message;
    private HttpStatusCode httpStatusCode;

    ErrorCode(int code, String message, HttpStatusCode httpStatusCode) {
        this.code = code;
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }
}
