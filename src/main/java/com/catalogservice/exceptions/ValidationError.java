package com.catalogservice.exceptions;

public record ValidationError(String field, String message) {
}
