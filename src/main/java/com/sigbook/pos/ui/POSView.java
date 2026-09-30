package com.sigbook.pos.ui;

import com.sigbook.pos.db.ItemDao;
import com.sigbook.pos.db.SaleDao;
import com.sigbook.pos.model.CartLine;
import com.sigbook.pos.model.Item;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class POSView {
    private final ItemDao itemDao = new ItemDao();
    private final SaleDao saleDao = new SaleDao();

    private final ObservableList<CartLine> cartLines = FXCollections.observableArrayList();
    private final ListView<CartLine> cartListView = new ListView<>(cartLines);
    private final Label totalLabel = new Label("$0.00");
    private final Label gstLabel = new Label("Includes GST: $0.00");
    private final FlowPane itemGrid = new FlowPane();

    private Stage stage;

    public Scene createScene(Stage stage) {
        this.stage = stage;
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(16));



        // Toolbar
        Button adminBtn = new Button ("Admin");
        adminBtn.setOnAction(e -> onAdminClicked());

        HBox toolbar = new HBox( 10, adminBtn);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setPadding(new Insets(0, 0, 12, 0));

        Label heading = new Label("Sig Bookshop POS");
        heading.setFont(Font.font("System", FontWeight.BOLD, 20));
        BorderPane.setMargin(heading, new Insets(0, 0, 12, 0));

        VBox topSection = new VBox(8, heading, toolbar);
        root.setTop(topSection);

        //item grid
        itemGrid.setHgap(12);
        itemGrid.setVgap(12);
        refreshItemGrid();

        ScrollPane itemScroll = new ScrollPane(itemGrid);
        itemScroll.setFitToWidth(true);
        itemScroll.setPrefWidth(560);

        root.setCenter(itemScroll);

        //Cart panel
        VBox cartPanel = buildCartPanel();
        cartPanel.setPrefWidth(620);
        BorderPane.setMargin(cartPanel, new Insets(0, 0, 0, 16));
        root.setRight(cartPanel);

        //Set maximised
        stage.setMaximized(true);
        return new Scene(root);
    }


    private void refreshItemGrid(){
        itemGrid.getChildren().clear();
        List<Item> items = itemDao.findAll();
        for (Item item : items) {
            itemGrid.getChildren().add(buildItemTile(item));
        }
    }
    private void onAdminClicked() {
        ItemAdminView itemAdminView = new ItemAdminView();
        stage.setScene(itemAdminView.createScene(stage));
    }

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
        tile.setDisable(item.getStock() <= 0);
        tile.setOnAction(e -> addToCart(item));
        return tile;
    }
    private VBox buildCartPanel() {
        Label cartHeading = new Label("Current Sale");
        cartHeading.setFont(Font.font("System", FontWeight.BOLD, 28));

        cartListView.setCellFactory(lv -> new CartLineCell());
        cartListView.setPrefHeight(360);

        HBox totalsBox = new HBox();
        totalsBox.setAlignment(Pos.CENTER_RIGHT);
        VBox totalsText = new VBox(2);
        Label totalTitle = new Label("Total");
        totalTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        totalLabel.setFont(Font.font("System", FontWeight.BOLD, 22));
        gstLabel.setFont(Font.font("System, 11"));
        gstLabel.setTextFill(Color.GRAY);
        totalsText.setAlignment(Pos.CENTER_RIGHT);
        totalsText.getChildren().addAll(totalTitle, totalLabel, gstLabel);
        totalsBox.getChildren().add(totalsText);

        Button voidSaleBtn = new Button("Void Entire Sale");
        voidSaleBtn.setMaxWidth(Double.MAX_VALUE);
        voidSaleBtn.setStyle("-fx-background-color: #e0554f; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 24px");
        voidSaleBtn.setOnAction(e -> voidSale());

        Button checkoutCashBtn = new Button("Checkout - Cash");
        checkoutCashBtn.setMaxWidth(Double.MAX_VALUE);
        checkoutCashBtn.setStyle("-fx-background-color: #2c5f2d; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 24px");
        checkoutCashBtn.setOnAction(e -> checkout("Cash"));

        Button checkoutEftposBtn = new Button("Checkout - EFTPOS");
        checkoutEftposBtn.setMaxWidth(Double.MAX_VALUE);
        checkoutEftposBtn.setStyle("-fx-background-color: #2c5f2d; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 24px");
        checkoutEftposBtn.setOnAction(e -> checkout("EFTPOS"));

        VBox panel = new VBox(10,
                cartHeading,
                cartListView,
                new Separator(),
                totalsBox,
                checkoutCashBtn,
                checkoutEftposBtn,
                voidSaleBtn);
        panel.setPadding(new Insets(12));
        panel.setStyle("-fx-background-color: #f7f6f0; -fx-background-radius: 10;");
        return panel;
    }

    private void addToCart(Item item) {
        for (CartLine line : cartLines) {
            if (line.getItem().getId() == item.getId()) {
                if (line.getQuantity() + 1 > item.getStock()) {
                    showAlert("Not enough stock for another " + item.getName() + ".");
                    return;
                }
                line.setQuantity(line.getQuantity() + 1);
                cartListView.refresh();
                updateTotals();
                return;
            }
        }
        cartLines.add(new CartLine(item, 1));
        updateTotals();
    }

    private void removeFromCart(CartLine line) {
        cartLines.remove(line);
        updateTotals();
    }


    private void updateTotals() {
        double total = cartLines.stream().mapToDouble(CartLine::getLineTotal).sum();
        double gst = total / 11.0;
        totalLabel.setText(String.format("$%.2f", total));
        gstLabel.setText(String.format("Includes GST: $%.2f", gst));
    }

    private void voidSale() {
        if (cartLines.isEmpty()) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Void the entire current sale? This cannot be undone.", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            cartLines.clear();
            updateTotals();
        }
    }

    private void checkout(String paymentMethod) {
        if (cartLines.isEmpty()) {
            showAlert("Add at least one item before checkout out.");
            return;
        }
        List<CartLine> linesCopy = new ArrayList<>(cartLines);
        SaleDao.SaleResult result = saleDao.recordSale(linesCopy, paymentMethod);

        Alert done = new Alert(Alert.AlertType.INFORMATION);
        done.setHeaderText(null);
        done.setTitle("Sale Complete");
        done.setContentText(String.format(
                "Receipt %s saved.%nTotal: $%.2f (%s)%n%nPrinting will be added in the next stage - " +
                        "for now, this confirms sale is recorded.",
                result.saleNumber(), result.total(), paymentMethod));
        done.showAndWait();

        cartLines.clear();
        updateTotals();
        refreshItemGrid();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private class CartLineCell extends ListCell<CartLine> {
        @Override
        protected void updateItem(CartLine line, boolean empty) {
            super.updateItem(line, empty);
            if (empty || line == null) {
                setGraphic(null);
                return;
            }

            Label name = new Label(line.getItem().getName());
            name.setFont(Font.font("System", FontWeight.BOLD, 16));
            name.setPrefWidth(130);
            name.setWrapText(true);

            Button minus = new Button("-");
            Button plus = new Button("+");
            Label qty = new Label(String.valueOf(line.getQuantity()));
            qty.setFont(Font.font(16));
            qty.setPrefWidth(20);
            qty.setAlignment(Pos.CENTER);

            minus.setOnAction(e -> {
                if (line.getQuantity() <= 1) {
                    removeFromCart(line);
                } else {
                    line.setQuantity(line.getQuantity() - 1);
                    getListView().refresh();
                    updateTotals();
                }
            });
            plus.setOnAction(e-> {
                if (line.getQuantity() + 1 > line.getItem().getStock()) {
                    showAlert("Not enough stock for another " + line.getItem().getName() + ".");
                    return;
                }
                line.setQuantity(line.getQuantity() + 1);
                getListView().refresh();
                updateTotals();
            });
            Label lineTotal = new Label(String.format("$%.2f", line.getLineTotal()));
            lineTotal.setFont(Font.font(16));
            lineTotal.setPrefWidth(60);
            lineTotal.setAlignment(Pos.CENTER_RIGHT);

            Button remove = new Button("x");
            remove.setStyle("-fx-text-fill: #e0554f; -fx-font-weight: bold;");
            remove.setOnAction(e -> removeFromCart(line));

            HBox row = new HBox(6, name, minus, qty, plus, lineTotal, remove);
            row.setAlignment(Pos.CENTER_LEFT);
            setGraphic(row);
        }
    }

}
