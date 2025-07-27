package io.github.andasulca.conversiontracker.repository;

import io.github.andasulca.conversiontracker.entity.SalesData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SalesDataRepository extends JpaRepository<SalesData, Long> {

    @Query("""
        SELECT s FROM SalesData s
        WHERE s.landingPageCode = :code
        AND COALESCE(s.saleDate, s.visitDate) BETWEEN :from AND :to
    """)
    List<SalesData> findByLandingPageCodeAndTimestampBetween(
            @Param("code") String code,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    @Query("""
        SELECT s FROM SalesData s
        WHERE COALESCE(s.saleDate, s.visitDate) BETWEEN :from AND :to
    """)
    List<SalesData> findByTimestampBetween(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    boolean existsByTrackingIdAndVisitDateAndSaleDateAndProductIdAndActionType(
            String trackingId,
            LocalDateTime visitDate,
            LocalDateTime saleDate,
            String productId,
            String actionType
    );
}
