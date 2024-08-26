package user_service.controllers;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

import org.springframework.web.bind.annotation.ExceptionHandler;

import user_service.exceptions.BadRequestException;

@ControllerAdvice
public class ExceptionApiHandler {

    @Autowired
    private Logger logger;


    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleIOException(IOException ex) {
        logger.warning(ex.getMessage());
        return ResponseEntity.status(500).body(ex.toString());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        logger.warning(ex.getMessage());
        return ResponseEntity.status(500).body(ex.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> handleBadRequestException(Exception ex) {
        logger.warning(ex.getMessage());
        return ResponseEntity.status(400).body(ex.getMessage());
    }

}
