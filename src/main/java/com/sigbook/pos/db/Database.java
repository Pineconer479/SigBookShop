package com.sigbook.pos.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Handles the single SQLite connection and makes sure the schema exists.
 * The database file (bookshop.db) lives next to the application, so it
 * carries over between runs and can be backed up like any normal file.
 */
public class Database {

    private static final String DB_FILE = "SigBookShop.db";
    private static Connection connection;

    public static Connection get() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection("jdbc:sqlite:" + DB_FILE);
                initSchema();
            } catch (SQLException e) {
                throw new RuntimeException("Could not open database: " + e.getMessage(), e);
            }
        }
        return connection;
    }

    private static void initSchema() throws SQLException {
        try (Statement st = connection.createStatement()) {
            st.execute("""
                CREATE TABLE IF NOT EXISTS items (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    description TEXT,
                    icon TEXT,
                    price REAL NOT NULL,
                    stock INTEGER NOT NULL DEFAULT 0
                )
            """);

            st.execute("""
                CREATE TABLE IF NOT EXISTS sales (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    sale_number TEXT UNIQUE NOT NULL,
                    sale_time TEXT NOT NULL DEFAULT (datetime('now', 'localtime')),
                    payment_method TEXT NOT NULL,
                    subtotal REAL NOT NULL,
                    gst REAL NOT NULL,
                    total REAL NOT NULL,
                    voided INTEGER NOT NULL DEFAULT 0
                )
            """);

            st.execute("""
                CREATE TABLE IF NOT EXISTS sale_lines (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    sale_id INTEGER NOT NULL,
                    item_id INTEGER NOT NULL,
                    item_name TEXT NOT NULL,
                    quantity INTEGER NOT NULL,
                    unit_price REAL NOT NULL,
                    line_total REAL NOT NULL,
                    FOREIGN KEY (sale_id) REFERENCES sales(id),
                    FOREIGN KEY (item_id) REFERENCES items(id)
                )
            """);

            st.execute("""
                CREATE TABLE IF NOT EXISTS counters (
                    name TEXT PRIMARY KEY,
                    value INTEGER NOT NULL
                )
            """);
            st.execute("INSERT OR IGNORE INTO counters (name, value) VALUES ('sale', 0)");

            // Seed a handful of sample items only if the catalog is empty,
            // so first run isn't a blank screen. Nancy/BSM can edit these later.
            try (var rs = st.executeQuery("SELECT COUNT(*) AS c FROM items")) {
                rs.next();
                if (rs.getInt("c") == 0) {
                    st.execute("""
                        INSERT INTO items (name, description, icon, price, stock) VALUES
                        ('The Hobbit', 'Paperback novel', 'BOOK', 16.99, 12),
                        ('Bookmark - Wood', 'Handmade wooden bookmark', 'MARK', 4.50, 40),
                        ('Notebook A5', 'Lined, 120 pages', 'NOTE', 8.00, 25),
                        ('Gift Card $20', 'Store gift card', 'GIFT', 20.00, 999),
                        ('Tote Bag', 'Canvas shop tote', 'BAG', 12.00, 15),
                        ('Pencil Set', 'Set of 6 HB pencils', 'PEN', 5.50, 30)
                    """);
                }
            }
        }
    }

    /** Generates the next sequential sale number, e.g. BS-2026-0001 */
    public static String nextSaleNumber() throws SQLException {
        Connection conn = get();
        try (Statement st = conn.createStatement()) {
            st.executeUpdate("UPDATE counters SET value = value + 1 WHERE name = 'sale'");
        }
        int value;
        try (Statement st = conn.createStatement();
             var rs = st.executeQuery("SELECT value FROM counters WHERE name = 'sale'")) {
            rs.next();
            value = rs.getInt("value");
        }
        int year = java.time.Year.now().getValue();
        return String.format("BS-%d-%04d", year, value);
    }
}
