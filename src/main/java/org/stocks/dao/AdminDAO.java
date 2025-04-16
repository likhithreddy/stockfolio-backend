package org.stocks.dao;

import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
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




}
