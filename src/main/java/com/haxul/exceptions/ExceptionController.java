package com.haxul.exceptions;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@ControllerAdvice
@RequiredArgsConstructor
public class ExceptionController extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ErrorMessage message = new ErrorMessage("Error", "InvalidMethod", ex.getMessage(), new Date());
        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(message);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ErrorMessage message = new ErrorMessage("Error", "Incorrect request param",ex.getMessage(), new Date());
        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(message);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ErrorMessage message = new ErrorMessage("Error", "Type param does not match",ex.getMessage(), new Date());
        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(message);
    }

    @ExceptionHandler(value = {TimeoutException.class, InterruptedException.class, ExecutionException.class})
    public ResponseEntity<Object> handleUsernameExistException(Exception ex, WebRequest request) {
        ErrorMessage message = new ErrorMessage("Error", "Concurrency problem",  ex.getMessage(), new Date());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body(message);
    }
}