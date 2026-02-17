package com.example.AirbnbBookingSpring.controllers;

import com.example.AirbnbBookingSpring.dtos.AirbnbDTO;
import com.example.AirbnbBookingSpring.dtos.CreateAirbnbRequest;
import com.example.AirbnbBookingSpring.mapper.AirbnbMapper;
import com.example.AirbnbBookingSpring.models.Airbnb;
import com.example.AirbnbBookingSpring.services.AirbnbService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/airbnbs")
@RequiredArgsConstructor
public class AirbnbController {

    private final AirbnbService airbnbService;

    @PostMapping
    public ResponseEntity<AirbnbDTO> createAirbnb(@RequestBody CreateAirbnbRequest request) {
        Airbnb airbnb = AirbnbMapper.toEntity(request);

        Airbnb createdAirbnb = airbnbService.createAirbnb(airbnb);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(createdAirbnb));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AirbnbDTO> getAirbnbById(@PathVariable Long id) {
        return airbnbService.getAirbnbById(id)
                .map(airbnb -> ResponseEntity.ok(convertToDTO(airbnb)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<AirbnbDTO>> getAllAirbnbs() {
        List<AirbnbDTO> airbnbs = airbnbService.getAllAirbnbs().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(airbnbs);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AirbnbDTO> updateAirbnb(@PathVariable Long id, @RequestBody CreateAirbnbRequest request) {
        Airbnb airbnb = AirbnbMapper.toEntity(request);
        Airbnb updatedAirbnb = airbnbService.updateAirbnb(id, airbnb);
        return ResponseEntity.ok(convertToDTO(updatedAirbnb));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAirbnb(@PathVariable Long id) {
        airbnbService.deleteAirbnb(id);
        return ResponseEntity.noContent().build();
    }

    private AirbnbDTO convertToDTO(Airbnb airbnb) {
        return AirbnbMapper.toDTO(airbnb);
    }
}