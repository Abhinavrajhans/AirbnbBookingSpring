package com.example.AirbnbBookingSpring.models;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseModel{
    private String name;
    private String email;
    private String password;
}
