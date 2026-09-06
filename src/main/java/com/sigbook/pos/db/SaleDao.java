package com.sigbook.pos.db;

import com.sigbook.pos.model.CartLine;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * Records completed sales (the receipt header) and their line items.
 * GST is calculated assuming prices are GST-inclusive (standard for AU retail):
 * GST component = total / 11.
 */
public class SaleDao {

    private static final double GST_DIVISOR = 11.0; // for 10% GST, inclusive pricing

    public record SaleResult(String saleNumber, double subtotal, double gst, double total) {}

    public SaleResult recordSale(List<CartLine> cartLines, String paymentMethod) {
        double total = cartLines.stream().mapToDouble(CartLine::getLineTotal).sum();
        double gst = total / GST_DIVISOR;
        double subtotal = total - gst;

        try {
            String saleNumber = Database.nextSaleNumber();
            var conn = Database.get();
            conn.setAutoCommit(false);
            try {
                long saleId;
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO sales (sale_number, payment_method, subtotal, gst, total) VALUES (?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, saleNumber);
                    ps.setString(2, paymentMethod);
                    ps.setDouble(3, subtotal);
                    ps.setDouble(4, gst);
                    ps.setDouble(5, total);
                    ps.executeUpdate();
                    var keys = ps.getGeneratedKeys();
                    keys.next();
                    saleId = keys.getLong(1);
                }

                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO sale_lines (sale_id, item_id, item_name, quantity, unit_price, line_total) VALUES (?, ?, ?, ?, ?, ?)")) {
                    for (CartLine line : cartLines) {
                        ps.setLong(1, saleId);
                        ps.setInt(2, line.getItem().getId());
                        ps.setString(3, line.getItem().getName());
                        ps.setInt(4, line.getQuantity());
                        ps.setDouble(5, line.getItem().getPrice());
                        ps.setDouble(6, line.getLineTotal());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }

                ItemDao itemDao = new ItemDao();
                for (CartLine line : cartLines) {
                    itemDao.decrementStock(line.getItem().getId(), line.getQuantity());
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }

            return new SaleResult(saleNumber, subtotal, gst, total);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to record sale: " + e.getMessage(), e);
        }
    }
}
