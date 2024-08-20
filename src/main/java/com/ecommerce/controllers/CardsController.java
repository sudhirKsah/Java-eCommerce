package com.ecommerce.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
// import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import com.ecommerce.DatabaseManager;
import com.ecommerce.models.Cart;
import com.ecommerce.models.CartItem;
import com.ecommerce.models.UserSession;

public class CardsController implements Initializable {

    @FXML
    private ImageView account_icon;

    @FXML
    private BorderPane borderPane;

    @FXML
    private TilePane cardContainer;

    @FXML
    private ImageView cart_icon;

    @FXML
    private Label home_btn;

    @FXML
    private Label login_btn;

    @FXML
    private Label signup_btn;

    @FXML
    private VBox homeBrowse;

    @FXML
    private ScrollPane centerScrollPane;

    private Parent customerMenu;
    private Parent sellerMenu;
    // private SignupModalController signupModalController;
    private String user = "";

    private Connection connection;

    private static CardsController instance;

    public CardsController() {
        instance = this;
    }

    public static CardsController getInstance() {
        return instance;
    }

    public void initialize(URL location, ResourceBundle resources) {
        try {
            connection = DatabaseManager.getConnection();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        loadData();
        signup_btn.setOnMouseClicked(event -> openSignupModal());
        login_btn.setOnMouseClicked(event -> openLoginModal());
        home_btn.setOnMouseClicked(event -> setHomePage());

        account_icon.setOnMouseClicked(event -> {
            if (user.matches("customer")) {
                showCustomerMenu();
            } else if (user.matches("seller")) {
                showSellerMenu();
            }
        });

        cart_icon.setOnMouseClicked(event -> showCustomerCart());
    }

    private void setHomePage() {
        borderPane.setLeft(homeBrowse);
        borderPane.setCenter(centerScrollPane);
        refreshUI();
    }

    public void refreshUI() {
        cardContainer.getChildren().clear();
        loadData();
    }

    public void setUserSignedUp(String returnedUser) {
        user = returnedUser;
        login_btn.setVisible(false);
        signup_btn.setVisible(false);
    }

    private void openSignupModal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Signup.fxml"));
            Parent root = loader.load();

            SignupModalController signupModalController = loader.getController();
            // signupModalController.setCardsController(this);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setTitle("CrazyShop");
            stage.setScene(new Scene(root));

            signupModalController.setStage(stage);
            stage.showAndWait();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void openLoginModal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();

            LoginModalController loginModalController = loader.getController();
            // loginModalController.setCardsController(this);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.setTitle("CrazyShop");

            loginModalController.setStage(stage);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // shows customer menu to left of borderpane
    private void showCustomerMenu() {
        System.out.println("Setting Customer Menu");
        try {
            if (customerMenu == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CustomerMenu.fxml"));
                customerMenu = loader.load();
                CustomerMenuController customerMenuController = loader.getController();
                customerMenuController.setBorderPane(borderPane);
            }
            borderPane.setLeft(customerMenu);
            System.out.println("Customer Menu Set");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading Customer Menu");
        }
        System.out.println("reached Customer Menu");
    }

    private void showSellerMenu() {
        try {
            if (sellerMenu == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SellerMenu.fxml"));
                sellerMenu = loader.load();
                SellerMenuController sellerMenuController = loader.getController();
                sellerMenuController.setBorderPane(borderPane);
            }
            borderPane.setLeft(sellerMenu);
        } catch (IOException e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    private void showCustomerCart() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CustomerCart.fxml"));
            Parent cartRoot = loader.load();
            borderPane.setCenter(cartRoot);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading Customer Cart.");
        }
    }

    private void loadData() {
        try {
            String query = "SELECT * FROM products";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int productId = resultSet.getInt("product_id");
                String name = resultSet.getString("name");
                String imagePath = resultSet.getString("image_url");
                double price = resultSet.getDouble("price");
                // double rating = resultSet.getDouble("rating");
                // accordingly
                String description = resultSet.getString("description");

                // demo rating
                double rating = 4.5;

                VBox card = createCard(productId, name, imagePath, price, rating, description);
                cardContainer.getChildren().add(card);
            }

            statement.close();
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private VBox createCard(int productId, String name, String imagePath, double price, double rating,
            String description) {

        Label productName = new Label(name);

        // Construct the absolute path to the image
        // String absoluteImagePath =
        // getClass().getResource(imagePath).toExternalForm();
        String absoluteImagePath = Paths.get(imagePath).toUri().toString();

        // Load the image
        Image image = new Image(absoluteImagePath);
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(200);
        imageView.setFitHeight(150);

        Label priceLabel = new Label("Price: $रू" + price);
        Label ratingLabel = new Label("Rating: " + rating);

        Button addToCartButton = new Button("Add to Cart");
        if (UserSession.getUserRole() == null) {
            addToCartButton.setVisible(false);
        } else {
            addToCartButton.setOnAction(event -> {
                addToCartButton.setVisible(true);
                addItemToCart(productId, name, price);
            });
        }

        VBox card = new VBox(10);
        card.setPadding(new Insets(10));
        card.getChildren().addAll(imageView, new VBox(productName, priceLabel, ratingLabel, addToCartButton));

        card.setOnMouseClicked(event -> displayProductDetails(productId, name, imagePath, price, description, rating));

        return card;
    }

    private void displayProductDetails(int productId, String name, String imagePath, double price, String description,
            double rating) {
        Label productName = new Label(name);

        // String absoluteImagePath =
        // getClass().getResource(imagePath).toExternalForm();
        String absoluteImagePath = Paths.get(imagePath).toUri().toString();
        System.out.println(absoluteImagePath);

        Image image = new Image(absoluteImagePath);
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(200);
        imageView.setFitHeight(150);

        Label priceLabel = new Label("Price: $" + price);
        Text descriptionText = new Text(description);
        Label ratingLabel = new Label("Rating: " + rating);

        Button addToCartButton = new Button("Add to Cart");
        if (UserSession.getUserRole() == null) {
            addToCartButton.setVisible(false);
        } else {
            addToCartButton.setOnAction(event -> {
                addToCartButton.setVisible(true);
                addItemToCart(productId, name, price);
            });
        }

        VBox detailsCard = new VBox(10);
        detailsCard.setPadding(new Insets(10));
        detailsCard.getChildren().addAll(productName, imageView, priceLabel, ratingLabel, addToCartButton,
                descriptionText);

        borderPane.setCenter(detailsCard);
    }

    private void addItemToCart(int productId, String name, double price) {
        int userId = UserSession.getUserId();
        int quantity = 1;
        CartItem cartItem = new CartItem(productId, userId, name, price, quantity);
        Cart.addItem(cartItem);
        refreshUI();
        System.out.println("Item added to cart");
    }

}
