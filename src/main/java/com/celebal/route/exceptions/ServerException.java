package com.celebal.route.exceptions;

public class ServerException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ServerException(String message) {
        super(message);
    }
}
