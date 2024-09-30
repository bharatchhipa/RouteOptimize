package com.celebal.route.wrapper;


import com.celebal.route.constants.ResponseConstants;
import com.celebal.route.enums.StatusCode;
import com.celebal.route.response.ResponseWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseEntityWrapper {
    public static ResponseEntity<ResponseWrapper> genericException(String message) {
        return new ResponseEntity<>(ResponseWrapper.builder()
                .status(ResponseConstants.FAILURE)
                .message(message)
                .code(StatusCode.INTERNAL_SERVER_ERROR.getCode())
                .build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static ResponseEntity<ResponseWrapper> badRequestException(String message) {
        return new ResponseEntity<>(ResponseWrapper.builder()
                .status(ResponseConstants.FAILURE)
                .message(message)
                .code(StatusCode.BAD_REQUEST.getCode())
                .build(), HttpStatus.BAD_REQUEST);
    }

    public static ResponseEntity<ResponseWrapper> badRequestException(String message, Object data) {
        return new ResponseEntity<>(ResponseWrapper.builder()
                .status(ResponseConstants.FAILURE)
                .message(message)
                .data(data)
                .code(StatusCode.BAD_REQUEST.getCode())
                .build(), HttpStatus.BAD_REQUEST);
    }

    public static ResponseEntity<ResponseWrapper> accessDeniedException(String message) {
        return new ResponseEntity<>(ResponseWrapper.builder()
                .status(ResponseConstants.FAILURE)
                .message(message)
                .code(StatusCode.FORBIDDEN.getCode())
                .build(), HttpStatus.FORBIDDEN);
    }

    public static ResponseEntity<ResponseWrapper> authenticationFailureException(String message) {
        return new ResponseEntity<>(ResponseWrapper.builder()
                .status(ResponseConstants.FAILURE)
                .message(message)
                .code(StatusCode.UNAUTHORIZED.getCode())
                .build(), HttpStatus.UNAUTHORIZED);
    }

    public static ResponseEntity<ResponseWrapper> resourceNotFoundException(String message) {
        return new ResponseEntity<>(ResponseWrapper.builder()
                .status(ResponseConstants.FAILURE)
                .message(message)
                .code(StatusCode.NOT_FOUND.getCode())
                .build(), HttpStatus.NOT_FOUND);
    }

    public static ResponseEntity<ResponseWrapper> unprocessableEntityException(String message) {
        return new ResponseEntity<>(ResponseWrapper.builder()
                .status(ResponseConstants.FAILURE)
                .message(message)
                .code(StatusCode.UNPROCESSABLE_ENTITY.getCode())
                .build(), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    public static ResponseEntity<ResponseWrapper> successResponseBuilder(String message) {
        return new ResponseEntity<>(ResponseWrapper.builder()
                .status(ResponseConstants.SUCCESS)
                .message(message)
                .code(StatusCode.OK.getCode())
                .build(), HttpStatus.OK);
    }

    public static ResponseEntity<ResponseWrapper> successResponseBuilder(String message, Object data) {
        return new ResponseEntity<>(ResponseWrapper.builder()
                .status(ResponseConstants.SUCCESS)
                .message(message)
                .data(data)
                .code(StatusCode.OK.getCode())
                .build(), HttpStatus.OK);
    }

}

