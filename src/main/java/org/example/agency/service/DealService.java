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
        // Update property status
        Property property = deal.getProperty();
        property.setStatus("SOLD");
        propertyRepository.save(property);

        deal.setStatus("COMPLETED");
        return dealRepository.save(deal);
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

        // Restore property status
        Property property = deal.getProperty();
        property.setStatus("AVAILABLE");
        propertyRepository.save(property);

        deal.setStatus("CANCELLED");
        dealRepository.save(deal);
    }

    public List<Deal> getDealsByAgent(Long agentId) {
        return dealRepository.findByAgentId(agentId);
    }

    public List<Deal> getDealsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return dealRepository.findByDateRange(startDate, endDate);
    }
}