package com.example.patrycja.filmbase.exception;

public class AlreadyUpToDateException extends RuntimeException {

    public AlreadyUpToDateException() {
        super();
    }

    public AlreadyUpToDateException(String message) {
        super(message);
    }
}
