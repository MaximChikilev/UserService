package com.example.userservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ErrorResponseDTO {
  private List<String> errorMessages = new ArrayList<>();

  public void addErrorMessage(String errorMessage) {
    errorMessages.add(errorMessage);
  }

  public boolean isErrorsPresent() {
    return (!errorMessages.isEmpty());
  }

  public List<String> getErrorMessages() {
    return errorMessages;
  }
}
