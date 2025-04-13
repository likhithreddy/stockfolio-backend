package org.stocks.dao;

import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserDAO {

    private final DataSource dataSource;

    public UserDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Map<String, Object> validateLogin(String email, String password) throws SQLException {
        Map<String, Object> result = new HashMap<>();

        try (Connection conn = dataSource.getConnection()) {
            CallableStatement stmt = conn.prepareCall("{CALL sp_login_user(?, ?, ?, ?, ?)}");

            stmt.setString(1, email);
            stmt.setString(2, password);
            stmt.registerOutParameter(3, Types.INTEGER);   // userId
            stmt.registerOutParameter(4, Types.VARCHAR);   // lastName
            stmt.registerOutParameter(5, Types.VARCHAR);   // role

            stmt.execute();

            int userId = stmt.getInt(3);
            String lastName = stmt.getString(4);
            String role = stmt.getString(5);

            if (userId > 0) {
                result.put("userId", userId);
                result.put("lastName", lastName);
                result.put("role", role);
            }
        }

        return result;
    }

    public void registerUser(Map<String, String> userData) throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            CallableStatement stmt = conn.prepareCall("{CALL sp_register_user(?, ?, ?, ?)}");

            stmt.setString(1, userData.get("firstname"));
            stmt.setString(2, userData.get("lastname"));
            stmt.setString(3, userData.get("email"));
            stmt.setString(4, userData.get("secret"));

            stmt.execute();
        }
    }

    public Map<String, Object> getUserDashboard(int userId) throws Exception {
        Map<String, Object> result = new HashMap<>();

        try (Connection conn = dataSource.getConnection()) {
            CallableStatement stmt = conn.prepareCall("{CALL sp_get_user_dashboard(?)}");
            stmt.setInt(1, userId);
            boolean hasResults = stmt.execute();

            if (hasResults) {
                try (ResultSet userRs = stmt.getResultSet()) {
                    if (userRs.next()) {
                        result.put("firstname", userRs.getString("firstname"));
                        result.put("lastname", userRs.getString("lastname"));
                        result.put("is_kyc_done", userRs.getBoolean("is_kyc_done"));
                    }
                }
            }

            if (stmt.getMoreResults()) {
                try (ResultSet portfolioRs = stmt.getResultSet()) {
                    List<Map<String, Object>> portfolios = new ArrayList<>();
                    while (portfolioRs.next()) {
                        Map<String, Object> port = new HashMap<>();
                        port.put("portfolio_id", portfolioRs.getInt("portfolio_id"));
                        port.put("portfolio_name", portfolioRs.getString("portfolio_name"));
                        portfolios.add(port);
                    }
                    result.put("portfolios", portfolios);
                }
            }
        }

        return result;
    }

    public Map<String, Object> getFullUserDashboard(int userId) throws Exception {
        Map<String, Object> dashboard = new HashMap<>();

        try (Connection conn = dataSource.getConnection()) {
            CallableStatement stmt = conn.prepareCall("{CALL sp_get_full_user_dashboard(?)}");
            stmt.setInt(1, userId);

            boolean hasResults = stmt.execute();

            // 1. User Info
            if (hasResults) {
                try (ResultSet rs = stmt.getResultSet()) {
                    if (rs.next()) {
                        dashboard.put("userInfo", Map.of(
                            "firstname", rs.getString("firstname"),
                            "lastname", rs.getString("lastname"),
                            "email", rs.getString("email"),
                            "is_kyc_done", rs.getBoolean("is_kyc_done")
                        ));
                    }
                }
            }

            // 2. Preferences
            if (stmt.getMoreResults()) {
                try (ResultSet rs = stmt.getResultSet()) {
                    if (rs.next()) {
                        dashboard.put("preferences", Map.of(
                            "preferred_sector", rs.getString("preferred_sector"),
                            "preferred_risk_level", rs.getString("preferred_risk_level"),
                            "notification_enabled", rs.getBoolean("notification_enabled")
                        ));
                    }
                }
            }

            // 3. Investment Goals
            if (stmt.getMoreResults()) {
                try (ResultSet rs = stmt.getResultSet()) {
                    List<Map<String, Object>> goals = new ArrayList<>();
                    while (rs.next()) {
                        goals.add(Map.of(
                            "goal_id", rs.getInt("goal_id"),
                            "goal_name", rs.getString("goal_name"),
                            "target_amount", rs.getBigDecimal("target_amount"),
                            "target_date", rs.getDate("target_date"),
                            "progress", rs.getBigDecimal("progress")
                        ));
                    }
                    dashboard.put("goals", goals);
                }
            }

            // 4. Portfolios
            if (stmt.getMoreResults()) {
                try (ResultSet rs = stmt.getResultSet()) {
                    List<Map<String, Object>> portfolios = new ArrayList<>();
                    while (rs.next()) {
                        portfolios.add(Map.of(
                            "portfolio_id", rs.getInt("portfolio_id"),
                            "portfolio_name", rs.getString("portfolio_name"),
                            "created_date", rs.getTimestamp("created_date")
                        ));
                    }
                    dashboard.put("portfolios", portfolios);
                }
            }

            // 5. Holdings
            if (stmt.getMoreResults()) {
                try (ResultSet rs = stmt.getResultSet()) {
                    List<Map<String, Object>> holdings = new ArrayList<>();
                    while (rs.next()) {
                        holdings.add(Map.of(
                            "portfolio_id", rs.getInt("portfolio_id"),
                            "stock_id", rs.getInt("stock_id"),
                            "symbol", rs.getString("symbol"),
                            "company_name", rs.getString("company_name"),
                            "sector", rs.getString("sector"),
                            "shares_owned", rs.getInt("shares_owned"),
                            "stock_average_price", rs.getBigDecimal("stock_average_price"),
                            "current_value", rs.getBigDecimal("current_value"),
                            "total_value", rs.getBigDecimal("total_value")
                        ));
                    }
                    dashboard.put("holdings", holdings);
                }
            }

            // 6. Watchlist
            if (stmt.getMoreResults()) {
                try (ResultSet rs = stmt.getResultSet()) {
                    List<Map<String, Object>> watchlist = new ArrayList<>();
                    while (rs.next()) {
                        watchlist.add(Map.of(
                            "stock_id", rs.getInt("stock_id"),
                            "symbol", rs.getString("symbol"),
                            "company_name", rs.getString("company_name"),
                            "current_value", rs.getBigDecimal("current_value")
                        ));
                    }
                    dashboard.put("watchlist", watchlist);
                }
            }
        }

        return dashboard;
    }


    public void saveOrUpdateGoal(Map<String, Object> goal) throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            CallableStatement stmt = conn.prepareCall("{CALL sp_add_or_update_goal(?, ?, ?, ?)}");

            stmt.setInt(1, Integer.parseInt(goal.get("user_id").toString()));
            stmt.setString(2, (String) goal.get("goal_name"));
            stmt.setBigDecimal(3, new BigDecimal(goal.get("target_amount").toString()));
            stmt.setDate(4, Date.valueOf(goal.get("target_date").toString()));

            stmt.execute();
        }
    }

    public void deleteGoal(int goalId) throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            CallableStatement stmt = conn.prepareCall("{CALL sp_delete_goal(?)}");
            stmt.setInt(1, goalId);
            stmt.execute();
        }
    }

    public void updateUserPreferences(int userId, String sector, String riskLevel, boolean notify) throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            CallableStatement stmt = conn.prepareCall("{CALL sp_set_user_preferences(?, ?, ?, ?)}");
            stmt.setInt(1, userId);
            stmt.setString(2, sector);
            stmt.setString(3, riskLevel);
            stmt.setBoolean(4, notify);
            stmt.execute();
        }
    }

    public void executeTrade(Map<String, Object> trade) throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            CallableStatement stmt = conn.prepareCall("{CALL sp_execute_trade(?, ?, ?, ?, ?, ?, ?)}");

            stmt.setInt(1, Integer.parseInt(trade.get("user_id").toString()));
            stmt.setInt(2, Integer.parseInt(trade.get("portfolio_id").toString()));
            stmt.setInt(3, Integer.parseInt(trade.get("stock_id").toString()));
            stmt.setString(4, trade.get("buy_or_sell").toString());
            stmt.setInt(5, Integer.parseInt(trade.get("quantity").toString()));
            stmt.setString(6, trade.get("order_type").toString());
            stmt.setString(7, trade.get("transaction_mode").toString());

            stmt.execute();
        }
    }

    public void addToWatchlist(int userId, int stockId) throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            CallableStatement stmt = conn.prepareCall("{CALL sp_add_to_watchlist(?, ?)}");
            stmt.setInt(1, userId);
            stmt.setInt(2, stockId);
            stmt.execute();
        }
    }

    public List<Map<String, Object>> getAllStocks() throws Exception {
        List<Map<String, Object>> stocks = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM vw_all_stocks");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                stocks.add(Map.of(
                    "stock_id", rs.getInt("stock_id"),
                    "symbol", rs.getString("symbol"),
                    "company_name", rs.getString("company_name"),
                    "current_value", rs.getBigDecimal("current_value")
                ));
            }
        }
        return stocks;
    }

    public void removeFromWatchlist(int userId, int stockId) throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            CallableStatement stmt = conn.prepareCall("{CALL sp_remove_from_watchlist(?, ?)}");
            stmt.setInt(1, userId);
            stmt.setInt(2, stockId);
            stmt.execute();
        }
    }



}
