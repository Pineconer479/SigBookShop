package com.sigbook.pos.db;

import com.sigbook.pos.model.Item;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ItemDao {

    public List<Item> findAll() {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT id, name, description, icon, price, stock FROM items ORDER BY name";
        try (Statement st = Database.get().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                items.add(new Item(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("icon"),
                        rs.getDouble("price"),
                        rs.getInt("stock")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load items: " + e.getMessage(), e);
        }
        return items;
    }

    public void decrementStock(int itemId, int quantity) {
        String sql = "UPDATE items SET stock = stock - ? WHERE id = ?";
        try (PreparedStatement ps = Database.get().prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setInt(2, itemId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update stock: " + e.getMessage(), e);
        }
    }

    public void setStock(int itemId, int newStock) {
        String sql = "UPDATE items SET stock = ? WHERE id = ?";
        try (PreparedStatement ps = Database.get().prepareStatement(sql)) {
            ps.setInt(1, newStock);
            ps.setInt(2, itemId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to set stock: " + e.getMessage(), e);
        }
    }

    /** Adds a new item to the catalog. Returns the generated id. */
    public int insert(String name, String description, String icon, double price, int stock) {

        String sql = "INSERT INTO items (name, description, icon, price, stock) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = Database.get().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, description);
            ps.setString(3, icon);
            ps.setDouble(4, price);
            ps.setInt(5, stock);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
            return -1;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert item: " + e.getMessage(), e);
        }
    }

    /** Updates an existing item's details. */
    public void update(int itemId, String name, String description, String icon, double price, int stock) {

        String sql = "UPDATE items SET name = ?, description = ?, icon = ?, price = ?, stock = ? WHERE id = ?";
        try (PreparedStatement ps = Database.get().prepareStatement(sql)) {
             ps.setString(1, name);
             ps.setString(2, description);
             ps.setString(3, icon);
             ps.setDouble(4, price);
             ps.setInt(5, stock);
             ps.setInt(6, itemId);
             ps.executeUpdate();
             } catch (SQLException e) {

            throw new RuntimeException("Failed to update stock: " + e.getMessage(), e);
        }
    }

    /** Removes an item from the catalog entirely. */
    public void delete(int itemId) {

               String sql = "DELETE FROM items WHERE id = ?";
        try (PreparedStatement ps = Database.get().prepareStatement(sql)) {
            ps.setInt(1, itemId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to remove item: " + e.getMessage(), e);
        }
    }

}
