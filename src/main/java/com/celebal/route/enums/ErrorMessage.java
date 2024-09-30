package com.celebal.route.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorMessage {
    GENERIC("Oops! Something went wrong, please try again."),
    BAD_REQUEST("Invalid request"),
    PROCESSING_ERROR("Oops! Something went wrong while processing the data. Please try again."),
    ACCESS_DENIED("You are not authorised to access this portal, please login from correct portal");
    private String message;
}
