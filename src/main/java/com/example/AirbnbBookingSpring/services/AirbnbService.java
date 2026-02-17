package com.example.AirbnbBookingSpring.services;

import com.example.AirbnbBookingSpring.models.Airbnb;
import com.example.AirbnbBookingSpring.repositories.writes.AirbnbWriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AirbnbService {

    private final AirbnbWriteRepository airbnbRepository;

    @Transactional
    public Airbnb createAirbnb(Airbnb airbnb) {
        if (airbnb.getPricePerNight() == null || airbnb.getPricePerNight() <= 0) {
            throw new RuntimeException("Price per night must be greater than 0");
        }
        return airbnbRepository.save(airbnb);
    }

    @Transactional(readOnly = true)
    public Optional<Airbnb> getAirbnbById(Long id) {
        return airbnbRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Airbnb> getAllAirbnbs() {
        return airbnbRepository.findAll();
    }

    @Transactional
    public Airbnb updateAirbnb(Long id, Airbnb updatedAirbnb) {
        Airbnb existingAirbnb = airbnbRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Airbnb not found with id: " + id));

        existingAirbnb.setName(updatedAirbnb.getName());
        existingAirbnb.setDescription(updatedAirbnb.getDescription());
        existingAirbnb.setLocation(updatedAirbnb.getLocation());
        existingAirbnb.setPricePerNight(updatedAirbnb.getPricePerNight());

        return airbnbRepository.save(existingAirbnb);
    }

    @Transactional
    public void deleteAirbnb(Long id) {
        if (!airbnbRepository.existsById(id)) {
            throw new RuntimeException("Airbnb not found with id: " + id);
        }
        airbnbRepository.deleteById(id);
    }
}