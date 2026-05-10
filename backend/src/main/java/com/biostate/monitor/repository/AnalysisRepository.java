package com.biostate.monitor.repository;

import com.biostate.monitor.model.Analysis;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AnalysisRepository extends JpaRepository<Analysis, Long> {
    List<Analysis> findByCultureIdOrderByCreatedAtDesc(Long cultureId);
}