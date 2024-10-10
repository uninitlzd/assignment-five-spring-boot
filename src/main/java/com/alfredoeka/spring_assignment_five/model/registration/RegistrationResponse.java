package com.alfredoeka.spring_assignment_five.model.registration;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class RegistrationResponse {
    private Long id;
    private String username;
    private String fullname;
    private String otp;
    private Date otpExpiredDate;
    private List<String> roles;
    private List<String> authorities;
}
