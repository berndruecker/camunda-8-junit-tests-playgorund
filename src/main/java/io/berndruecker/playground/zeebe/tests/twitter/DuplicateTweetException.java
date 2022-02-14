package io.berndruecker.playground.zeebe.tests.twitter;

public class DuplicateTweetException extends Exception {
    public DuplicateTweetException(String message) {
        super(message);
    }

    public DuplicateTweetException(String message, Throwable cause) {
        super(message, cause);
    }
}
