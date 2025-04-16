package org.stocks.controller;

import org.springframework.web.bind.annotation.*;
import org.stocks.dao.AdminDAO;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminDAO adminDAO;

    public AdminController(AdminDAO adminDAO) {
        this.adminDAO = adminDAO;
    }

    @PostMapping("/add-stock")
    public ResponseEntity<?> addStock(@RequestBody Map<String, Object> stockData) {
        System.out.println(stockData);
        try {
            adminDAO.addStock(stockData);
            return ResponseEntity.ok(Map.of("message", "Stock inserted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/add-user")
    public ResponseEntity<?> addUser(@RequestBody Map<String, String> userData) {
        try {
            adminDAO.addUser(userData);
            return ResponseEntity.ok(Map.of("message", "User added successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/add-exchange")
    public ResponseEntity<?> addStockExchange(@RequestBody Map<String, Object> exchangeData) {
        try {
            adminDAO.addStockExchange(exchangeData);
            return ResponseEntity.ok(Map.of("message", "Stock Exchange added successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/update-kyc")
    public ResponseEntity<?> updateKYC(@RequestBody Map<String, Object> kycData) {
        try {
            int userId = Integer.parseInt(kycData.get("user_id").toString());
            boolean kycStatus = Boolean.parseBoolean(kycData.get("is_kyc_done").toString());

            adminDAO.updateUserKYC(userId, kycStatus);
            return ResponseEntity.ok(Map.of("message", "KYC status updated"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }


    @PostMapping("/update-price-history")
    public ResponseEntity<?> updateStockPriceHistory(@RequestBody Map<String, Object> priceData) {
        try {
            adminDAO.updateStockPriceHistory(priceData);
            return ResponseEntity.ok(Map.of("message", "Stock price history updated"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/add-market-news")
    public ResponseEntity<?> addMarketNews(@RequestBody Map<String, Object> newsData) {
        try {
            adminDAO.addMarketNews(newsData);
            return ResponseEntity.ok(Map.of("message", "Market news added successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsersWithKyc() {
        try {
            return ResponseEntity.ok(adminDAO.getAllUsersWithKyc());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/approve-kyc")
    public ResponseEntity<?> approveKyc(@RequestBody Map<String, Object> data) {
        try {
            int userId = Integer.parseInt(data.get("user_id").toString());
            adminDAO.approveKyc(userId);
            return ResponseEntity.ok(Map.of("message", "KYC approved"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/revoke-kyc")
    public ResponseEntity<?> revokeKyc(@RequestBody Map<String, Object> data) {
        try {
            int userId = Integer.parseInt(data.get("user_id").toString());
            adminDAO.revokeKyc(userId);
            return ResponseEntity.ok(Map.of("message", "KYC revoked"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/stocks")
    public ResponseEntity<?> getAllStocks() {
        try {
            return ResponseEntity.ok(adminDAO.getAllStocks());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/stocks/update-price")
    public ResponseEntity<?> updateStockPrice(@RequestBody Map<String, Object> data) {
        try {
            int stockId = (int) data.get("stock_id");
            BigDecimal newPrice = new BigDecimal(data.get("new_price").toString());
            adminDAO.updateStockPrice(stockId, newPrice);
            return ResponseEntity.ok(Map.of("message", "Stock price updated"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/stock-exchanges")
    public ResponseEntity<?> getAllStockExchanges() {
        try {
            return ResponseEntity.ok(adminDAO.getAllStockExchanges());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }


}
