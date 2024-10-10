package com.alfredoeka.spring_assignment_five.service.registration;

import com.alfredoeka.spring_assignment_five.controller.exception.InvalidPayloadException;
import com.alfredoeka.spring_assignment_five.controller.exception.UserAlreadyExistsException;
import com.alfredoeka.spring_assignment_five.entity.UserDetail;
import com.alfredoeka.spring_assignment_five.entity.oauth.Role;
import com.alfredoeka.spring_assignment_five.entity.oauth.User;
import com.alfredoeka.spring_assignment_five.model.registration.ConfirmOtpRequest;
import com.alfredoeka.spring_assignment_five.model.registration.RegistrationRequest;
import com.alfredoeka.spring_assignment_five.model.registration.RegistrationResponse;
import com.alfredoeka.spring_assignment_five.model.registration.ResendOtpRequest;
import com.alfredoeka.spring_assignment_five.repository.oauth.UserRepository;
import com.alfredoeka.spring_assignment_five.repository.registration.UserDetailRepository;
import com.alfredoeka.spring_assignment_five.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserRegistrationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDetailRepository userDetailRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Transactional
    public RegistrationResponse registerUser(RegistrationRequest dto) {
        // Validate payload
        if (dto.getUsername() == null || dto.getUsername().isEmpty()) {
            throw new InvalidPayloadException("Username is required");
        }

        // Check if user already exists
        if (userRepository.findOneByUsername(dto.getUsername()) != null) {
            throw new UserAlreadyExistsException("User with this username already exists");
        }

        // Create and save User
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setFullname(dto.getFullname());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEnabled(false);


        // Generate OTP
        String otp = generateOTP();
        Date otpExpiredDate = calculateOtpExpiryDate();
        user.setOtp(otp);
        user.setOtpExpiredDate(otpExpiredDate);

        userRepository.save(user);

        // Create and save UserDetail
        UserDetail userDetail = new UserDetail();
        userDetail.setPhoneNumber(dto.getPhoneNumber());
        userDetail.setDomicile(dto.getDomicile());
        userDetail.setGender(dto.getGender());
        userDetail.setUser(user);

        userDetailRepository.save(userDetail);
        emailService.sendAfterRegistrationEmail(user, otp);

        return RegistrationResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullname(user.getFullname())
                .otp(otp)
                .otpExpiredDate(otpExpiredDate)
                .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
                .authorities(user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .build();
    }

    public static String generateOTP() {
        // Implement OTP generation logic (e.g., 6-digit random number)
        return String.format("%06d", new Random().nextInt(999999));
    }

    public static Date calculateOtpExpiryDate() {
        // Set OTP expiry (e.g., 15 minutes from now)
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 15);
        return calendar.getTime();
    }

    public Optional<User> resendOtp(ResendOtpRequest request) {
        if (request.getUsername() == null || request.getUsername().isEmpty()) {
            throw new InvalidPayloadException("Username is required");
        }

        User user = userRepository.findOneByUsername(request.getUsername());
        if (user == null) {
            throw new InvalidPayloadException("Username not found");
        }

        if (user.isEnabled()) {
            throw new InvalidPayloadException("Your account is already active");
        }

        // Send OTP email
        emailService.sendAfterRegistrationEmail(user, user.getOtp());

        return Optional.of((user));
    }

    public Optional<User> confirmOtp(ConfirmOtpRequest request, String otp) {
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            throw new InvalidPayloadException("Username is required");
        }

        User user = userRepository.findOneByUsername(request.getEmail());
        if (user == null) {
            throw new InvalidPayloadException("Username not found");
        }

        if (user.isEnabled()) {
            throw new InvalidPayloadException("Your account is already active");
        }

        if (otp.equals(user.getOtp())) {
            user.setOtp(null);
            user.setOtpExpiredDate(null);
            user.setEnabled(true);
            userRepository.save(user);
        } else {
            throw new InvalidPayloadException("Wrong OTP Code");
        }

        return Optional.of(user);
    }
}
