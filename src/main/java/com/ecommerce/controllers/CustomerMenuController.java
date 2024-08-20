package com.ecommerce.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.ecommerce.App;
import com.ecommerce.models.UserSession;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class CustomerMenuController implements Initializable{

    @FXML
    private Button cartButton;

    @FXML
    private Button customerDetailsButton;

    @FXML
    private VBox customerMenuVbox;

    @FXML
    private Button logoutButton;

    @FXML
    private Button orderHistoryButton;

    @FXML
    private Button shippingAddressButton;

    private BorderPane mainBorderPane;

    @Override
    public void initialize(URL location, ResourceBundle resources){
        customerDetailsButton.setOnAction(event -> showCustomerProfile());
        cartButton.setOnAction(event -> showCustomerCart());
        logoutButton.setOnAction(event -> handleLogout());
    }

    public void setBorderPane(BorderPane borderPane){
        this.mainBorderPane = borderPane;
    }

    private void showCustomerProfile(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CustomerProfile.fxml"));
            mainBorderPane.setCenter(loader.load());
            mainBorderPane.setRight(null);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void showCustomerCart(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CustomerCart.fxml"));
            mainBorderPane.setCenter(loader.load());
            mainBorderPane.setRight(null);
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

