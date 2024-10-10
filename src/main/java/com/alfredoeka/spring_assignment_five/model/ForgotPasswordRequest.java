package com.alfredoeka.spring_assignment_five.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ForgotPasswordRequest {
    public String username;
    public String otp;
    public String newPassword;
    public String confirmNewPassword;
}
