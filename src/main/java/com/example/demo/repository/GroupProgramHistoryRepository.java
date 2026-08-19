package com.example.demo.repository;

import com.example.demo.entity.JGroupProgramHistory;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupProgramHistoryRepository extends JpaRepository<JGroupProgramHistory, UUID> {

  List<JGroupProgramHistory> findByGroupIdOrderByStartDateDesc(UUID groupId);

  Optional<JGroupProgramHistory> findByGroupIdAndEndDateIsNull(UUID groupId);
}
