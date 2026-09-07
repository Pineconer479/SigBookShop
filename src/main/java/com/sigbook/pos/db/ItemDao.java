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
        // TODO: INSERT INTO items (...) VALUES (...)
        //       use Statement.RETURN_GENERATED_KEYS to get the new id back,
        //       same pattern as SaleDao.recordSale() uses for the sale id
        return -1;
    }

    /** Updates an existing item's details. */
    public void update(int itemId, String name, String description, String icon, double price, int stock) {
        // TODO: UPDATE items SET name = ?, description = ?, icon = ?, price = ?, stock = ? WHERE id = ?
    }

    /** Removes an item from the catalog entirely. */
    public void delete(int itemId) {
        // TODO: DELETE FROM items WHERE id = ?
        // Note: consider whether you want a "hard delete" or to just mark items
        // inactive (add an `active` column) so old sales history referencing
        // this item id still makes sense if you ever join back to it.
    }

}
