package org.example.agency.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "properties")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String city;

    @ManyToOne
    @JoinColumn(name = "district_id")
    private District district;

    @ManyToOne
    @JoinColumn(name = "property_type_id")
    private PropertyType propertyType;

    @Column(nullable = false)
    private BigDecimal price;

    private Double area; // in square meters

    private Integer bedrooms;

    private Integer bathrooms;

    @Column(length = 2000)
    private String description;

    private String status; // AVAILABLE, SOLD, RENTED, UNDER_CONTRACT

    @ManyToOne
    @JoinColumn(name = "agent_id")
    private Agent agent;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "property_features",
            joinColumns = @JoinColumn(name = "property_id"),
            inverseJoinColumns = @JoinColumn(name = "feature_id")
    )
    private Set<Feature> features = new HashSet<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
