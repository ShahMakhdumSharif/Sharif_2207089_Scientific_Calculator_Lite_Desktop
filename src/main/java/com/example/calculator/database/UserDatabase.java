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

            try (PreparedStatement ps = c.prepareStatement("SELECT count(*) FROM users WHERE role = ?")) {
                ps.setString(1, "admin");
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) == 0) {
                        try (PreparedStatement ins = c.prepareStatement("INSERT INTO users (username,password,role) VALUES (?,?,?)")) {
                            ins.setString(1, "admin");
                            ins.setString(2, "admin123");
                            ins.setString(3, "admin");
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
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement("SELECT count(*) FROM users WHERE username = ? AND password = ?")) {
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
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement("SELECT id, username, password FROM users WHERE role <> 'admin' ORDER BY id")) {
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
}
