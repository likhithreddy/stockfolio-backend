package org.stocks.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.stocks.dao.UserDAO;

import java.math.BigDecimal;
import java.sql.SQLException;
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

  @PutMapping("/goal")
  public ResponseEntity<?> updateGoal(@RequestBody Map<String, Object> goalData) {
    try {
      userDAO.saveOrUpdateGoal(goalData);
      return ResponseEntity.ok(Map.of("message", "Goal updated successfully"));
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

  @PostMapping("/preferences")
  public ResponseEntity<?> updatePreferences(@RequestBody Map<String, Object> prefs) {
    try {
      int userId = Integer.parseInt(prefs.get("user_id").toString());
      String sector = (String) prefs.get("preferred_sector");
      String risk = (String) prefs.get("preferred_risk_level");
      boolean notify = Boolean.parseBoolean(prefs.get("notification_enabled").toString());

      userDAO.updateUserPreferences(userId, sector, risk, notify);
      return ResponseEntity.ok(Map.of("message", "Preferences updated"));
    } catch (Exception e) {
      return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
    }
  }

  @PostMapping("/trade")
  public ResponseEntity<?> executeTrade(@RequestBody Map<String, Object> tradeData) {
    try {
      userDAO.executeTrade(tradeData);
      return ResponseEntity.ok(Map.of("message", "Trade executed successfully"));
    } catch (Exception e) {
      return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
    }
  }

  @PostMapping("/watchlist/add")
  public ResponseEntity<?> addToWatchlist(@RequestBody Map<String, Object> data) {
    try {
      int userId = Integer.parseInt(data.get("user_id").toString());
      int stockId = Integer.parseInt(data.get("stock_id").toString());
      userDAO.addToWatchlist(userId, stockId);
      return ResponseEntity.ok(Map.of("message", "Stock added to watchlist"));
    } catch (Exception e) {
      return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
    }
  }

  @GetMapping("/stock/all")
  public ResponseEntity<?> getAllStocks() {
    try {
      return ResponseEntity.ok(userDAO.getAllStocks());
    } catch (Exception e) {
      return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
    }
  }

  @PostMapping("/watchlist/remove")
  public ResponseEntity<?> removeFromWatchlist(@RequestBody Map<String, Object> data) {
    try {
      int userId = Integer.parseInt(data.get("user_id").toString());
      int stockId = Integer.parseInt(data.get("stock_id").toString());
      userDAO.removeFromWatchlist(userId, stockId);
      return ResponseEntity.ok(Map.of("message", "Removed from watchlist"));
    } catch (Exception e) {
      return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
    }
  }

  @PostMapping("/portfolio")
  public ResponseEntity<?> createPortfolio(@RequestBody Map<String, Object> data) {
    try {
      int userId = Integer.parseInt(data.get("user_id").toString());
      String name = data.get("portfolio_name").toString();
      userDAO.createPortfolio(userId, name);
      return ResponseEntity.ok(Map.of("message", "Portfolio created"));
    } catch (SQLException e) {
      if (e.getMessage().toLowerCase().contains("duplicate")) {
        return ResponseEntity.badRequest().body(Map.of("error", "Portfolio name already exists."));
      }
      return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(500).body(Map.of("error", "Unexpected error: " + e.getMessage()));
    }
  }


  @GetMapping("/user/news")
  public ResponseEntity<?> getUserNews(@RequestParam int userId) {
    try {
      return ResponseEntity.ok(userDAO.getUserNews(userId));
    } catch (Exception e) {
      return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
    }
  }

  @PostMapping("/funds/update")
  public ResponseEntity<?> updateFunds(@RequestBody Map<String, Object> data) {
    try {
      int userId = Integer.parseInt(data.get("user_id").toString());
      BigDecimal amount = new BigDecimal(data.get("amount").toString());
      String operation = data.get("operation").toString().toUpperCase(); // 'ADD' or 'WITHDRAW'

      userDAO.updateFunds(userId, amount, operation);
      return ResponseEntity.ok(Map.of("message", "Funds updated successfully"));
    } catch (Exception e) {
      return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
    }
  }


}
