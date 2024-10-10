package com.alfredoeka.spring_assignment_five.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class SignInGoogleResponse<T> {
    private T data;
    private String message;
    private String type;
    private int status;

    public static <T> SignInGoogleResponseBuilder<T> builder() {
        return new SignInGoogleResponseBuilder<T>();
    }
}
