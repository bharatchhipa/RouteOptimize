package com.celebal.route.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseWrapper {
    private String message;

    private Integer code;

    private Object data;

    private String status;

    private String errorCode;

    public ResponseWrapper(String status, Integer code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
