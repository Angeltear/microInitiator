package com.angeltear.microinitiator.Exceptions;

import com.angeltear.microinitiator.Model.PaymentResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    /*
     * Basic error handling. Handle all the BAD_REQUEST responses and
     * return the map as the body for all the bad requests.
     * */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    Map<String, String> handleBadRequests(Exception e) {

        Map<String, String> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("status", "Invalid input!");
        response.put("detailedMessage", e.getLocalizedMessage());


        return response;
    }

    /*
     * Basic error handling. Handle specific BAD_REQUEST responses that failed due to validation.
     * Return the response object with the constraints within the detailed message
     * of the response body for each of the validation failed requests.
     * */

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<PaymentResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        PaymentResponse response = new PaymentResponse();
        response.setTimestamp(LocalDateTime.now());
        response.setDetailedMessage(errors.values().toString());
        response.setResult("Invalid input!");

        return ResponseEntity.badRequest().body(response);
    }


}
