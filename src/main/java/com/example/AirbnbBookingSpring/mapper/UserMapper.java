package com.example.AirbnbBookingSpring.mapper;

import com.example.AirbnbBookingSpring.dtos.CreateAirbnbRequest;
import com.example.AirbnbBookingSpring.dtos.CreateUserRequest;
import com.example.AirbnbBookingSpring.dtos.UserDTO;
import com.example.AirbnbBookingSpring.models.Airbnb;
import com.example.AirbnbBookingSpring.models.User;

public class UserMapper {

    public static User toEntity(CreateUserRequest request) {
        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .build();
    }

    public static UserDTO toDTO(User user) {
         return UserDTO.builder()
                 .id(user.getId())
                 .name(user.getName())
                 .email(user.getEmail())
                 .build();
    }
}
