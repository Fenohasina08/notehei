package com.example.demo.repository;

import com.example.demo.entity.JStudent;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<JStudent, UUID> {

  Optional<JStudent> findByEmail(String email);

  boolean existsByEmail(String email);

  boolean existsByMatricule(String matricule);

  @Query(
      value =
          "SELECT id, firstname, lastname, email, password, birth_date, address, matricule,"
              + " created_at, updated_at FROM student WHERE cohort_id = :cohortId",
      nativeQuery = true)
  List<JStudent> findByCohortId(@Param("cohortId") UUID cohortId);
}
