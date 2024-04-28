package com.example.userservice;

import com.example.userservice.dto.UserDTO;
import com.example.userservice.entity.CustomUser;
import com.example.userservice.repo.UserRepository;
import com.example.userservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = {"permissibleAge=18"})
public class UserServiceTest {
  @MockBean private UserRepository userRepository;
  @Autowired private UserService userService;
  private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
  private final Date expectedDate = sdf.parse("1980-10-10");

  public UserServiceTest() throws ParseException {}

  @Test
  public void testCreateUser() {
    UserDTO userDTO =
        new UserDTO("maxim.chikilev@gmail.com", "Maxim", "Chikilev", expectedDate, "", "");

    CustomUser expectedUser =
        CustomUser.builder()
            .email("maxim.chikilev@gmail.com")
            .firstName("Maxim")
            .lastName("Chikilev")
            .birthday(expectedDate)
            .address("")
            .phoneNumber("")
            .build();

    when(userRepository.save(any(CustomUser.class))).thenReturn(expectedUser);
    CustomUser createdUser = userService.create(userDTO);

    assertNotNull(createdUser);
    assertEquals("maxim.chikilev@gmail.com", createdUser.getEmail());
    assertEquals("Maxim", createdUser.getFirstName());
    assertEquals("Chikilev", createdUser.getLastName());
    assertEquals("", createdUser.getAddress());
    assertEquals("", createdUser.getPhoneNumber());
  }

  @Test
  public void dataCorrectnessCheckTest() throws ParseException{
    UserDTO userDTO =
        new UserDTO(
            "maxim.chikilev@gmail.com", "Maxim", "Chikilev", sdf.parse("1980-10-10"), "", "");

    CustomUser customUserUser =
        CustomUser.builder()
            .email("maxim.chikilev@gmail.com")
            .firstName("Maxim")
            .lastName("Chikilev")
            .birthday(sdf.parse("1980-10-10"))
            .address("")
            .phoneNumber("")
            .build();
    assertEquals(0, userService.dataCorrectnessCheck(userDTO).getErrorMessages().size());
    assertEquals(0, userService.dataCorrectnessCheck(customUserUser).getErrorMessages().size());
    userDTO = new UserDTO("maxim.chikilev@gmail", "", "", sdf.parse("2023-10-10"), "", "");

    customUserUser =
        CustomUser.builder()
            .email("maxim.chikilev@gmail")
            .firstName("")
            .lastName("")
            .birthday(sdf.parse("2025-10-10"))
            .address("")
            .phoneNumber("")
            .build();
    assertEquals(4, userService.dataCorrectnessCheck(userDTO).getErrorMessages().size());
    assertEquals(5, userService.dataCorrectnessCheck(customUserUser).getErrorMessages().size());
    assertEquals(1, userService.dataCorrectnessCheck(null).getErrorMessages().size());
  }

  @Test
  public void readAllTest() throws ParseException {
    List<CustomUser> expectedUsers =
        Arrays.asList(
            new CustomUser(
                1L,
                "maxim.chikilev@gmail.com",
                "Maxim",
                "Chikilev",
                sdf.parse("1980-10-10"),
                "",
                ""),
            new CustomUser(
                2L,
                "tolya.chikilev@gmail.com",
                "Tolya",
                "Chikilev",
                sdf.parse("2003-10-10"),
                "",
                ""));
    when(userRepository.findAll()).thenReturn(expectedUsers);
    List<CustomUser> actualUsers = userService.readAll();
    assertEquals(expectedUsers, actualUsers, "The returned user list should match the expected");
    verify(userRepository).findAll();
  }

  @Test
  public void updateUserTest() throws ParseException {
    CustomUser existingUser =
        new CustomUser(
            1L, "maxim.chikilev@gmail.com", "Maxim", "Chikilev", sdf.parse("1980-10-10"), "", "");
    when(userRepository.findById(existingUser.getId())).thenReturn(Optional.of(existingUser));

    CustomUser updatedUser =
        new CustomUser(
            1L, "tolya.chikilev@gmail.com", "Tolya", "Chikilev", sdf.parse("2003-10-10"), "", "");
    boolean result = userService.update(updatedUser);

    assertTrue(result, "Update should return true when user exists");
    verify(userRepository).findById(existingUser.getId());
    verify(userRepository).save(updatedUser);

    CustomUser nonExistingUser =
        new CustomUser(
            2L,
            "nastya.chikileva@gmail.com",
            "Nastya",
            "Chikileva",
            sdf.parse("2003-10-10"),
            "",
            "");
    when(userRepository.findById(nonExistingUser.getId())).thenReturn(Optional.empty());

    result = userService.update(nonExistingUser);
    assertFalse(result, "Update should return false when user does not exist");
    verify(userRepository).findById(nonExistingUser.getId());
    verifyNoMoreInteractions(userRepository);
  }

  @Test
  public void deleteUserTest() {
    Long userId = 1L;
    when(userRepository.existsById(userId)).thenReturn(true);
    boolean result = userService.delete(userId);
    assertTrue(result, "Delete should return true when user exists");
    verify(userRepository).existsById(userId);
    verify(userRepository).deleteById(userId);

    userId = 2L;
    when(userRepository.existsById(userId)).thenReturn(false);
    result = userService.delete(userId);
    assertFalse(result, "Delete should return false when user does not exist");
    verify(userRepository).existsById(userId);
    verifyNoMoreInteractions(userRepository);
  }

  @Test
  public void getUsedByIdTest() throws ParseException {

    Long userId = 1L;
    CustomUser expectedUser =
        new CustomUser(
            1L, "maxim.chikilev@gmail.com", "Maxim", "Chikilev", sdf.parse("1980-10-10"), "", "");
    when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));
    CustomUser actualUser = userService.getUserById(userId);
    assertEquals(expectedUser, actualUser, "Returned user should match the expected user");
    verify(userRepository).findById(userId);

    userId = 2L;
    when(userRepository.findById(userId)).thenReturn(Optional.empty());
    actualUser = userService.getUserById(userId);
    assertNull(actualUser, "Returned user should be null when user does not exist");
    verify(userRepository).findById(userId);
  }
  @Test
  public void findUsersByBirthdayRangeTest() throws ParseException {
    Date fromDate = sdf.parse("1979-10-10");
    Date toDate = new Date();
    List<CustomUser> expectedUsers = new ArrayList<>();
    expectedUsers.add(new CustomUser(
            1L, "maxim.chikilev@gmail.com", "Maxim", "Chikilev", sdf.parse("1980-10-10"), "", ""));
    expectedUsers.add(new CustomUser(
            2L, "tolya.chikilev@gmail.com", "Tolya", "Chikilev", sdf.parse("2003-10-10"), "", ""));
    when(userRepository.findByBirthdayBetween(fromDate, toDate)).thenReturn(expectedUsers);
    List <CustomUser> actualUsers = userService.findUsersByBirthdayRange(fromDate, toDate);
    verify(userRepository).findByBirthdayBetween(fromDate, toDate);
    assertEquals(expectedUsers, actualUsers);
  }
}
