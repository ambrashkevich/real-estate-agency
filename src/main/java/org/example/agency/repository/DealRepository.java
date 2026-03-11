package org.example.agency.repository;


import org.example.agency.model.Deal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DealRepository extends JpaRepository<Deal, Long> {

    List<Deal> findByAgent_Id(Long agentId);

    List<Deal> findByClient_Id(Long clientId);

    List<Deal> findByProperty_Id(Long propertyId);

    List<Deal> findByDealType(String dealType);

    List<Deal> findByStatus(String status);

    @Query("SELECT d FROM Deal d WHERE d.dealDate BETWEEN :startDate AND :endDate")
    List<Deal> findByDateRange(@Param("startDate") LocalDateTime startDate,
                               @Param("endDate") LocalDateTime endDate);
}