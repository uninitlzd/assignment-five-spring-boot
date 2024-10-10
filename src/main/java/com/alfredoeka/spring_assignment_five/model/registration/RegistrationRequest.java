package com.alfredoeka.spring_assignment_five.model.registration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationRequest {
    private String username;
    private String name;
    private String phoneNumber;
    private String domicile;
    private String gender;
    private String password;
    private String fullname;
}
