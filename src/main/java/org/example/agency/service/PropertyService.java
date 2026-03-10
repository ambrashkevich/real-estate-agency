package org.example.agency.service;

import org.example.agency.model.Property;
import org.example.agency.repository.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class PropertyService {

    @Autowired
    private PropertyRepository propertyRepository;

    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    public Property getPropertyById(Long id) {
        return propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found with id: " + id));
    }

    public Property createProperty(Property property) {
        property.setStatus("AVAILABLE");
        return propertyRepository.save(property);
    }

    public Property updateProperty(Long id, Property propertyDetails) {
        Property property = getPropertyById(id);

        property.setTitle(propertyDetails.getTitle());
        property.setAddress(propertyDetails.getAddress());
        property.setCity(propertyDetails.getCity());
        property.setDistrict(propertyDetails.getDistrict());
        property.setPropertyType(propertyDetails.getPropertyType());
        property.setPrice(propertyDetails.getPrice());
        property.setArea(propertyDetails.getArea());
        property.setBedrooms(propertyDetails.getBedrooms());
        property.setBathrooms(propertyDetails.getBathrooms());
        property.setDescription(propertyDetails.getDescription());
        property.setStatus(propertyDetails.getStatus());

        return propertyRepository.save(property);
    }

    public void deleteProperty(Long id) {
        Property property = getPropertyById(id);
        propertyRepository.delete(property);
    }

    public List<Property> searchProperties(String city, BigDecimal minPrice, BigDecimal maxPrice,
                                           String propertyType, String status) {
        if (city != null && !city.isEmpty()) {
            if (status != null && !status.isEmpty()) {
                return propertyRepository.findByCityAndStatus(city, status);
            }
            return propertyRepository.findByCity(city);
        } else if (minPrice != null && maxPrice != null) {
            return propertyRepository.findByPriceRange(minPrice, maxPrice);
        } else if (propertyType != null && !propertyType.isEmpty()) {
            return propertyRepository.findByPropertyType(propertyType);
        } else if (status != null && !status.isEmpty()) {
            return propertyRepository.findByStatus(status);
        }
        return propertyRepository.findAll();
    }
}
