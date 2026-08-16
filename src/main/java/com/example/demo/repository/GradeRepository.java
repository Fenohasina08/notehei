package com.example.demo.repository;

import com.example.demo.entity.JGrade;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GradeRepository extends JpaRepository<JGrade, UUID> {

  Optional<JGrade> findByStudentIdAndExamId(UUID studentId, UUID examId);

  List<JGrade> findByStudentId(UUID studentId);

  List<JGrade> findByExamId(UUID examId);
}
