package com.sigbook.pos.model;

public class Item {
    private final int id;
    private final String name;
    private final String description;
    private final String icon;
    private final double price;
    private int stock;

    public Item(int id, String name, String description, String icon, double price, int stock) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.price = price;
        this.stock = stock;
    }
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getIcon() { return icon; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
}
