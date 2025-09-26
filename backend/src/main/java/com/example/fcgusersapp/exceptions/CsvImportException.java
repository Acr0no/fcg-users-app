package com.example.fcgusersapp.exceptions;

/**
 * Exception thrown when an error occurs while importing users from a CSV file.
 * <p>
 * This is a custom runtime exception used to signal validation errors,
 * parsing failures, or other issues encountered during the CSV import process.
 */
public class CsvImportException extends RuntimeException {
    public CsvImportException(String message) {
        super(message);
    }
}
