package com.example.userservice.dto;

import com.example.userservice.entity.UserDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO implements UserDetails {
    private String email;
    private String firstName;
    private String lastName;
    private Date birthday;
    private String address;
    private String phoneNumber;
}
