package com.example.userservice.entity;

public enum UserDataErrors {
    EMAIL ("Incorrect email"),
    FIRSTNAME ("First name cannot be empty"),
    LASTNAME ("Last name cannot be empty"),
    DATE_FORMAT ("Incorrect date format"),
    DATE_FUTURE ("Date cannot be in future"),
    DATE_YOUNG_USER ("User cannot be younger than :");
    private final String title;

    UserDataErrors(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
