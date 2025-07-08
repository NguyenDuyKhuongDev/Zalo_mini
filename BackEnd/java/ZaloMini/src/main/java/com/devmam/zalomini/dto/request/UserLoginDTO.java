package com.devmam.zalomini.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserLoginDTO implements Serializable {

    @NotBlank
    String account;
    @NotBlank
    String password;
}
