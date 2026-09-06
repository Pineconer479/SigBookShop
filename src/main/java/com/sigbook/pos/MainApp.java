package com.sigbook.pos;

import com.sigbook.pos.db.Database;
import com.sigbook.pos.ui.POSView;
import javafx.application.Application;
import javafx.stage.Stage;


public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        Database.get();

       POSView posView = new POSView();
        stage.setTitle("SigBookShop POS");
        stage.setScene(posView.createScene(stage));
        stage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
