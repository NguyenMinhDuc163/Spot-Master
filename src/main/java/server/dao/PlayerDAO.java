package server.dao;

import Client.model.entities.Player;

import java.sql.*;
import java.util.List;

public class PlayerDAO implements GenericDAO<Player>{
    private Connection connection;

    public PlayerDAO() {
        this.connection = DBConnection.getConnection();
    }

    public Player getPlayerByUsername(String username) {
        String sql = "SELECT * FROM players WHERE username = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Player(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getInt("total_score")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean authenticate(String username, String password) {
        Player player = getPlayerByUsername(username);
        if (player != null && player.getPassword().equals(password)) {
            return true;
        }
        return false;
    }

    @Override
    public void add(Player player) {
        String sql = "INSERT INTO players (username, password, total_score) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, player.getUsername());
            ps.setString(2, player.getPassword());
            ps.setInt(3, player.getTotalScore());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Player getById(int id) {
        String sql = "SELECT * FROM players WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Player(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getInt("total_score")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Player> getAll() {
        return List.of();
    }

    @Override
    public void update(Player player) {

    }

    @Override
    public void delete(int id) {

    }
}
