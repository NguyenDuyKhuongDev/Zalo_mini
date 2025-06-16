package com.devmam.zalomini.services;

import com.devmam.zalomini.dto.request.UserLoginDTO;
import com.devmam.zalomini.dto.request.UserRegisterDTO;
import org.springframework.http.ResponseEntity;

public interface UserService {

    ResponseEntity<?> login(UserLoginDTO dto);
    ResponseEntity<?> register(UserRegisterDTO dto);
    ResponseEntity<?> getProfile(String authHeader);
}
