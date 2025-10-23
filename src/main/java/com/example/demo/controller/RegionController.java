package com.example.demo.controller;

import com.example.demo.model.entities.Region;
import com.example.demo.services.RegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RegionController {
    @Autowired
    private RegionService regionService;

    @GetMapping("/regions")
    public ResponseEntity<List<Region>> getAllRegions() {
        List<Region> regions = regionService.getAllRegions();
        return ResponseEntity.ok(regions);
    }

    @GetMapping("/region/{id}")
    public ResponseEntity<Region> getRegionById(@PathVariable Long id) {
        Region region = regionService.getById(id);
        return ResponseEntity.ok(region);
    }

    @GetMapping("/region/code/{code}")
    public ResponseEntity<Region> getRegionByCode(@PathVariable String code) {
        Region region = regionService.getByCode(code);
        return ResponseEntity.ok(region);
    }
}
