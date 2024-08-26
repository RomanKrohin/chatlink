package user_service.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import java.util.logging.Logger;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionApiHandler {

    @Autowired
    private Logger logger;

    @ExceptionHandler(IOException.class)
    public void handleIOException(IOException ex){
        logger.warning(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public void handleException(Exception ex) {
        logger.warning(ex.getMessage());
    }

}