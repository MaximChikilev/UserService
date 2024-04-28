package com.example.userservice.controller;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(InvalidFormatException.class)
  public ResponseEntity<Object> handleInvalidFormatException(InvalidFormatException ex) {
    ErrorResponse errorResponse =
        new ErrorResponse("Invalid input format: ", "You entered: " + ex.getValue());
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }
  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<Object> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
    ErrorResponse errorResponse =
            new ErrorResponse("Missing request parameter: ",  ex.getParameterName());
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<Object> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
    ErrorResponse errorResponse =
            new ErrorResponse("Request method is not supported",ex.getMethod());
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<Object> handleHttpMessageNotReadableException(
      HttpMessageNotReadableException ex) {
    String genericMessage = "Request body is malformed or incorrect.";
    if (ex.getCause() != null) {
      Throwable rootCause = ex.getCause();
      if (rootCause instanceof JsonParseException) {
        genericMessage = "JSON is malformed and cannot be parsed.";
      } else if (rootCause instanceof JsonMappingException) {
        JsonMappingException jme = (JsonMappingException) rootCause;
        genericMessage = "JSON structure does not match expected format: " + jme.getPathReference();
      }
    }
    ErrorResponse errorResponse =
        new ErrorResponse("Unable to process the request", genericMessage);
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<Object> handleNoResourceFoundException(NoResourceFoundException ex) {
    ErrorResponse errorResponse =
        new ErrorResponse(
            "Bad request path", "You entered: " + ex.getHttpMethod() + "/" + ex.getResourcePath());
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<Object> handleMethodArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException ex) {
    ErrorResponse errorResponse =
        new ErrorResponse(
            "Incorrect argument type",
            "You entered: "
                + ex.getValue()
                + ", expected type is "
                + ex.getRequiredType().getSimpleName());
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

    public ErrorResponse(String message, String details) {
      this.message = message;
      this.details = details;
    }
  }
}
