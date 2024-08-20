package com.ecommerce.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
// import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

// import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ecommerce.DatabaseManager;

import javafx.fxml.Initializable;

public class SignupModalController implements Initializable {

    @FXML
    private PasswordField confirmPassword_input;

    @FXML
    private TextField eMail_input;

    @FXML
    private Label errorLabel;

    @FXML
    private TextField firstName_input;

    @FXML
    private TextField lastName_input;

    @FXML
    private PasswordField password_input;

    @FXML
    private Button sellerSignupButton;

    @FXML
    private Button signup_btn;

    private Stage stage;
    // private CardsController cardsController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        eMail_input.setOnKeyReleased(event -> checkEmail());
        password_input.setOnKeyReleased(event -> checkPasswordStrength());
        signup_btn.setOnAction(event -> signup());
        sellerSignupButton.setOnMouseClicked(event -> openSellerSignup());
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    // public void setCardsController(CardsController cardsController) {
    //     this.cardsController = cardsController;
    // }

    private void signup() {
        String firstName = firstName_input.getText();
        String lastName = lastName_input.getText();
        String email = eMail_input.getText();
        String password = password_input.getText();
        String confirmPassword = confirmPassword_input.getText();

        if (!(!firstName.isBlank() && !lastName.isBlank() && !email.isBlank() && !password.isBlank()
                && !confirmPassword.isBlank())) {
            showError("All fields are required!");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Confirm password doesn't match with password!");
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

        if (insertUserIntoDatabase(firstName, lastName, email, password)) {
            stage.close();
            // cardsController.setUserSignedUp("customer");
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setContentText("Signup Successful! Please login");
            alert.showAndWait();
        } else {
            showError("Error signing up! Please try again.");
        }
    }

    private boolean isValidEmail(String email) {
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isValidPassword(String password) {
        Pattern pattern = Pattern.compile("^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[!@#$%&^*()+?/<>€\\|}{\\]\\[\\\\]).{8,}$");
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    private void checkPasswordStrength() {
        String password = password_input.getText();
        if (isValidPassword(password)) {
            // System.out.println("Password is valid.");
            errorLabel.setVisible(false); 
        } else {
            showError("Password must contain uppercase, lowercase, special character, and number.");
        }
    }

    private void checkEmail() {
        String email = eMail_input.getText();
        if (isValidEmail(email)) {
            // System.out.println("Email is valid.");
            errorLabel.setVisible(false); 
        } else {
            showError("Please enter a valid email!");
        }
    }

    private boolean insertUserIntoDatabase(String firstName, String lastName, String email, String password) {
        String sql = "INSERT INTO users (firstName, lastName, email, password, role) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, password);
            preparedStatement.setString(5, "customer");
            int rowsInserted = preparedStatement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> {
            errorLabel.setVisible(false);
        }));
        timeline.play();
    }

    private void openSellerSignup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SellerSignup.fxml"));
            Parent sellerRoot = loader.load();

            SellerSignupController sellerSignupController = loader.getController();

            Stage sellerStage = new Stage();
            sellerStage.initModality(Modality.APPLICATION_MODAL);
            sellerStage.setResizable(false);
            sellerStage.setScene(new Scene(sellerRoot));
            sellerStage.setTitle("CrazyShop");

            sellerSignupController.setStage(sellerStage);

            PauseTransition delay = new PauseTransition(Duration.seconds(0.5));
            delay.setOnFinished(event -> stage.close());
            delay.play();

            sellerStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}