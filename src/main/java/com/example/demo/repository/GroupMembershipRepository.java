package com.example.demo.repository;

import com.example.demo.entity.JGroupMembership;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupMembershipRepository extends JpaRepository<JGroupMembership, UUID> {

  List<JGroupMembership> findByStudentIdOrderByStartDateDesc(UUID studentId);

  List<JGroupMembership> findByGroupId(UUID groupId);

  Optional<JGroupMembership> findByStudentIdAndEndDateIsNull(UUID studentId);
}
