package com.example.calculator;

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
            if (Files.notExists(DB_PATH.getParent())) {
                Files.createDirectories(DB_PATH.getParent());
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
}
