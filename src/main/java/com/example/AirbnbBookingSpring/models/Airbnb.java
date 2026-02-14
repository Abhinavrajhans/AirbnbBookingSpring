package com.example.AirbnbBookingSpring.models;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "airbnb")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Airbnb extends BaseModel{

    private String name;
    private String description;
    @Column(nullable = false)
    private String pricePerNight;
    private String location;
}
