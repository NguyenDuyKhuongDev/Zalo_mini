package com.devmam.zalomini.exception;


import com.devmam.zalomini.dto.response.ErrorResponse;
import com.devmam.zalomini.exception.customizeException.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@RestControllerAdvice
public class RestControllerGlobalExceptionHandler {

    @ExceptionHandler({ResourceNotFoundException.class, AddingFailException.class, AuthenticationFailException.class, UnAuthenticationException.class})
    public ResponseEntity<?> handleException(Exception e, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        if(e instanceof ResourceNotFoundException){
             errorResponse = ErrorResponse.builder()
                    .timestamp(new Date())
                    .status(HttpStatus.BAD_REQUEST.value())
                    .path(request.getDescription(true))
                    .error((HttpStatus.BAD_REQUEST.getReasonPhrase()))
                    .message(e.getMessage())
                    .build();
        }else if(e instanceof AddingFailException){
            errorResponse = ErrorResponse.builder()
                    .timestamp(new Date())
                    .status(HttpStatus.BAD_REQUEST.value())
                    .path(request.getDescription(true))
                    .error((HttpStatus.BAD_REQUEST.getReasonPhrase()))
                    .data(((AddingFailException)e).getData())
                    .message(e.getMessage())
                    .build();
        } else if (e instanceof AuthenticationFailException) {
            errorResponse = ErrorResponse.builder()
                    .timestamp(new Date())
                    .status(HttpStatus.BAD_REQUEST.value())
                    .path(request.getDescription(true))
                    .error((HttpStatus.BAD_REQUEST.getReasonPhrase()))
                    .data(((AuthenticationFailException)e).getData())
                    .message(e.getMessage())
                    .build();
        } else if (e instanceof UnAuthenticationException) {
            errorResponse = ErrorResponse.builder()
                    .timestamp(new Date())
                    .status(HttpStatus.BAD_REQUEST.value())
                    .path(request.getDescription(true))
                    .error((HttpStatus.BAD_REQUEST.getReasonPhrase()))
                    .data(((UnAuthenticationException)e).getData())
                    .message(e.getMessage())
                    .build();
        } else if (e instanceof CallException) {
            errorResponse = ErrorResponse.builder()
                    .timestamp(new Date())
                    .status(HttpStatus.BAD_REQUEST.value())
                    .path(request.getDescription(true))
                    .error((HttpStatus.BAD_REQUEST.getReasonPhrase()))
                    .data(((CallException)e).getData())
                    .message(e.getMessage())
                    .build();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}