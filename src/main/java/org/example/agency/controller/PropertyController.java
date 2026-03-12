package org.example.agency.controller;

import org.example.agency.model.Property;
import org.example.agency.service.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/properties")
@CrossOrigin(origins = "*")
public class PropertyController {

    @Autowired
    private PropertyService propertyService;

    @GetMapping
    public ResponseEntity<List<Property>> getAllProperties() {
        return ResponseEntity.ok(propertyService.getAllProperties());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Property> getPropertyById(@PathVariable Long id) {
        return ResponseEntity.ok(propertyService.getPropertyById(id));
    }

    @PostMapping
    public ResponseEntity<Property> createProperty(@RequestBody Property property, 
                                                   @RequestParam(required = false) Long agentId,
                                                   @RequestParam(required = false) Long districtId,
                                                   @RequestParam(required = false) Long propertyTypeId) {
        return new ResponseEntity<>(propertyService.createProperty(property, agentId, districtId, propertyTypeId), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Property> updateProperty(@PathVariable Long id, 
                                                   @RequestBody Property property,
                                                   @RequestParam(required = false) Long agentId,
                                                   @RequestParam(required = false) Long districtId,
                                                   @RequestParam(required = false) Long propertyTypeId) {
        return ResponseEntity.ok(propertyService.updateProperty(id, property, agentId, districtId, propertyTypeId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable Long id) {
        propertyService.deleteProperty(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Property>> searchProperties(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String propertyType,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(propertyService.searchProperties(city, minPrice, maxPrice, propertyType, status));
    }
}