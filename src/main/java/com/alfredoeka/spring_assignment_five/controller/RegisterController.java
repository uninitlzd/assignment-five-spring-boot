package com.alfredoeka.spring_assignment_five.controller;

import com.alfredoeka.spring_assignment_five.controller.exception.InvalidPayloadException;
import com.alfredoeka.spring_assignment_five.controller.exception.UserAlreadyExistsException;
import com.alfredoeka.spring_assignment_five.entity.oauth.User;
import com.alfredoeka.spring_assignment_five.model.ApiResponse;
import com.alfredoeka.spring_assignment_five.model.registration.ConfirmOtpRequest;
import com.alfredoeka.spring_assignment_five.model.registration.RegistrationRequest;
import com.alfredoeka.spring_assignment_five.model.registration.RegistrationResponse;
import com.alfredoeka.spring_assignment_five.model.registration.ResendOtpRequest;
import com.alfredoeka.spring_assignment_five.service.registration.UserRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class RegisterController {

    @Autowired
    private UserRegistrationService userRegistrationService;

    @PostMapping("/user-register")
    public ResponseEntity<ApiResponse<RegistrationResponse>> registerUser(@RequestBody RegistrationRequest request) {
        try {
            RegistrationResponse registrationResponse = userRegistrationService.registerUser(request);
            ApiResponse<RegistrationResponse> response = ApiResponse.<RegistrationResponse>builder()
                    .data(registrationResponse)
                    .message("Success")
                    .status(HttpStatus.OK.value())
                    .build();

            return ResponseEntity.ok(response);
        } catch (InvalidPayloadException | UserAlreadyExistsException ex) {
            throw ex;
        }
    }

    @PostMapping("/send-otp")
    public ResponseEntity<ApiResponse<String>> registerUser(@RequestBody ResendOtpRequest request) {
        try {
            userRegistrationService.resendOtp(request);

            ApiResponse<String> response = ApiResponse.<String>builder()
                    .data("Thanks, please check your email for activation")
                    .message("Success")
                    .status(HttpStatus.OK.value())
                    .build();

            return ResponseEntity.ok(response);
        } catch (InvalidPayloadException | UserAlreadyExistsException ex) {
            throw ex;
        }
    }

    @PostMapping("/register-confirm-otp/{otp}")
    public ResponseEntity<ApiResponse<String>> confirmOtp(@RequestBody ConfirmOtpRequest request, @PathVariable String otp) {
        try {
            Optional<User> confirm = userRegistrationService.confirmOtp(request, otp);

            ApiResponse<String> response = ApiResponse.<String>builder()
                    .data("Akun anda sudah aktif. Silahkan melakukan login")
                    .message("Success")
                    .status(HttpStatus.OK.value())
                    .build();

            return ResponseEntity.ok(response);
        } catch (InvalidPayloadException | UserAlreadyExistsException ex) {
            throw ex;
        }
    }

    @GetMapping("/check")
    public String check() {
        return "OK";
    }
}