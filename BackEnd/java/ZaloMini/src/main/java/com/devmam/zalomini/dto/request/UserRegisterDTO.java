package com.devmam.zalomini.dto.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRegisterDTO implements Serializable {
    String phoneNumber;
    String displayName;
    String email;
    String gender;
    String password;
}
