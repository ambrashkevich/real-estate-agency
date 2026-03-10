package org.example.agency.controller;

import org.example.agency.model.Deal;
import org.example.agency.service.DealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/deals")
@CrossOrigin(origins = "*")
public class DealController {

    @Autowired
    private DealService dealService;

    @GetMapping
    public ResponseEntity<List<Deal>> getAllDeals() {
        return ResponseEntity.ok(dealService.getAllDeals());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Deal> getDealById(@PathVariable Long id) {
        return ResponseEntity.ok(dealService.getDealById(id));
    }

    @PostMapping
    public ResponseEntity<Deal> createDeal(@RequestBody Deal deal) {
        return new ResponseEntity<>(dealService.createDeal(deal), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Deal> updateDeal(@PathVariable Long id, @RequestBody Deal deal) {
        return ResponseEntity.ok(dealService.updateDeal(id, deal));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelDeal(@PathVariable Long id) {
        dealService.cancelDeal(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/agent/{agentId}")
    public ResponseEntity<List<Deal>> getDealsByAgent(@PathVariable Long agentId) {
        return ResponseEntity.ok(dealService.getDealsByAgent(agentId));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<Deal>> getDealsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(dealService.getDealsByDateRange(startDate, endDate));
    }
}
