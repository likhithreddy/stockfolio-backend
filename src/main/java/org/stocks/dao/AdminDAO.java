package org.stocks.dao;

import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class AdminDAO {

    private final DataSource dataSource;

    public AdminDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void addStock(Map<String, Object> data) throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            CallableStatement stmt = conn.prepareCall("{CALL sp_insert_stock(?, ?, ?, ?, ?, ?, ?, ?, ?)}");

            stmt.setString(1, (String) data.get("symbol"));
            stmt.setString(2, (String) data.get("company_name"));
            stmt.setDouble(3, Double.parseDouble(data.get("current_value").toString()));
            stmt.setString(4, (String) data.get("sector"));
            stmt.setDouble(5, Double.parseDouble(data.get("market_cap").toString()));
            stmt.setTimestamp(6, new java.sql.Timestamp(System.currentTimeMillis()));
            stmt.setString(7, (String) data.get("volatility"));
            stmt.setDouble(8, Double.parseDouble(data.get("average_return").toString()));
            stmt.setString(9, (String) data.get("stock_exchange"));

            stmt.execute();
        }
    }

    public void addUser(Map<String, String> data) throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            CallableStatement stmt = conn.prepareCall("{CALL sp_register_user(?, ?, ?, ?)}");

            stmt.setString(1, data.get("firstname"));
            stmt.setString(2, data.get("lastname"));
            stmt.setString(3, data.get("email"));
            stmt.setString(4, data.get("password"));

            stmt.execute();
        }
    }

    public void addStockExchange(Map<String, Object> data) throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            CallableStatement stmt = conn.prepareCall("{CALL sp_insert_stock_exchange(?, ?, ?, ?, ?)}");

            stmt.setString(1, (String) data.get("name"));
            stmt.setString(2, (String) data.get("country"));
            stmt.setString(3, (String) data.get("timezone"));
            stmt.setString(4, (String) data.get("opening_time"));
            stmt.setString(5, (String) data.get("closing_time"));

            stmt.execute();
        }
    }

    public void updateUserKYC(int userId, boolean kycStatus) throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            CallableStatement stmt = conn.prepareCall("{CALL sp_update_kyc_status(?, ?)}");

            stmt.setInt(1, userId);
            stmt.setBoolean(2, kycStatus);

            stmt.execute();
        }
    }

    public void updateStockPriceHistory(Map<String, Object> data) throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            CallableStatement stmt = conn.prepareCall("{CALL sp_update_stock_price_history(?, ?, ?, ?, ?, ?, ?)}");

            stmt.setInt(1, Integer.parseInt(data.get("stock_id").toString()));
            stmt.setDouble(2, Double.parseDouble(data.get("opening_price").toString()));
            stmt.setDouble(3, Double.parseDouble(data.get("closing_price").toString()));
            stmt.setDouble(4, Double.parseDouble(data.get("highest_price").toString()));
            stmt.setDouble(5, Double.parseDouble(data.get("lowest_price").toString()));
            stmt.setDouble(6, Double.parseDouble(data.get("adjusted_close").toString()));
            stmt.setLong(7, Long.parseLong(data.get("volume").toString()));

            stmt.execute();
        }
    }

    public void addMarketNews(Map<String, Object> data) throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            CallableStatement stmt = conn.prepareCall("{CALL sp_add_market_news(?, ?, ?, ?)}");

            stmt.setInt(1, Integer.parseInt(data.get("stock_id").toString()));
            stmt.setString(2, (String) data.get("headline"));
            stmt.setString(3, (String) data.get("news_source"));
            stmt.setInt(4, Integer.parseInt(data.get("impact_score").toString()));

            stmt.execute();
        }
    }

    public List<Map<String, Object>> getAllUsersWithKyc() throws SQLException {
        List<Map<String, Object>> users = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             CallableStatement stmt = conn.prepareCall("{CALL sp_get_all_users_kyc()}");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(Map.of(
                        "user_id", rs.getInt("user_id"),
                        "firstname", rs.getString("firstname"),
                        "lastname", rs.getString("lastname"),
                        "email", rs.getString("email"),
                        "is_kyc_done", rs.getBoolean("is_kyc_done")
                ));
            }
        }
        return users;
    }

    public void approveKyc(int userId) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             CallableStatement stmt = conn.prepareCall("{CALL sp_approve_kyc(?)}")) {
            stmt.setInt(1, userId);
            stmt.execute();
        }
    }

    public void revokeKyc(int userId) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             CallableStatement stmt = conn.prepareCall("{CALL sp_revoke_kyc(?)}")) {
            stmt.setInt(1, userId);
            stmt.execute();
        }
    }

    public List<Map<String, Object>> getAllStocks() throws SQLException {
        List<Map<String, Object>> stocks = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             CallableStatement stmt = conn.prepareCall("{CALL sp_get_all_stocks()}");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> stock = new HashMap<>();
                stock.put("stock_id", rs.getInt("stock_id"));
                stock.put("symbol", rs.getString("symbol"));
                stock.put("company_name", rs.getString("company_name"));
                stock.put("current_value", rs.getBigDecimal("current_value"));
                stock.put("sector", rs.getString("sector"));
                stock.put("market_cap", rs.getBigDecimal("market_cap"));
                stock.put("volatility", rs.getString("volatility"));
                stock.put("average_return_percentage", rs.getBigDecimal("average_return_percentage"));
                stock.put("last_updated_date", rs.getTimestamp("last_updated_date"));
                stock.put("exchange_name", rs.getString("exchange_name"));
                stocks.add(stock);
            }
        }
        return stocks;
    }

    public void updateStockPrice(int stockId, BigDecimal newPrice) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             CallableStatement stmt = conn.prepareCall("{CALL sp_update_stock_price(?, ?)}")) {
            stmt.setInt(1, stockId);
            stmt.setBigDecimal(2, newPrice);
            stmt.execute();
        }
    }

    public List<Map<String, Object>> getAllStockExchanges() throws SQLException {
        List<Map<String, Object>> exchanges = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             CallableStatement stmt = conn.prepareCall("{CALL sp_get_all_stock_exchanges()}");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> exchange = new HashMap<>();
                exchange.put("stock_exchange_id", rs.getInt("stock_exchange_id"));
                exchange.put("stock_exchange_name", rs.getString("stock_exchange_name"));
                exchange.put("country", rs.getString("country"));
                exchange.put("opening_time", rs.getString("opening_time"));
                exchange.put("closing_time", rs.getString("closing_time"));
                exchange.put("timezone", rs.getString("timezone"));
                exchanges.add(exchange);
            }
        }
        return exchanges;
    }



    public List<Map<String, Object>> getAllMarketNews() throws SQLException {
        List<Map<String, Object>> newsList = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             CallableStatement stmt = conn.prepareCall("{CALL sp_get_all_market_news()}")) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                newsList.add(Map.of(
                    "news_id", rs.getInt("news_id"),
                    "headline", rs.getString("headline"),
                    "news_source", rs.getString("news_source"),
                    "publication_date", rs.getDate("publication_date").toString(),
                    "impact_score", rs.getInt("impact_score"),
                    "stock_id", rs.getInt("stock_id"),
                    "symbol", rs.getString("symbol"),
                    "company_name", rs.getString("company_name")
                ));
            }
        }

        return newsList;
    }
}
