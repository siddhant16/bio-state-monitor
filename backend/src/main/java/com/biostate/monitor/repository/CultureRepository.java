package com.biostate.monitor.repository;

import com.biostate.monitor.model.Culture;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CultureRepository extends JpaRepository<Culture, Long> {
    List<Culture> findByUserId(Long userId);
}