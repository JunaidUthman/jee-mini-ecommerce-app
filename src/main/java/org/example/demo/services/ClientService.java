package org.example.demo.services;

import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import jakarta.transaction.Transactional;
import org.example.demo.entities.Client;

import javax.sql.DataSource;
import java.sql.*;

@Stateless  // ✅ Gardez seulement celui-ci
@Transactional
public class ClientService {

    @Resource(lookup = "java:jboss/datasources/MySqlDS")
    private DataSource dataSource;

    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM client WHERE email = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Client getClientByEmail(String email) {
        String sql = "SELECT * FROM client WHERE email = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Client client = new Client();
                    client.setId(rs.getLong("id_client"));
                    client.setEmail(rs.getString("email"));
                    client.setNom(rs.getString("nom"));
                    client.setPrenom(rs.getString("prenom"));
                    client.setPassword(rs.getString("password"));
                    return client;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean save(Client client) {
        System.out.println("Calling clientService.save...");
        if (existsByEmail(client.getEmail())) {
            return false;
        }

        String sql = "INSERT INTO client (email, nom, password, prenom) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, client.getEmail());
            stmt.setString(2, client.getNom());
            stmt.setString(3, client.getPassword());
            stmt.setString(4, client.getPrenom());
            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean clientExists(String email, String password) {
        if (existsByEmail(email)) {
            String sql = "SELECT * FROM client WHERE email = ? AND password = ?";

            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, email);
                stmt.setString(2, password);

                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next();
                }

            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }
}