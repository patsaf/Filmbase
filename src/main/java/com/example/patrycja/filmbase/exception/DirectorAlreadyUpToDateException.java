package com.example.patrycja.filmbase.exception;

public class DirectorAlreadyUpToDateException extends RuntimeException {

    public DirectorAlreadyUpToDateException() { super(); }

    public DirectorAlreadyUpToDateException(String message) { super(message); }
}
