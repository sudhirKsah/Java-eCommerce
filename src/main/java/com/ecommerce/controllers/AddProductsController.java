package com.ecommerce.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
// import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import com.ecommerce.DatabaseManager;
import com.ecommerce.models.UserSession;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.util.Duration;

public class AddProductsController implements Initializable {

    @FXML
    private Button addProductButton;

    @FXML
    private Tab addProductTab;

    @FXML
    private TabPane addProductTabPane;

    @FXML
    private Tab addedProductsTab;

    @FXML
    private Button chooseImage;

    @FXML
    private TextArea productDescription;

    @FXML
    private TextField productImage;

    @FXML
    private TextField productPrice;

    @FXML
    private TextField productQuantity;

    @FXML
    private TextField productTitle;

    @FXML
    private Label errorLabel;

    @FXML
    private ChoiceBox<String> category;

    @FXML
    private ChoiceBox<String> subCategory;

    private String[] categories = { "Electronics", "Clothing", "Furniture" };
    private Map<String, String[]> subCategoriesMap = new HashMap<>() {
        {
            put("Electronics", new String[] { "Laptops", "Mobile Phones", "Televisions" });
            put("Clothing", new String[] { "Men", "Women", "Kids" });
            put("Furniture", new String[] { "Tables", "Chairs" });
        }
    };

    private File selectedImageFile;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        category.getItems().addAll(categories);
        category.setOnAction(event -> updateSubCategories());
        addProductButton.setOnAction(event -> addProduct());
        chooseImage.setOnAction(event -> selectedImageFile = chooseImage());
    }

    private void updateSubCategories() {
        String selectedCategory = category.getValue();
        subCategory.getItems().clear();
        if (selectedCategory != null) {
            subCategory.getItems().addAll(subCategoriesMap.get(selectedCategory));
        }
    }

    private File chooseImage() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpeg",
                "*.jpg", "*.webp");
        fileChooser.getExtensionFilters().add(extensionFilter);
        fileChooser.setTitle("Select Image");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Pictures"));

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            productImage.setText(file.getName());
        }
        return file;
    }

    private void addProduct() {
        String title = productTitle.getText();
        String price = productPrice.getText();
        String quantity = productQuantity.getText();
        String image = productImage.getText();
        String description = productDescription.getText();
        String productCategory = category.getValue();
        String productSubCategory = subCategory.getValue();

        if (title.isBlank() || price.isBlank() || quantity.isBlank() || image.isBlank() || description.isBlank() || productCategory == null || productSubCategory == null) {
            showError("All fields are required!");
            return;
        }

        if (selectedImageFile == null) {
            showError("No image file has been selected!");
            return;
        }

        String imagePath = copyFileToImagesFolder(selectedImageFile);

        String categoryQuery = "SELECT category_id FROM categories WHERE name = ?";
        String subCategoryQuery = "SELECT subcategory_id FROM subcategories WHERE name = ? AND category_id = ?";
        String productQuery = "INSERT INTO products (name, description, price, image_url, seller_id, subcategory_id, store_id) VALUES(?,?,?,?,?,?,(SELECT store_id FROM stores WHERE seller_id = ?))";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement categoryStatement = connection.prepareStatement(categoryQuery);
             PreparedStatement subCategoryStatement = connection.prepareStatement(subCategoryQuery);
             PreparedStatement productStatement = connection.prepareStatement(productQuery)) {

            categoryStatement.setString(1, productCategory);
            ResultSet categoryResultSet = categoryStatement.executeQuery();
            int categoryId = -1;
            if (categoryResultSet.next()) {
                categoryId = categoryResultSet.getInt("category_id");
            } else {
                showError("Category not found!");
                return;
            }

            // Fetch subcategory ID
            subCategoryStatement.setString(1, productSubCategory);
            subCategoryStatement.setInt(2, categoryId);
            ResultSet subCategoryResultSet = subCategoryStatement.executeQuery();
            int subCategoryId = -1;
            if (subCategoryResultSet.next()) {
                subCategoryId = subCategoryResultSet.getInt("subcategory_id");
            } else {
                showError("Subcategory not found!");
                return;
            }

            int sellerId = UserSession.getUserId();
            productStatement.setString(1, title);
            productStatement.setString(2, description);
            productStatement.setString(3, price);
            productStatement.setString(4, imagePath);
            productStatement.setInt(5, sellerId);
            productStatement.setInt(6, subCategoryId);
            productStatement.setInt(7, sellerId);
            productStatement.executeUpdate();

            // clearing the textfields
            productTitle.setText("");
            productPrice.setText("");
            productQuantity.setText("");
            productImage.setText("");
            productDescription.setText("");
            category.setValue(null);
            subCategory.setValue(null);
            showError("Product added successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error adding product!");
        }
    }

    private String copyFileToImagesFolder(File file) {
        String projectDirectory = System.getProperty("user.dir");
        String imagesDirectory = projectDirectory + File.separator + "images";
        new File(imagesDirectory).mkdirs();
        File dest = new File(imagesDirectory + File.separator + file.getName());
        try {
            Files.copy(file.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return "images/" + file.getName();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> errorLabel.setVisible(false)));
        timeline.play();
    }
}