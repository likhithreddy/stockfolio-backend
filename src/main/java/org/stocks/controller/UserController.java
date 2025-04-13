package org.stocks.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.stocks.dao.UserDAO;

import java.util.Map;

@RestController

public class UserController {

  private final UserDAO userDAO;

  public UserController(UserDAO userDAO) {
    this.userDAO = userDAO;
  }

  @GetMapping("/dashboard")
  public ResponseEntity<?> getDashboard(@RequestParam int userId) {
    try {
      Map<String, Object> dashboard = userDAO.getUserDashboard(userId);
      return ResponseEntity.ok(dashboard);
    } catch (Exception e) {
      return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
    }
  }

  @GetMapping("/user/dashboard")
  public ResponseEntity<?> getInvestorDashboard(@RequestParam int userId) {
    try {
      Map<String, Object> data = userDAO.getFullUserDashboard(userId);
      return ResponseEntity.ok(data);
    } catch (Exception e) {
      return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
    }
  }

  @PostMapping("/goal")
  public ResponseEntity<?> saveGoal(@RequestBody Map<String, Object> goalData) {
    try {
      userDAO.saveOrUpdateGoal(goalData);
      return ResponseEntity.ok(Map.of("message", "Goal saved successfully"));
    } catch (Exception e) {
      return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
    }
  }

  @DeleteMapping("/goal/{goalId}")
  public ResponseEntity<?> deleteGoal(@PathVariable int goalId) {
    try {
      userDAO.deleteGoal(goalId);
      return ResponseEntity.ok(Map.of("message", "Goal deleted successfully"));
    } catch (Exception e) {
      return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
    }
  }


}
