package com.devmam.zalomini.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileDTO implements Serializable {
    String phoneNumber;
    String userName;
    String displayName;
    String email;
    String avatarUrl;
    String bio;
    Date dateOfBirth;
    String gender;
}
