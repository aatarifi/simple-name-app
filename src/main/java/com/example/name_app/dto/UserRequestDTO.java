package com.example.name_app.dto;
/*
public class UserRequestDTO {
    private String firstName;
    private String lastName;

    public UserRequestDTO(){}

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
*/

// Instead of writing a full class with 30 lines of getters/setters, you write:

public record UserRequestDTO(String firstName, String lastName) {}

