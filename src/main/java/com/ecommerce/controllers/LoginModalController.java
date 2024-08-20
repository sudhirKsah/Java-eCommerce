package com.ecommerce.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
// import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
// import javafx.scene.layout.VBox;
// import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import com.ecommerce.DatabaseManager;
import com.ecommerce.models.UserSession;

public class LoginModalController implements Initializable {

    @FXML
    private TextField loginEmail_input;

    @FXML
    private PasswordField loginPassword_input;

    @FXML
    private Button login_btn;

    @FXML
    private Label errorLabel;

    private Stage stage;

    // private CardsController cardsController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        login_btn.setOnAction(event -> login());
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    // public void setCardsController(CardsController cardsController) {
    //     this.cardsController = cardsController;
    // }

    private void login() {
        String email = loginEmail_input.getText();
        String password = loginPassword_input.getText();
        String role = authenticateUser(email, password);

        if (role == null) {
            showError("Invalid email or password!");
            return;
        }

        try (Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement = connection
                        .prepareStatement("SELECT user_id FROM users WHERE email = ?")) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int userId = resultSet.getInt("user_id");
                UserSession.setUserId(userId);
                UserSession.setUserRole(role);

                stage.close();
                // cardsController.setUserSignedUp(role);
                CardsController.getInstance().setUserSignedUp(role);
                // CardsController.getInstance().refreshUI();
            } else {
                showError("Invalid email or password!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String authenticateUser(String email, String password) {
        String query = "SELECT role FROM users WHERE email = ? AND password = ?";
        try (Connection connection = DatabaseManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("role");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> {
            errorLabel.setVisible(false);
        }));
        timeline.play();
    }
}