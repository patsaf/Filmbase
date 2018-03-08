package com.example.patrycja.filmbase.exception;

public class FilmDoesntExistException extends RuntimeException {

    public FilmDoesntExistException() {
        super();
    }
    public FilmDoesntExistException(String message) { super(message); }
}
