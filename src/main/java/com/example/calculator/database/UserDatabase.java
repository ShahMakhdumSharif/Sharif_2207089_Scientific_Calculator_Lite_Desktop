package com.example.calculator.database;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserDatabase {

    private static final Path DB_PATH = Paths.get("Database", "users.db");
    private static final String URL_PREFIX = "jdbc:sqlite:";

    public static void init() throws SQLException {
        try {
            Path parent = DB_PATH.getParent();
            if (parent != null && Files.notExists(parent)) {
                Files.createDirectories(parent);
            }
        } catch (Exception e) {
            throw new SQLException("Failed to create DB directory", e);
        }

        try (Connection c = getConnection(); Statement s = c.createStatement()) {
            s.execute("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT UNIQUE NOT NULL, password TEXT NOT NULL, role TEXT NOT NULL)");

            s.execute("CREATE TABLE IF NOT EXISTS history (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER NOT NULL, expression TEXT NOT NULL, result TEXT NOT NULL, timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)");

            boolean hasBlocked = false;
            try (ResultSet cols = s.executeQuery("PRAGMA table_info(users)")) {
                while (cols.next()) {
                    String name = cols.getString("name");
                    if ("blocked".equalsIgnoreCase(name)) {
                        hasBlocked = true;
                        break;
                    }
                }
            }
            // ensure unblock_requests table exists (id, user_id, message, timestamp, processed)
            s.execute("CREATE TABLE IF NOT EXISTS unblock_requests (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER NOT NULL, message TEXT, timestamp DATETIME DEFAULT CURRENT_TIMESTAMP, processed INTEGER DEFAULT 0)");
            if (!hasBlocked) {
                try (Statement alter = c.createStatement()) {
                    alter.execute("ALTER TABLE users ADD COLUMN blocked INTEGER DEFAULT 0");
                } catch (SQLException ex) {
                    // default
                }
            }

            try (PreparedStatement ps = c.prepareStatement("SELECT count(*) FROM users WHERE role = ?")) {
                ps.setString(1, "admin");
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) == 0) {
                        try (PreparedStatement ins = c.prepareStatement("INSERT INTO users (username,password,role,blocked) VALUES (?,?,?,?)")) {
                            ins.setString(1, "admin");
                            ins.setString(2, "admin123");
                            ins.setString(3, "admin");
                            ins.setInt(4, 0);
                            ins.executeUpdate();
                        }
                    }
                }
            }
        }
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL_PREFIX + DB_PATH.toAbsolutePath().toString());
    }

    public static boolean registerUser(String username, String password) throws SQLException {
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement("INSERT INTO users (username,password,role) VALUES (?,?,?)")) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, "user");
            int rows = ps.executeUpdate();
            return rows > 0;
        }
    }

    public static boolean validateUser(String username, String password) throws SQLException {
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement("SELECT count(*) FROM users WHERE username = ? AND password = ? AND (blocked IS NULL OR blocked = 0)")) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public static boolean validateAdminByPassword(String password) throws SQLException {
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement("SELECT count(*) FROM users WHERE role = 'admin' AND password = ?")) {
            ps.setString(1, password);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public static java.util.List<String> getAllUsernames() throws SQLException {
        java.util.List<String> list = new java.util.ArrayList<>();
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement("SELECT username FROM users ORDER BY id")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getString("username"));
                }
            }
        }
        return list;
    }

    public static java.util.List<String> getAllNonAdminUsernames() throws SQLException {
        java.util.List<String> list = new java.util.ArrayList<>();
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement("SELECT username FROM users WHERE role <> 'admin' ORDER BY id")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getString("username"));
                }
            }
        }
        return list;
    }

    public static java.util.List<com.example.calculator.model.UserInfo> getAllNonAdminUsers() throws SQLException {
        java.util.List<com.example.calculator.model.UserInfo> list = new java.util.ArrayList<>();
        System.out.println("UserDatabase: DB file path = " + DB_PATH.toAbsolutePath());
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement("SELECT id, username, password, blocked FROM users WHERE role <> 'admin' ORDER BY id")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String username = rs.getString("username");
                    String password = rs.getString("password");
                    list.add(new com.example.calculator.model.UserInfo(id, username, password));
                }
            }
        }
        System.out.println("UserDatabase: fetched non-admin users count = " + list.size());
        return list;
    }

    public static java.util.List<com.example.calculator.model.UserInfo> getBlockedUsers() throws SQLException {
        java.util.List<com.example.calculator.model.UserInfo> list = new java.util.ArrayList<>();
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement("SELECT id, username, password FROM users WHERE role <> 'admin' AND blocked = 1 ORDER BY id")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String username = rs.getString("username");
                    String password = rs.getString("password");
                    list.add(new com.example.calculator.model.UserInfo(id, username, password));
                }
            }
        }
        return list;
    }

    public static boolean blockUserByUsername(String username) throws SQLException {
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement("UPDATE users SET blocked = 1 WHERE username = ?")) {
            ps.setString(1, username);
            return ps.executeUpdate() > 0;
        }
    }

    public static boolean unblockUserByUsername(String username) throws SQLException {
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement("UPDATE users SET blocked = 0 WHERE username = ?")) {
            ps.setString(1, username);
            return ps.executeUpdate() > 0;
        }
    }

    public static java.util.List<com.example.calculator.model.UserInfo> getAllActiveNonAdminUsers() throws SQLException {
        java.util.List<com.example.calculator.model.UserInfo> list = new java.util.ArrayList<>();
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement("SELECT id, username, password FROM users WHERE role <> 'admin' AND (blocked IS NULL OR blocked = 0) ORDER BY id")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String username = rs.getString("username");
                    String password = rs.getString("password");
                    list.add(new com.example.calculator.model.UserInfo(id, username, password));
                }
            }
        }
        return list;
    }

    public static com.example.calculator.model.UserInfo getUserByUsername(String username) throws SQLException {
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement("SELECT id, username, password, role, blocked FROM users WHERE username = ? LIMIT 1")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String u = rs.getString("username");
                    String password = rs.getString("password");
                    return new com.example.calculator.model.UserInfo(id, u, password);
                }
            }
        }
        return null;
    }

    /**
     * Authenticate user by username/password ignoring blocked flag. Returns UserInfo if credentials match.
     */
    public static com.example.calculator.model.UserInfo authenticateUser(String username, String password) throws SQLException {
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement("SELECT id, username, password FROM users WHERE username = ? AND password = ? LIMIT 1")) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String u = rs.getString("username");
                    String pw = rs.getString("password");
                    return new com.example.calculator.model.UserInfo(id, u, pw);
                }
            }
        }
        return null;
    }

    public static boolean isUserBlocked(String username) throws SQLException {
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement("SELECT blocked FROM users WHERE username = ? LIMIT 1")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int b = rs.getInt("blocked");
                    return b == 1;
                }
            }
        }
        return false;
    }

    public static boolean createUnblockRequest(int userId, String message) throws SQLException {
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement("INSERT INTO unblock_requests (user_id, message) VALUES (?,?)")) {
            ps.setInt(1, userId);
            ps.setString(2, message);
            return ps.executeUpdate() > 0;
        }
    }

    public static java.util.List<com.example.calculator.model.UnblockRequest> getPendingUnblockRequests() throws SQLException {
        java.util.List<com.example.calculator.model.UnblockRequest> list = new java.util.ArrayList<>();
        String sql = "SELECT ur.id, ur.user_id, u.username, ur.message, ur.timestamp FROM unblock_requests ur JOIN users u ON ur.user_id = u.id WHERE ur.processed = 0 ORDER BY ur.timestamp DESC";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    int userId = rs.getInt("user_id");
                    String username = rs.getString("username");
                    String message = rs.getString("message");
                    String ts = rs.getString("timestamp");
                    list.add(new com.example.calculator.model.UnblockRequest(id, userId, username, message, ts));
                }
            }
        }
        return list;
    }

    public static boolean markUnblockRequestProcessed(int requestId) throws SQLException {
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement("UPDATE unblock_requests SET processed = 1 WHERE id = ?")) {
            ps.setInt(1, requestId);
            return ps.executeUpdate() > 0;
        }
    }

    public static boolean logHistory(int userId, String expression, String result) throws SQLException {
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement("INSERT INTO history (user_id, expression, result) VALUES (?,?,?)")) {
            ps.setInt(1, userId);
            ps.setString(2, expression);
            ps.setString(3, result);
            return ps.executeUpdate() > 0;
        }
    }

    public static java.util.List<com.example.calculator.model.CalculationHistory> getHistoryForUser(int userId) throws SQLException {
        java.util.List<com.example.calculator.model.CalculationHistory> list = new java.util.ArrayList<>();
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement("SELECT id, expression, result, timestamp FROM history WHERE user_id = ? ORDER BY id DESC")) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String expr = rs.getString("expression");
                    String res = rs.getString("result");
                    String ts = rs.getString("timestamp");
                    list.add(new com.example.calculator.model.CalculationHistory(id, userId, expr, res, ts));
                }
            }
        }
        return list;
    }
}
