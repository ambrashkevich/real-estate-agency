package org.example.agency.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "viewings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Viewing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne
    @JoinColumn(name = "agent_id", nullable = false)
    private Agent agent;

    @Column(name = "viewing_date", nullable = false)
    private LocalDateTime viewingDate;

    @Column(length = 1000)
    private String notes;

    private String status; // SCHEDULED, COMPLETED, CANCELLED
}
