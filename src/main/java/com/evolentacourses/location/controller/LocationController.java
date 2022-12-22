package com.evolentacourses.location.controller;

import com.evolentacourses.location.model.Geodata;
import com.evolentacourses.location.model.Weather;
import com.evolentacourses.location.repository.GeodataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@RestController
public class LocationController {
    @Autowired
    private GeodataRepository repository;
    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/")
    public Optional<Geodata> getWeather(@RequestParam String location) {
        return repository.findByName(location);
    }

    @GetMapping("/weather")
    public ResponseEntity<Weather> redirectRequestWeather(@RequestParam String location) {
        Optional<Geodata> geodataOptional = repository.findByName(location);
        return geodataOptional
                .map(geodata -> {
                    String url = String
                            .format("http://localhost:8082/?lat=%s&lon=%s", geodata.getLat(), geodata.getLon());
                    return new ResponseEntity<>(restTemplate.getForObject(url, Weather.class), HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @PostMapping("/")
    public ResponseEntity<Geodata> save(@RequestBody Geodata geodata) {
        Optional<Geodata> geodataOptional = repository.findByName(geodata.getName());
        return geodataOptional
                .map(value -> new ResponseEntity<>(value, HttpStatus.BAD_REQUEST))
                .orElseGet(() -> new ResponseEntity<>(repository.save(geodata), HttpStatus.CREATED));
    }
}
