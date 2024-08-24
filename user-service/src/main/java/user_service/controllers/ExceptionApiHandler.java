package user_service.controllers;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionApiHandler {
    
    @ExceptionHandler(IOException.class)
    public void handleIOException(IOException ex){
    }

    @ExceptionHandler(Exception.class)
    public void handleException(Exception ex) {
    }

}