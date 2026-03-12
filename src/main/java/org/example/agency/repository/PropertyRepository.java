package org.example.agency.repository;


import org.example.agency.model.Property;
import org.example.agency.model.PropertyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

    List<Property> findByCity(String city);

    List<Property> findByStatus(String status);

    List<Property> findByPropertyType_Name(String propertyTypeName);

    List<Property> findByAgent_Id(Long agentId);

    @Query("SELECT p FROM Property p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    List<Property> findByPriceRange(@Param("minPrice") BigDecimal minPrice,
                                    @Param("maxPrice") BigDecimal maxPrice);

    @Query("SELECT p FROM Property p WHERE p.city = :city AND p.status = :status")
    List<Property> findByCityAndStatus(@Param("city") String city,
                                       @Param("status") String status);
}