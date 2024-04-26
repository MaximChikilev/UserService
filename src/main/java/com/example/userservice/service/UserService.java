package com.example.userservice.service;

import com.example.userservice.dto.ErrorResponseDTO;
import com.example.userservice.dto.UserDTO;
import com.example.userservice.entity.CustomUser;
import com.example.userservice.entity.UserDataErrors;
import com.example.userservice.entity.UserDetails;
import com.example.userservice.repo.UserRepository;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
public class UserService {
  private final UserRepository userRepository;

  @Value("${permissibleAge}")
  private int permissibleAge;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
    this.permissibleAge = permissibleAge;
  }

  @Transactional
  public CustomUser create(UserDTO userDTO) {
    return userRepository.save(getCustomUserFromDTO(userDTO));
  }

  @Transactional
  public List<CustomUser> readAll() {
    return userRepository.findAll();
  }

  @Transactional
  public boolean update(CustomUser customUser) {
    if (userRepository.findById(customUser.getId()).isEmpty()) {
      return false;
    } else {
      userRepository.save(customUser);
      return true;
    }
  }

  @Transactional
  public boolean delete(Long id) {
    boolean isUserExist = userRepository.existsById(id);
    if (isUserExist) userRepository.deleteById(id);
    return isUserExist;
  }

  @Transactional
  public CustomUser getUserById(Long id) {
    return userRepository.findById(id).get();
  }

  @Transactional
  public List<CustomUser> findUsersByBirthdayRange(Date fromDate, Date toDate) {
    return userRepository.findByBirthdayBetween(fromDate, toDate);
  }

  public ErrorResponseDTO dataCorrectnessCheck(UserDetails user) {

    ErrorResponseDTO errorResponse = new ErrorResponseDTO();
    if (!isEmailCorrect(user.getEmail())) {
      errorResponse.addErrorMessage(UserDataErrors.EMAIL.getTitle());
    }
    if ((user.getFirstName() == null) || (user.getFirstName().isEmpty())) {
      errorResponse.addErrorMessage(UserDataErrors.FIRSTNAME.getTitle());
    }
    if ((user.getLastName() == null) || (user.getLastName().isEmpty())) {
      errorResponse.addErrorMessage(UserDataErrors.LASTNAME.getTitle());
    }
    if (!isUserAdult(user.getBirthday())) {
      errorResponse.addErrorMessage(UserDataErrors.DATE_YOUNG_USER.getTitle() + permissibleAge);
    }
    if (isDateInFuture(user.getBirthday())) {
      errorResponse.addErrorMessage(UserDataErrors.DATE_FUTURE.getTitle());
    }
    return errorResponse;
  }

  private boolean isEmailCorrect(String email) {
    return (EmailValidator.getInstance().isValid(email));
  }

  private boolean isUserAdult(Date dateOfBirth) {
    return getYearsBetweenDateAndNow(dateOfBirth) >= permissibleAge;
  }

  private boolean isDateInFuture(Date dateOfBirth) {
    return getYearsBetweenDateAndNow(dateOfBirth) < 0;
  }

  private int getYearsBetweenDateAndNow(Date dateOfBirth) {
    LocalDate today = LocalDate.now();
    LocalDate dob = dateOfBirth.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    Period period = Period.between(dob, today);
    return period.getYears();
  }

  public CustomUser getCustomUserFromDTO(UserDTO userDTO) {
    return CustomUser.builder()
        .email(userDTO.getEmail())
        .firstName(userDTO.getFirstName())
        .lastName(userDTO.getLastName())
        .birthday(userDTO.getBirthday())
        .address(userDTO.getAddress())
        .phoneNumber(userDTO.getPhoneNumber())
        .email(userDTO.getEmail())
        .build();
  }
}
