package com.catalogservice.exceptions;

public class DuplicateProductException extends RuntimeException{
    public DuplicateProductException(String message) {
        super(message);
    }
}
