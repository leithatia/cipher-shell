package com.leithatia.ciphershell.exceptions;

/**
 * Exception thrown when a cryptographic operation fails.
 */
public class CryptoException extends Exception {

    /**
     * Constructs a new CryptoException with the specified detail message.
     *
     * @param message the detail message.
     */    public CryptoException(String message) {
        super(message);
    }

    /**
     * Constructs a new CryptoException with the specified detail message and cause.
     *
     * @param message the detail message.
     * @param cause the cause of the exception.
     */
    public CryptoException(String message, Throwable cause) {
        super(message, cause);
    }
}
