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

}
