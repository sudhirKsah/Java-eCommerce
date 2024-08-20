package com.ecommerce;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        showHomeScene();
    }

    public static void showHomeScene() throws IOException {
        Parent root = FXMLLoader.load(App.class.getResource("/fxml/Home.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("CrazyShop");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}