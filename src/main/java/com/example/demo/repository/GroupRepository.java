package com.example.demo.repository;

import com.example.demo.entity.JGroup;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<JGroup, UUID> {

  List<JGroup> findByCohortId(UUID cohortId);

  Optional<JGroup> findByReferenceAndCohortId(String reference, UUID cohortId);

  boolean existsByReferenceAndCohortId(String reference, UUID cohortId);
}
