package com.example.patrycja.filmbase.exception;

public class InvalidIdException extends RuntimeException {

    public InvalidIdException() {
        super();
    }

    public InvalidIdException(String message) {
        super(message);
    }
}
