package com.celebal.route.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusCode {
    BAD_REQUEST(400), UNAUTHORIZED(401), FORBIDDEN(403), NOT_FOUND(404), UNSUPPORTED_MEDIA_TYPE(415), TOO_MANY_REQUESTS(429),
    OK(200), INTERNAL_SERVER_ERROR(500), USER_ALREADY_EXISTS(1), UNPROCESSABLE_ENTITY(422);

    private Integer code;
}
