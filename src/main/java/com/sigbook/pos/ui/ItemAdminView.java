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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;



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

    private Item selectedItem; // null = "add new" mode, non-null = "editing this item"

    public Scene createScene(Stage stage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(16));

        //Toolbar
        Button addBtn = new Button ("Add Item");
        Button deleteBtn = new Button("Delete Selected");
        Button backBtn = new Button("Back to POS");

        //Empty region

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox toolbar = new HBox(10, addBtn, deleteBtn, spacer, backBtn);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setPadding(new Insets(0, 0, 12, 0));

        Label heading = new Label("Admin Menu");
        heading.setFont(Font.font("System", FontWeight.BOLD, 20));
        BorderPane.setMargin(heading, new Insets(0, 0, 12, 0));

        VBox topSection = new VBox(8, heading, toolbar);
        root.setTop(topSection);

        root.setPadding(new Insets(16));






        refreshTable();

        //Set maximised
        stage.setMaximized(true);
        return new Scene(root);
    }

    private void refreshTable() {
        items.setAll(itemDao.findAll());
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

    private void clearForm() {
        selectedItem = null;
        // TODO: clear all text fields
    }
}
