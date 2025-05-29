package com.example.demo.builder.userbuilder;

import com.example.demo.dto.userdto.UserDTO;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;

import java.time.LocalDateTime;

public class UserBuilder {

    public static User generateEntityFromDTO(UserDTO userDTO, Role role) {
        if (userDTO == null || role == null) {
            throw new IllegalArgumentException("UserDTO or Role cannot be null");
        }
        return User.builder()
                .name(userDTO.getName())
                .email(userDTO.getEmail())
                .password(userDTO.getPassword())  // You may want to hash the password here
                .timeStamp(LocalDateTime.now())
                .role(role)
                .build();
    }
}
