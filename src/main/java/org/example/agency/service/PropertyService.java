package org.example.agency.service;

import org.example.agency.model.Agent;
import org.example.agency.model.Property;
import org.example.agency.repository.AgentRepository;
import org.example.agency.repository.DistrictRepository;
import org.example.agency.repository.PropertyRepository;
import org.example.agency.repository.PropertyTypeRepository;
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

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private DistrictRepository districtRepository;

    @Autowired
    private PropertyTypeRepository propertyTypeRepository;

    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    public List<Property> getPropertiesByAgentId(Long agentId) {
        return propertyRepository.findByAgent_Id(agentId);
    }

    public Property getPropertyById(Long id) {
        return propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found with id: " + id));
    }

    public Property createProperty(Property property, Long agentId) {
        return createProperty(property, agentId, null, null);
    }

    public Property createProperty(Property property, Long agentId, Long districtId, Long propertyTypeId) {
        property.setStatus("AVAILABLE");
        if (agentId != null) {
            property.setAgent(agentRepository.findById(agentId).orElse(null));
        }
        if (districtId != null) {
            property.setDistrict(districtRepository.findById(districtId).orElse(null));
        }
        if (propertyTypeId != null) {
            property.setPropertyType(propertyTypeRepository.findById(propertyTypeId).orElse(null));
        }
        return propertyRepository.save(property);
    }

    public Property updateProperty(Long id, Property propertyDetails) {
        return updateProperty(id, propertyDetails, 
                propertyDetails.getAgent() != null ? propertyDetails.getAgent().getId() : null,
                propertyDetails.getDistrict() != null ? propertyDetails.getDistrict().getId() : null,
                propertyDetails.getPropertyType() != null ? propertyDetails.getPropertyType().getId() : null);
    }

    public Property updateProperty(Long id, Property propertyDetails, Long agentId, Long districtId, Long propertyTypeId) {
        Property property = getPropertyById(id);
        property.setTitle(propertyDetails.getTitle());
        property.setAddress(propertyDetails.getAddress());
        property.setCity(propertyDetails.getCity());
        property.setPrice(propertyDetails.getPrice());
        property.setArea(propertyDetails.getArea());
        property.setBedrooms(propertyDetails.getBedrooms());
        property.setBathrooms(propertyDetails.getBathrooms());
        property.setDescription(propertyDetails.getDescription());
        property.setStatus(propertyDetails.getStatus());
        
        if (agentId != null) {
            property.setAgent(agentRepository.findById(agentId).orElse(null));
        } else {
            property.setAgent(null);
        }
        
        if (districtId != null) {
            property.setDistrict(districtRepository.findById(districtId).orElse(null));
        } else {
            property.setDistrict(null);
        }
        
        if (propertyTypeId != null) {
            property.setPropertyType(propertyTypeRepository.findById(propertyTypeId).orElse(null));
        } else {
            property.setPropertyType(null);
        }
        
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
            return propertyRepository.findByPropertyType_Name(propertyType);
        } else if (status != null && !status.isEmpty()) {
            return propertyRepository.findByStatus(status);
        }
        return propertyRepository.findAll();
    }
}
