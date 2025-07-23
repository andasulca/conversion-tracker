package io.github.andasulca.conversiontracker.repository;

import io.github.andasulca.conversiontracker.entity.SalesData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalesDataRepository extends JpaRepository<SalesData, Long> {
}