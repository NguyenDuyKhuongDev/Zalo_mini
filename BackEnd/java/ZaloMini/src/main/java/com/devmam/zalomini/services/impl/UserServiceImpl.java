package com.devmam.zalomini.services.impl;

import com.devmam.zalomini.dto.request.UserLoginDTO;
import com.devmam.zalomini.dto.request.UserRegisterDTO;
import com.devmam.zalomini.dto.response.ProfileDTO;
import com.devmam.zalomini.dto.response.ResponseData;
import com.devmam.zalomini.entities.User;
import com.devmam.zalomini.exception.customizeException.AddingFailException;
import com.devmam.zalomini.exception.customizeException.ResourceNotFoundException;
import com.devmam.zalomini.exception.customizeException.UnAuthenticationException;
import com.devmam.zalomini.repositories.UserRepository;
import com.devmam.zalomini.services.UserService;
import com.devmam.zalomini.utils.JwtUtil;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<?> login(UserLoginDTO dto) {
        User findingUser = userRepository.findByEmailOrPhoneNumber(dto.getAccount(), dto.getAccount())
                .orElseThrow(() -> new ResourceNotFoundException("Tài khoản hoặc mật khẩu sai", dto));
        if (!passwordEncoder.matches(dto.getPassword(), findingUser.getPasswordHash())) {
            throw new ResourceNotFoundException("Tài khoản hoặc mật khẩu sai", dto);
        }
        String token = jwtUtil.generateToken(findingUser.getId(), findingUser.getEmail(), findingUser.getRoles());

        return ResponseEntity.ok(
                ResponseData.builder()
                        .status(200)
                        .message("Đăng nhập thành công")
                        .data(token)
                        .build()
        );
    }

    @Override
    public ResponseEntity<?> register(UserRegisterDTO dto) {
        Optional<User> existingUser = userRepository.findByEmailOrPhoneNumber(dto.getEmail(), dto.getPhoneNumber());
        if (existingUser.isPresent()) {
            throw new AddingFailException("Tài khoản đã tồn tại", dto);
        }
        User user = User.builder()
                .email(dto.getEmail())
                .phoneNumber(dto.getPhoneNumber())
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .gender(dto.getGender())
                .build();
        userRepository.save(user);
        return ResponseEntity.ok(
                ResponseData.builder()
                        .status(200)
                        .message("Đăng ký thành công")
                        .data(dto)
                        .build()
        );
    }

    @Override
    public ResponseEntity<?> getProfile(String authHeader) {

        String token = jwtUtil.getTokenFromAuthHeader(authHeader);
        if(token == null) throw new UnAuthenticationException("Không có quyền truy cập");
        JWTClaimsSet jwtClaimsSet = jwtUtil.getClaimsFromToken(token);
        Long userId = Long.parseLong((String)jwtClaimsSet.getClaims().get("id"));
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng", authHeader));
        ProfileDTO profileDTO = ProfileDTO.builder()
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .avatarUrl(user.getAvatarUrl())
                .bio(user.getBio())
                .dateOfBirth(user.getDateOfBirth())
                .gender(user.getGender())
                .build();
        return ResponseEntity.ok(
                ResponseData.builder()
                        .status(200)
                        .message("Lấy thông tin người dùng thành công")
                        .data(profileDTO)
                        .build()
        );
    }
}
