package org.example.agency.service;


import org.example.agency.model.Deal;
import org.example.agency.model.Property;
import org.example.agency.repository.DealRepository;
import org.example.agency.repository.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class DealService {

    @Autowired
    private DealRepository dealRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private PropertyService propertyService;

    public List<Deal> getAllDeals() {
        return dealRepository.findAll();
    }

    public Deal getDealById(Long id) {
        return dealRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Deal not found with id: " + id));
    }

    public Deal createDeal(Deal deal) {
        Property property = deal.getProperty();
        if ("VIEWING".equals(deal.getDealType())) {
            property.setStatus("VIEWING_SCHEDULED");
            deal.setStatus("PENDING");
        } else {
            property.setStatus("SOLD");
            deal.setStatus("COMPLETED");
        }
        propertyRepository.save(property);
        return dealRepository.save(deal);
    }

    public List<Deal> getDealsByPropertyId(Long propertyId) {
        return dealRepository.findByProperty_Id(propertyId);
    }

    public Deal updateDeal(Long id, Deal dealDetails) {
        Deal deal = getDealById(id);

        deal.setFinalPrice(dealDetails.getFinalPrice());
        deal.setDealType(dealDetails.getDealType());
        deal.setStatus(dealDetails.getStatus());
        deal.setNotes(dealDetails.getNotes());

        return dealRepository.save(deal);
    }

    public void cancelDeal(Long id) {
        Deal deal = getDealById(id);
        Property property = deal.getProperty();
        if ("VIEWING".equals(deal.getDealType()) || "PENDING".equals(deal.getStatus())) {
            property.setStatus("AVAILABLE");
        }
        propertyRepository.save(property);
        deal.setStatus("CANCELLED");
        dealRepository.save(deal);
    }

    public List<Deal> getDealsByClientId(Long clientId) {
        return dealRepository.findByClient_Id(clientId);
    }

    public List<Deal> getDealsByAgent(Long agentId) {
        return dealRepository.findByAgent_Id(agentId);
    }

    public List<Deal> getDealsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return dealRepository.findByDateRange(startDate, endDate);
    }
}