package com.ecommerce.controllers;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ecommerce.DatabaseManager;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class SellerSignupController implements Initializable {

    @FXML
    private PasswordField confirmPasswordInput;

    @FXML
    private TextField emailInput;

    @FXML
    private TextField firstNameInput;

    @FXML
    private TextField lastNameInput;

    @FXML
    private PasswordField passwordInput;

    @FXML
    private TextField sellerAddressInput;

    @FXML
    private TextField sellerPhoneNumberInput;

    @FXML
    private Button signupButton;

    @FXML
    private TextField storeAddressInput;

    @FXML
    private TextArea storeDescription;

    @FXML
    private TextField storeNameInput;

    @FXML
    private TextField storePhoneNumberInput;

    @FXML
    private Label errorLabel;

    private Stage stage;

    // private CardsController cardsController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        emailInput.setOnKeyReleased(event -> checkEmail());
        sellerPhoneNumberInput.setOnKeyReleased(event -> checkPhone(sellerPhoneNumberInput.getText()));
        storePhoneNumberInput.setOnKeyReleased(event -> checkPhone(storePhoneNumberInput.getText()));
        passwordInput.setOnKeyReleased(event -> checkPasswordStrength());
        signupButton.setOnAction(event -> signup());
    }

    public void setStage(Stage sellerStage) {
        this.stage = sellerStage;
    }

    private void signup() {
        String firstName = firstNameInput.getText();
        String lastName = lastNameInput.getText();
        String email = emailInput.getText();
        String phoneNumber = sellerPhoneNumberInput.getText();
        String password = passwordInput.getText();
        String confirmPassword = confirmPasswordInput.getText();
        String sellerAddress = sellerAddressInput.getText();
        String storeName = storeNameInput.getText();
        String storeAddress = storeAddressInput.getText();
        String storePhoneNumber = storePhoneNumberInput.getText();
        String store_Description = storeDescription.getText();

        if (!(!firstName.isBlank() && !lastName.isBlank() && !email.isBlank() && !phoneNumber.isBlank()
                && !password.isBlank() && !confirmPassword.isBlank() && !storeName.isBlank() && !storeAddress.isBlank()
                && !storePhoneNumber.isBlank())) {
            showError("Please, fill all required fields!");
            return;
        }

        if (!isValidPhone(phoneNumber, storePhoneNumber)) {
            showError("Please enter a valid phone number!");
            return;
        }

        if (!isValidEmail(email)) {
            showError("Please enter a valid email!");
            return;
        }

        if (!isValidPassword(password)) {
            showError("Password must contain uppercase, lowercase, special character, and number.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Confirm password doesn't match with password!");
            return;
        }

        if (insertIntoDatabase(firstName, lastName, email, phoneNumber, password, sellerAddress, storeName,
                storeAddress, storePhoneNumber, store_Description)) {
            stage.close();
            // cardsController.setUserSignedUp("seller");
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setContentText("Signup Successful! Please login");
            alert.showAndWait();
        } else {
            showError("Error signing up! Please try again.");
        }
    }

    private boolean isValidPhone(String phoneNumber, String storePhoneNumber) {
        Pattern pattern = Pattern.compile("^[0-9]{10}$");
        Matcher matcher = pattern.matcher(phoneNumber);
        Matcher matcher2 = pattern.matcher(storePhoneNumber);
        return matcher.matches() && matcher2.matches();
    }

    private boolean isValidEmail(String email) {
        Pattern pattern = Pattern
                .compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isValidPassword(String password) {
        Pattern pattern = Pattern
                .compile("^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[!@#$%&^*()+?/<>€\\|}{\\]\\[\\\\]).{8,}$");
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    private void checkPasswordStrength() {
        String password = passwordInput.getText();
        if (isValidPassword(password)) {
            // System.out.println("Password is valid.");
            errorLabel.setVisible(false);
        } else {
            showError("Password must contain uppercase, lowercase, special character, and number.");
        }
    }

    private void checkEmail() {
        String email = emailInput.getText();
        if (isValidEmail(email)) {
            // System.out.println("Email is valid.");
            errorLabel.setVisible(false);
        } else {
            showError("Please enter a valid email!");
        }
    }

    private void checkPhone(String phoneNumber) {
        if (isValidPhone(phoneNumber, phoneNumber)) {
            errorLabel.setVisible(false);
        } else {
            showError("Please enter a valid 10 digit phone number!");
        }
    }

    private boolean insertIntoDatabase(String firstName, String lastName, String email, String phoneNumber,
            String password, String sellerAddress, String storeName, String storeAddress, String storePhoneNumber,
            String store_Description) {
        String userSql = "INSERT INTO users(firstName, lastName, email, password, role) VALUES(?, ?, ?, ?, ?)";
        String storeSql = "INSERT INTO stores(seller_id, seller_phone, seller_address, store_name, store_description, store_phone, store_address) VALUES(?,?,?,?,?,?,?)";

        try (Connection connection = DatabaseManager.getConnection();
                PreparedStatement userStatement = connection.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS);
                PreparedStatement storeStatement = connection.prepareStatement(storeSql)) {
            userStatement.setString(1, firstName);
            userStatement.setString(2, lastName);
            userStatement.setString(3, email);
            userStatement.setString(4, password);
            userStatement.setString(5, "seller");
            int rowsInserted1 = userStatement.executeUpdate();

            ResultSet resultSet = userStatement.getGeneratedKeys();
            if (resultSet.next()) {
                int userId = resultSet.getInt(1);

                storeStatement.setInt(1, userId);
                storeStatement.setString(2, phoneNumber);
                storeStatement.setString(3, sellerAddress);
                storeStatement.setString(4, storeName);
                storeStatement.setString(5, store_Description);
                storeStatement.setString(6, storePhoneNumber);
                storeStatement.setString(7, storeAddress);
                int rowsInserted2 = storeStatement.executeUpdate();

                return (rowsInserted1 > 0 && rowsInserted2 > 0);
            }

        } catch (SQLException e) {
            // TODO: handle exception
            e.printStackTrace();
            return false;
        }
        return false;
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);

        Timeline timeline = new Timeline(new KeyFrame(javafx.util.Duration.seconds(5), event -> {
            errorLabel.setVisible(false);
        }));
        timeline.play();
    }
}
