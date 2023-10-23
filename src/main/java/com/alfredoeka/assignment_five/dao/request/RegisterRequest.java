package com.alfredoeka.assignment_five.dao.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @NotNull
    public String username;

    @NotNull
    public String name;

    @NotNull
    public String phone_number;

    @NotNull
    public String domicile;

    @NotNull
    public String gender;

    @NotNull
    public String password;

    @NotNull
    public String fullname;
}
