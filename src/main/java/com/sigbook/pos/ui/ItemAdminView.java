package com.sigbook.pos.ui;

import com.sigbook.pos.db.ItemDao;
import com.sigbook.pos.model.Item;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.List;


public class ItemAdminView {

    private final ItemDao itemDao = new ItemDao();
    private final ObservableList<Item> items = FXCollections.observableArrayList();
    private final TableView<Item> table = new TableView<>(items);

    // Form fields for add/edit - reused for both actions
    private final TextField nameField = new TextField();
    private final TextField descriptionField = new TextField();
    private final TextField iconField = new TextField();
    private final TextField priceField = new TextField();
    private final TextField stockField = new TextField();
    private final FlowPane itemGrid = new FlowPane();


    private Item selectedItem; // null = "add new" mode, non-null = "editing this item"
    private Stage stage;

    public Scene createScene(Stage stage) {
        this.stage = stage;
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(16));

        //Toolbar
        Button addBtn = new Button ("Add Item");
        addBtn.setFont(Font.font("System", 30));



        Button backBtn = new Button("Return");
        backBtn.setFont(Font.font("System", 30));
        backBtn.setOnAction(e -> onBackClicked());

        //Empty region

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);


        HBox toolbar = new HBox(10, backBtn, addBtn);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setPadding(new Insets(0, 0, 12, 0));

        Label heading = new Label("Admin Menu");
        heading.setFont(Font.font("System", FontWeight.BOLD, 30));
        BorderPane.setMargin(heading, new Insets(0, 0, 12, 0));

        HBox topSection = new HBox(1300, heading, toolbar);
        root.setTop(topSection);

        root.setPadding(new Insets(16));

        VBox detailsPanel = buildDetailsPanel();
        detailsPanel.setPrefWidth(620);
        BorderPane.setMargin(detailsPanel, new Insets(0, 0, 0, 16));
        root.setRight(detailsPanel);

        refreshTable();
        //item grid
        itemGrid.setHgap(12);
        itemGrid.setVgap(12);
        ScrollPane itemScroll = new ScrollPane(itemGrid);
        itemScroll.setFitToWidth(true);
        itemScroll.setPrefWidth(560);

        root.setCenter(itemScroll);
        refreshItemGrid();

        //Set maximised
        javafx.stage.Screen screen = javafx.stage.Screen.getPrimary();
        javafx.geometry.Rectangle2D bounds = screen.getVisualBounds();
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());
        return new Scene(root);
    }

    private void refreshTable() {
        items.setAll(itemDao.findAll());
    }
    private void onBackClicked() {
        POSView posView = new POSView();
        stage.setScene(posView.createScene(stage));
    }
    private void refreshItemGrid(){
        itemGrid.getChildren().clear();
        List<Item> items = itemDao.findAll();
        for (Item item : items) {
            itemGrid.getChildren().add(buildItemTile(item));
        }
    }
    // Item tile
    private Button buildItemTile(Item item) {
        Label iconLabel = new Label(item.getIcon());
        iconLabel.setFont(Font.font("System", FontWeight.BOLD, 25));

        Label nameLabel = new Label(item.getName());
        nameLabel.setWrapText(true);
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 20));

        Label descLabel = new Label(item.getDescription());
        descLabel.setWrapText(true);
        descLabel.setFont(Font.font("System",FontWeight.BOLD, 18));
        descLabel.setTextFill(Color.GRAY);

        Label priceLabel = new Label(String.format("$%.2f", item.getPrice()));
        priceLabel.setFont(Font.font("System", FontWeight.BOLD, 24));

        Label stockLabel = new Label(item.getStock() <= 0 ? "Out of stock" : "Stock: " + item.getStock());
        stockLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        stockLabel.setTextFill(item.getStock() <= 0 ? Color.CRIMSON : Color.DARKGREEN);

        VBox content = new VBox(4, iconLabel, nameLabel, descLabel, priceLabel, stockLabel);
        content.setAlignment(Pos.TOP_LEFT);

        Button tile = new Button();
        tile.setGraphic(content);
        tile.setPrefSize(240, 240);
        tile.setWrapText(true);
        tile.setOnAction(e -> selectItem(item));
        return tile;
    }

    // Details panel
    private VBox buildDetailsPanel(){
        Label panelHeading = new Label("Details");
        panelHeading.setFont(Font.font("System", FontWeight.BOLD, 28));
        Label nameLabel = new Label("Name");
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 25));
        Label descriptionLabel = new Label("Description");
        descriptionLabel.setFont(Font.font("System", FontWeight.BOLD, 25));
        Label iconLabel = new Label("Icon");
        iconLabel.setFont(Font.font("System", FontWeight.BOLD, 25));
        Label priceLabel = new Label("Price");
        priceLabel.setFont(Font.font("System", FontWeight.BOLD, 25));
        Label stockLabel = new Label("Stock");
        stockLabel.setFont(Font.font("System", FontWeight.BOLD, 25));
        nameField.setFont(Font.font("System", 25));
        descriptionField.setFont(Font.font("System", 25));
        iconField.setFont(Font.font("System", 25));
        priceField.setFont(Font.font("System", 25));
        stockField.setFont(Font.font("System", 25));
        VBox form = new VBox(10,nameLabel, nameField, descriptionLabel, descriptionField, iconLabel, iconField, priceLabel, priceField, stockLabel, stockField);

        Button saveBtn = new Button("Save");
        saveBtn.setFont(Font.font("System", 30));
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        saveBtn.setOnAction(e -> onSaveClicked());

        Button deleteBtn = new Button("Delete");
        deleteBtn.setFont(Font.font("System", 30));
        deleteBtn.setMaxWidth(Double.MAX_VALUE);
        deleteBtn.setOnAction(e -> onDeleteClicked());

        VBox panel = new VBox(10, panelHeading, form, saveBtn, deleteBtn);
        panel.setPadding(new Insets(12));
        panel.setStyle("-fx-background-color: #f7f6f0; -fx-background-radius: 10;");
        return panel;

    }

    private void onSaveClicked() {
        // TODO: read values out of the form fields
        // TODO: validate them (non-empty name, price/stock parse as numbers, etc.)
        // TODO: if selectedItem == null -> call itemDao.insert(...)
        //       else -> call itemDao.update(selectedItem.getId(), ...)
        // TODO: refreshTable() and clearForm() afterwards


    }

    private void onDeleteClicked() {
        // TODO: confirm with an Alert (don't delete without asking)
        // TODO: call itemDao.delete(selectedItem.getId())
        // TODO: refreshTable() and clearForm() afterwards
        //
        // Worth thinking about: should deleting an item that appears in past
        // sale_lines actually be allowed? (sale_lines stores item_name as its
        // own copy already, precisely so old receipts don't break if you do)
    }
    private void selectItem(Item item) {
        selectedItem = item;
        nameField.setText(item.getName());
        descriptionField.setText(item.getDescription());
        iconField.setText(item.getIcon());
        priceField.setText(String.valueOf(item.getPrice()));
        stockField.setText(String.valueOf(item.getStock()));
    }

    private void clearForm() {
        selectedItem = null;
        // TODO: clear all text fields
    }
}
