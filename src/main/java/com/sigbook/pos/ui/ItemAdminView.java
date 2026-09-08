package com.sigbook.pos.ui;

import com.sigbook.pos.db.ItemDao;
import com.sigbook.pos.model.Item;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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

        // TODO: build the TableView columns (name, description, price, stock)
        //       hint: TableColumn<Item, String> nameCol = new TableColumn<>("Name");
        //             nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        //       (PropertyValueFactory needs matching getX() methods on Item - you have those already)

        // TODO: build the form (VBox of labeled TextFields) for add/edit,
        //       plus Save / Delete / Clear buttons

        // TODO: wire table selection -> populate form fields for editing
        //       hint: table.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> { ... })
        root.setPadding(new Insets(16));

        Label heading = new Label("Sig Bookshop POS");
        heading.setFont(Font.font("System", FontWeight.BOLD, 20));
        BorderPane.setMargin(heading, new Insets(0, 0, 12, 0));
        root.setTop(heading);


        refreshTable();



        // TODO: assemble root layout (table on one side, form on the other)


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
