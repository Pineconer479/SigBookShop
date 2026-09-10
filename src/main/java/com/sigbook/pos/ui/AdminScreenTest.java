package com.sigbook.pos.ui;

import com.sigbook.pos.db.Database;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * SCRATCH FILE - launches straight into ItemAdminView for quick testing.
 * Delete this once the admin screen is finished and wired into MainApp properly.
 */
public class AdminScreenTest extends Application {

    @Override
    public void start(Stage stage) {
        Database.get(); // schema + sample data

        ItemAdminView adminView = new ItemAdminView();
        stage.setTitle("Admin Screen Test");
        stage.setScene(adminView.createScene(stage));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}