package com.example.patrycja.filmbase;

import com.example.patrycja.filmbase.exception.AlreadyUpToDateException;
import com.example.patrycja.filmbase.exception.DuplicateException;
import com.example.patrycja.filmbase.exception.FilmDoesntExistException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestErrorHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = DuplicateException.class)
    public ResponseEntity<String> handleDuplicateItem(DuplicateException ex, WebRequest request) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = AlreadyUpToDateException.class)
    public ResponseEntity<String> handleDirectorUpToDate(AlreadyUpToDateException ex, WebRequest request) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = FilmDoesntExistException.class)
    public ResponseEntity<String> handleNonExistentFilm(FilmDoesntExistException ex, WebRequest request) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
}
