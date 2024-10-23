package com.daelim.sfa.controller;

import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.sql.SQLIntegrityConstraintViolationException;

@RestControllerAdvice
public class CommonExceptionHandler {

    //널포인터 포함
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException() {
        return new ResponseEntity<>("런타임 에러가 발생했습니다", HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<String> NoResourceFoundException() {
        return new ResponseEntity<>("404 - 존재하지 않는 페이지입니다", HttpStatus.CONFLICT);
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<String> SQLIntegrityConstraintViolationException() {
        return new ResponseEntity<>("중복된 데이터입니다.", HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<String> MissingServletRequestParameterException(MissingServletRequestParameterException exception) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(exception.getParameterName()).append(" ").append("가 누락됐습니다");
        return new ResponseEntity<>(stringBuilder.toString(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<String> MissingPathVariableException(MissingServletRequestParameterException exception) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(exception.getParameterName()).append(" ").append("가 누락됐습니다");
        return new ResponseEntity<>(stringBuilder.toString(), HttpStatus.BAD_REQUEST);
    }


    /*
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<String> MissingServletRequestParameterException() {
        return new ResponseEntity<>("MissingServletRequestParameterException 발생했습니다", HttpStatus.CONFLICT);
    }
     */


    /*

    @ExceptionHandler(ConversionFailedException.class)
    public ResponseEntity<String> handleConflict(RuntimeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
    */


}
