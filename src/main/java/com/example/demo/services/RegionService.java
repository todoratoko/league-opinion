package com.example.demo.services;

import com.example.demo.exceptions.NotFoundException;
import com.example.demo.model.entities.Region;
import com.example.demo.model.repositories.RegionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RegionService {
    @Autowired
    private RegionRepository regionRepository;

    public List<Region> getAllRegions() {
        return regionRepository.findAll();
    }

    public Region getById(Long id) {
        Optional<Region> region = regionRepository.findById(id);
        if (region.isPresent()) {
            return region.get();
        } else {
            throw new NotFoundException("Region not found");
        }
    }

    public Region getByCode(String code) {
        Optional<Region> region = regionRepository.findByCode(code);
        if (region.isPresent()) {
            return region.get();
        } else {
            throw new NotFoundException("Region not found with code: " + code);
        }
    }
}
