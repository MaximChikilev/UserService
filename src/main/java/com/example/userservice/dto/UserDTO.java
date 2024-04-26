package com.example.userservice.dto;

import com.example.userservice.deserializer.CustomDateDeserializer;
import com.example.userservice.entity.CustomUser;
import com.example.userservice.entity.UserDetails;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class UserDTO implements UserDetails {
    private String email;
    private String firstName;
    private String lastName;
    @JsonDeserialize(using = CustomDateDeserializer.class)
    private Date birthday;
    private String address;
    private String phoneNumber;
}
