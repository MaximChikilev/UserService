package com.example.userservice.controller;

import com.example.userservice.dto.ErrorResponseDTO;
import com.example.userservice.dto.UserDTO;
import com.example.userservice.entity.CustomUser;
import com.example.userservice.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@AllArgsConstructor
public class UserController {
  private final UserService userService;

  @PostMapping(value = "/users")
  public ResponseEntity<?> addNewUser(@RequestBody UserDTO userDTO) {
    ErrorResponseDTO errorResponseDTO =
        userService.dataCorrectnessCheck(userDTO);
    if (errorResponseDTO.isErrorsPresent()) {
      return ResponseEntity.badRequest().body(errorResponseDTO.getErrorMessages());
    } else {
      return ResponseEntity.ok(userService.create(userDTO));
    }
  }

  @GetMapping(value = "/users")
  public ResponseEntity<List<CustomUser>> getAllUsers() {
    final List<CustomUser> users = userService.readAll();
    return users != null && !users.isEmpty()
        ? new ResponseEntity<>(users, HttpStatus.OK)
        : new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }
  @GetMapping("/users/byBirthdayRange")
  public ResponseEntity<List<CustomUser>> getUsersByBirthdayRange(
          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fromDate,
          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date toDate) {

    List<CustomUser> users = userService.findUsersByBirthdayRange(fromDate, toDate);
    return users != null && !users.isEmpty()
            ? new ResponseEntity<>(users, HttpStatus.OK)
            : new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }

  @GetMapping(value = "/users/{id}")
  public ResponseEntity<CustomUser> getUsersById(@PathVariable(name = "id") Long id) {
    final CustomUser user = userService.getUserById(id);
    return user != null
        ? new ResponseEntity<>(user, HttpStatus.OK)
        : new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }

  @DeleteMapping(value = "/users/{id}")
  public ResponseEntity<HttpStatus> deleteUser(@PathVariable(name = "id") Long id) {
    return userService.delete(id)
        ? new ResponseEntity<>(HttpStatus.OK)
        : new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }

  @PutMapping(value = "/users")
  public ResponseEntity<?> updateUser(@RequestBody CustomUser user) {
      ErrorResponseDTO errorResponseDTO =
              userService.dataCorrectnessCheck(user);
      if (errorResponseDTO.isErrorsPresent()) {
          return ResponseEntity.badRequest().body(errorResponseDTO);
      } else {
        return userService.update(user)
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
      }
  }

}
