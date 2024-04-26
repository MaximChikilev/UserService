package com.example.userservice.entity;

import java.util.Date;

public interface UserDetails {
    public String getEmail();
    public String getFirstName();
    public String getLastName();
    public Date getBirthday();
}
