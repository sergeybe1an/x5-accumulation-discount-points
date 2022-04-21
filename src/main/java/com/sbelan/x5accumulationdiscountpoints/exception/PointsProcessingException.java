package com.sbelan.x5accumulationdiscountpoints.exception;

public class PointsProcessingException extends Exception {
    private String message;

    public PointsProcessingException(String message) {
        super(message);
    }
}
