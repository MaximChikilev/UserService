package com.example.userservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(InvalidFormatException.class)
  public ResponseEntity<Object> handleInvalidFormatException(InvalidFormatException ex) {
    ErrorResponse errorResponse = new ErrorResponse();
    errorResponse.setMessage("Invalid input format: ");
    errorResponse.setDetails("You entered: " + ex.getValue());

    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  static class ErrorResponse {
    private String message;
    private String details;

    public String getMessage() {
      return message;
    }

    public void setMessage(String message) {
      this.message = message;
    }

    public String getDetails() {
      return details;
    }

    public void setDetails(String details) {
      this.details = details;
    }
  }
}
