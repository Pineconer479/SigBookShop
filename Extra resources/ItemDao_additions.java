// Add these method signatures to your existing ItemDao class.
// Bodies are left as TODO - use insertSampleData in Database.java and
// decrementStock/setStock in ItemDao as reference examples for the
// PreparedStatement pattern.

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
