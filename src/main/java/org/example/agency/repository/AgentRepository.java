package org.example.agency.repository;


import org.example.agency.model.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AgentRepository extends JpaRepository<Agent, Long> {

    Optional<Agent> findByEmail(String email);

    Optional<Agent> findByLicenseNumber(String licenseNumber);
}
