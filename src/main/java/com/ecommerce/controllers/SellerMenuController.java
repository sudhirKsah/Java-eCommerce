package com.ecommerce.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.ecommerce.App;
import com.ecommerce.models.UserSession;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
// import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
// import javafx.stage.Stage;

public class SellerMenuController implements Initializable{

    @FXML
    private Button logoutButton;

    @FXML
    private Button sellerDashboardButton;

    @FXML
    private Button sellerDetailsButton;

    @FXML
    private VBox sellerMenuVbox;

    @FXML
    private Button sellerProductsButton;

    private BorderPane maiBorderPane;

    @Override
    public void initialize(URL location, ResourceBundle resources){
        sellerDetailsButton.setOnAction(event -> showSellerDetail());
        sellerProductsButton.setOnAction(event -> showAddProducts());
        logoutButton.setOnAction(event -> handleLogout());
    }

    public void setBorderPane(BorderPane borderPane){
        this.maiBorderPane = borderPane;
    }

    private void showSellerDetail(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SellerProfile.fxml"));
            Parent root = loader.load();
            maiBorderPane.setCenter(root);
            maiBorderPane.setRight(null);
        } catch (IOException e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    private void showAddProducts(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddProducts.fxml"));
            Parent root = loader.load();
            maiBorderPane.setCenter(root);
            maiBorderPane.setRight(null);
        } catch (IOException e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    private void handleLogout(){
        try {
            App.showHomeScene();
            UserSession.setUserId(-1);
            UserSession.setUserRole(null);
            CardsController.getInstance().refreshUI();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}

