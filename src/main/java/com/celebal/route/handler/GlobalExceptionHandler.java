package com.celebal.route.handler;


import com.celebal.route.constants.ResponseConstants;
import com.celebal.route.enums.ErrorMessage;
import com.celebal.route.enums.StatusCode;
import com.celebal.route.exceptions.*;
import com.celebal.route.response.ResponseWrapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Log4j2
@ControllerAdvice
public class GlobalExceptionHandler {

//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<ResponseWrapper> handleFieldsException(MethodArgumentNotValidException mve) {
//        Map<String, String> errors = new HashMap<>();
//
//        // Iterate over all errors
//        mve.getBindingResult().getAllErrors().forEach(error -> {
//            String fieldName = "";
//            if (error instanceof FieldError) {
//                fieldName = ((FieldError) error).getField();
//            } else {
//                fieldName = error.getObjectName();
//            }
//            String errorMessage = error.getDefaultMessage();
//            errors.put(fieldName, errorMessage);
//        });
//
//        // Construct and return ResponseEntity with ResponseWrapper
//        ResponseWrapper responseWrapper = ResponseWrapper.builder()
//                .message(ErrorMessage.BAD_REQUEST.getMessage())
//                .data(errors)
//                .code(StatusCode.BAD_REQUEST.getCode())
//                .status(ResponseConstants.FAILURE)
//                .build();
//
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseWrapper);
//    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseWrapper> handleFieldsException(MethodArgumentNotValidException mve) {
        Map<String, String> errors = new HashMap<>();

        // Iterate over all errors
        mve.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        // Construct and return ResponseEntity with ResponseWrapper
        ResponseWrapper responseWrapper = ResponseWrapper.builder()
                .message(ErrorMessage.BAD_REQUEST.getMessage())
                .data(errors)
                .code(StatusCode.BAD_REQUEST.getCode())
                .status(ResponseConstants.FAILURE)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseWrapper);
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<ResponseWrapper> handleConstraintViolationException(ConstraintViolationException e) {
        log.error("Bad Request exception : ", e);
        Set<String> violations = new HashSet<>();
        for (ConstraintViolation violation : e.getConstraintViolations()) {
            violations.add(violation.getMessage());
        }
        return new ResponseEntity<>(ResponseWrapper.builder().status(ResponseConstants.FAILURE).message(ErrorMessage.BAD_REQUEST.getMessage()).data(violations).code(StatusCode.BAD_REQUEST.getCode()).build(), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(value = BadRequestException.class)
    public ResponseEntity<ResponseWrapper> handleBadRequestException(BadRequestException bde) {
        log.error("Bad Request exception : ", bde);
        return new ResponseEntity<>(ResponseWrapper.builder().message(bde.getMessage()).code(StatusCode.BAD_REQUEST.getCode()).status(ResponseConstants.FAILURE).build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = DataNotFoundException.class)
    public ResponseEntity<ResponseWrapper> handleDataNotFoundException(DataNotFoundException dnfe) {
        log.error("Data Not Found exception : ", dnfe);
        return new ResponseEntity<>(ResponseWrapper.builder().message(dnfe.getMessage()).code(StatusCode.NOT_FOUND.getCode()).status(ResponseConstants.FAILURE).build(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = DataValidationException.class)
    public ResponseEntity<ResponseWrapper> handleDataNotFoundException(DataValidationException dve) {
        log.error("Data Validation exception : ", dve);
        return new ResponseEntity<>(ResponseWrapper.builder().message(dve.getMessage()).code(StatusCode.BAD_REQUEST.getCode()).status(ResponseConstants.FAILURE).build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<ResponseWrapper> handleAccessDeniedException(AccessDeniedException ade) {
        log.error("AccessDenied exception : ", ade);
        return new ResponseEntity<>(ResponseWrapper.builder().message(ade.getMessage()).code(StatusCode.FORBIDDEN.getCode()).status(ResponseConstants.FAILURE).build(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({TooManyRequestException.class})
    public ResponseEntity<ResponseWrapper> handleTooManyRequestException(TooManyRequestException tme) {
        log.error("Too many request exception : ", tme);
        return new ResponseEntity<>(ResponseWrapper.builder().message(tme.getMessage()).code(StatusCode.TOO_MANY_REQUESTS.getCode()).status(ResponseConstants.FAILURE).build(), HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler(value = ServerException.class)
    public ResponseEntity<ResponseWrapper> handleServerException(ServerException se) {
        log.error("Server exception detected : ", se);
        return new ResponseEntity<>(ResponseWrapper.builder().message(se.getMessage()).code(StatusCode.INTERNAL_SERVER_ERROR.getCode()).status(ResponseConstants.FAILURE).build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ResponseWrapper> handleRuntimeException(RuntimeException e) {
        log.error("Runtime exception detected : ", e);
        return new ResponseEntity<>(ResponseWrapper.builder().message(ErrorMessage.GENERIC.getMessage()).code(StatusCode.INTERNAL_SERVER_ERROR.getCode()).build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ResponseWrapper> handleGenericException(Exception e) {
        log.error("An exception detected : ", e);
        return new ResponseEntity<>(ResponseWrapper.builder().message(ErrorMessage.GENERIC.getMessage()).code(StatusCode.INTERNAL_SERVER_ERROR.getCode()).build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = DataIntegrityViolationException.class)
    public ResponseEntity<ResponseWrapper> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        log.error("Data Integrity SQL exception detected : ",e);
        return new ResponseEntity<>(ResponseWrapper.builder().message(ErrorMessage.PROCESSING_ERROR.getMessage()).code(StatusCode.INTERNAL_SERVER_ERROR.getCode()).build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
