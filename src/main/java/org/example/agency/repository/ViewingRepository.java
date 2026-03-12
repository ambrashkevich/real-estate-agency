package org.example.agency.repository;

import org.example.agency.model.Viewing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ViewingRepository extends JpaRepository<Viewing, Long> {
}
