package com.example.demo.endpoint.controller;

import com.example.demo.model.GroupMembership;
import com.example.demo.service.GroupMembershipService;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/group-memberships")
@AllArgsConstructor
public class GroupMembershipController {

  private final GroupMembershipService groupMembershipService;

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<GroupMembership> assign(@RequestBody Map<String, Object> body) {
    UUID studentId = UUID.fromString((String) body.get("studentId"));
    UUID groupId = UUID.fromString((String) body.get("groupId"));
    LocalDate startDate = LocalDate.parse((String) body.get("startDate"));
    GroupMembership created = groupMembershipService.assignToGroup(studentId, groupId, startDate);
    return ResponseEntity.created(URI.create("/group-memberships/" + created.id())).body(created);
  }

  @GetMapping("/student/{studentId}")
  @PreAuthorize(
      "hasAnyRole('ADMIN','TEACHER') or (hasRole('STUDENT') and"
          + " #studentId.equals(authentication.principal.id()))")
  public ResponseEntity<List<GroupMembership>> getHistoryForStudent(@PathVariable UUID studentId) {
    return ResponseEntity.ok(groupMembershipService.getHistoryForStudent(studentId));
  }

  @GetMapping("/student/{studentId}/active")
  @PreAuthorize(
      "hasAnyRole('ADMIN','TEACHER') or (hasRole('STUDENT') and"
          + " #studentId.equals(authentication.principal.id()))")
  public ResponseEntity<GroupMembership> getActiveMembership(@PathVariable UUID studentId) {
    return ResponseEntity.ok(groupMembershipService.getActiveMembership(studentId));
  }

  @GetMapping("/group/{groupId}")
  @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
  public ResponseEntity<List<GroupMembership>> getMembersOfGroup(@PathVariable UUID groupId) {
    return ResponseEntity.ok(groupMembershipService.getMembersOfGroup(groupId));
  }
}
