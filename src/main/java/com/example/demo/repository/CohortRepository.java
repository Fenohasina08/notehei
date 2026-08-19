package com.example.demo.repository;

import com.example.demo.entity.JCohort;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CohortRepository extends JpaRepository<JCohort, UUID> {

  Optional<JCohort> findByEntryYear(Integer entryYear);

  boolean existsByEntryYear(Integer entryYear);
}
