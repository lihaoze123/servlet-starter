package org.example.demo.dao;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.example.demo.model.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class UserDAO {
    @NonNull
    private final DataSource ds;

    public User getById(Integer id) {
        if (id == null) {
            return null;
        }
        String sql = "SELECT id, username, email FROM users WHERE id = ?";
        try (Connection conn = ds.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rowMapper(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to query user by id=" + id, e);
        }
    }

    public List<User> listAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, username, email FROM users ORDER BY id";
        try (Connection conn = ds.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                users.add(rowMapper(rs));
            }
            return users;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to query user list", e);
        }
    }

    public User create(User user) {
        String sql = "INSERT INTO users (username, email) VALUES (?, ?)";
        try (Connection conn = ds.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return new User(keys.getInt(1), user.getUsername(), user.getEmail());
                }
            }
            throw new SQLException("Creating user failed, no generated ID obtained");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create user", e);
        }
    }

    private User rowMapper(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("email")
        );
    }
}
