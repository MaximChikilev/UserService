package com.example.userservice;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.userservice.controller.UserController;
import com.example.userservice.dto.ErrorResponseDTO;
import com.example.userservice.dto.UserDTO;
import com.example.userservice.entity.CustomUser;
import com.example.userservice.entity.UserDataErrors;
import com.example.userservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
  @Mock private UserService userService;
  @InjectMocks private UserController userController;
  private MockMvc mockMvc;
  private ObjectMapper objectMapper;
  private ErrorResponseDTO errorResponseDTO;
  private SimpleDateFormat sdf;
  private UserDTO userDTO;
  Date expectedDate;

  @BeforeEach
  void setUp() throws ParseException {
    mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    errorResponseDTO = new ErrorResponseDTO();
    objectMapper = new ObjectMapper();
    sdf = new SimpleDateFormat("yyyy-MM-dd");
    expectedDate = sdf.parse("1980-10-10");
  }

  @Test
  void addNewUser() throws Exception {
    userDTO = new UserDTO("maxim.chikilev@gmail.com", "Maxim", "Chikilev", expectedDate, "", "");
    CustomUser customUser =
        new CustomUser(1L, "maxim.chikilev@gmail.com", "Maxim", "Chikilev", expectedDate, "", "");
    when(userService.dataCorrectnessCheck(any(UserDTO.class))).thenReturn(errorResponseDTO);
    when(userService.create(any(UserDTO.class))).thenReturn(customUser);
    mockMvc
        .perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("id").value(1L))
        .andExpect(jsonPath("email").value("maxim.chikilev@gmail.com"))
        .andExpect(jsonPath("firstName").value("Maxim"))
        .andExpect(jsonPath("lastName").value("Chikilev"))
        .andExpect(jsonPath("birthday").value(expectedDate))
        .andExpect(jsonPath("address").value(""))
        .andExpect(jsonPath("phoneNumber").value(""));

    userDTO = new UserDTO("maxim.chikilev@gmail", "", "", sdf.parse("2024-10-10"), "", "");
    errorResponseDTO.addErrorMessage(UserDataErrors.EMAIL.getTitle());
    errorResponseDTO.addErrorMessage(UserDataErrors.FIRSTNAME.getTitle());
    errorResponseDTO.addErrorMessage(UserDataErrors.LASTNAME.getTitle());
    errorResponseDTO.addErrorMessage(UserDataErrors.DATE_YOUNG_USER.getTitle());
    errorResponseDTO.addErrorMessage(UserDataErrors.DATE_FUTURE.getTitle());
    when(userService.dataCorrectnessCheck(any(UserDTO.class))).thenReturn(errorResponseDTO);
    mockMvc
        .perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasSize(5)));
  }

  @Test
  void deleteUser() throws Exception {
    when(userService.delete(1L)).thenReturn(true);
    mockMvc.perform(delete("/users/1")).andExpect(status().isOk());
    when(userService.delete(1L)).thenReturn(false);
    mockMvc.perform(delete("/users/1")).andExpect(status().isNotFound());
  }

  @Test
  void getUserById() throws Exception {
    Date expectedDate = sdf.parse("1980-10-10");
    CustomUser customUser =
        new CustomUser(
            1L, "maxim.chikilev@gmail.com", "Maxim", "Chikilev", expectedDate, "", "");
    when(userService.getUserById(1L)).thenReturn(customUser);
    mockMvc
        .perform(get("/users/1"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("id").value(1L))
        .andExpect(jsonPath("email").value("maxim.chikilev@gmail.com"))
        .andExpect(jsonPath("firstName").value("Maxim"))
        .andExpect(jsonPath("lastName").value("Chikilev"))
        .andExpect(jsonPath("birthday").value(expectedDate))
        .andExpect(jsonPath("address").value(""))
        .andExpect(jsonPath("phoneNumber").value(""));
    when(userService.getUserById(1L)).thenReturn(null);
    mockMvc.perform(get("/users/1")).andExpect(status().isNotFound());
  }

  @Test
  void getAllUsers() throws Exception {
    List<CustomUser> users = new ArrayList<>();
    users.add(
        new CustomUser(1L, "maxim.chikilev@gmail.com", "Maxim", "Chikilev", expectedDate, "", ""));
    users.add(
        new CustomUser(
            2L, "nastya.chikilev@gmail.com", "Nastya", "Chikilev", expectedDate, "", ""));
    users.add(
        new CustomUser(
            3L, "anatoly.chikilev@gmail.com", "Anatoly", "Chikilev", expectedDate, "", ""));
    when(userService.readAll()).thenReturn(users);
    mockMvc
        .perform(get("/users"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(3)));
    users = new ArrayList<>();
    when(userService.readAll()).thenReturn(users);
    mockMvc.perform(get("/users")).andExpect(status().isNotFound());
  }

  @Test
  void testGetUsersByBirthdayRange() throws Exception {
    Date fromDate = expectedDate;
    Date toDate = expectedDate;
    DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
    String isoFromDate =
        fromDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().format(formatter);
    String isoToDate =
        toDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().format(formatter);

    List<CustomUser> users = new ArrayList<>();
    users.add(
        new CustomUser(1L, "maxim.chikilev@gmail.com", "Maxim", "Chikilev", expectedDate, "", ""));
    users.add(
        new CustomUser(
            2L, "nastya.chikilev@gmail.com", "Nastya", "Chikilev", expectedDate, "", ""));
    users.add(
        new CustomUser(
            3L, "anatoly.chikilev@gmail.com", "Anatoly", "Chikilev", expectedDate, "", ""));

    when(userService.findUsersByBirthdayRange(any(), any())).thenReturn(users);

    mockMvc
        .perform(
            get("/users/byBirthdayRange").param("fromDate", isoFromDate).param("toDate", isoToDate))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(3)));

    users = new ArrayList<>();
    when(userService.findUsersByBirthdayRange(any(), any())).thenReturn(users);
    mockMvc
        .perform(
            get("/users/byBirthdayRange").param("fromDate", isoFromDate).param("toDate", isoToDate))
        .andExpect(status().isNotFound());
  }

  @Test
  void updateUser() throws Exception {
    errorResponseDTO = new ErrorResponseDTO();
   CustomUser customUser =
        new CustomUser(1L, "maxim.chikilev@gmail.com", "Maxim", "Chikilev", sdf.parse("1980-10-10"), "", "");
    when(userService.dataCorrectnessCheck(customUser)).thenReturn(errorResponseDTO);
    when(userService.update(customUser)).thenReturn(true);

    mockMvc
        .perform(
            put("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customUser)))
        .andExpect(status().isOk());

    when(userService.update(customUser)).thenReturn(false);
    mockMvc
        .perform(
            put("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customUser)))
        .andExpect(status().isNotFound());

    errorResponseDTO.addErrorMessage(UserDataErrors.EMAIL.getTitle());
    errorResponseDTO.addErrorMessage(UserDataErrors.FIRSTNAME.getTitle());
    errorResponseDTO.addErrorMessage(UserDataErrors.LASTNAME.getTitle());
    errorResponseDTO.addErrorMessage(UserDataErrors.DATE_YOUNG_USER.getTitle());
    errorResponseDTO.addErrorMessage(UserDataErrors.DATE_FUTURE.getTitle());
    when(userService.dataCorrectnessCheck(any())).thenReturn(errorResponseDTO);
    mockMvc
        .perform(
            put("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(errorResponseDTO)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasSize(5)));
  }
}
