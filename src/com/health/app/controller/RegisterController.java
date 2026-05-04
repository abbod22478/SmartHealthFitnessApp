package com.health.app.controller;

import com.health.app.model.User;
import com.health.app.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

public class RegisterController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField ageField;
    @FXML private ComboBox<String> genderBox;
    @FXML private ComboBox<String> goalBox;
    @FXML private TextField heightField;
    @FXML private TextField weightField;
    @FXML private Label messageLabel;

    private final UserService userService = new UserService();

    @FXML
    public void initialize() {
        genderBox.getItems().addAll("Male", "Female");
        goalBox.getItems().addAll("Weight Loss", "Muscle Gain", "Maintain Weight", "Improve Fitness");
    }

    @FXML
    private void handleRegister() {
        try {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String password = passwordField.getText().trim();
            int age = Integer.parseInt(ageField.getText().trim());
            String gender = genderBox.getValue();
            String goal = goalBox.getValue();
            double height = Double.parseDouble(heightField.getText().trim());
            double weight = Double.parseDouble(weightField.getText().trim());

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || gender == null || goal == null) {
                showError("Please fill in all fields.");
                return;
            }

            User user = new User(
                    0,
                    name,
                    age,
                    gender,
                    height,
                    weight,
                    email,
                    goal
            );

            boolean created = userService.createAccount(user);

            if (created) {
                showSuccess("Account created successfully.");
                clearFields();
            } else {
                showError("Failed to create account.");
            }

        } catch (NumberFormatException e) {
            showError("Age, height, and weight must be valid numbers.");
        } catch (Exception e) {
            showError("Something went wrong.");
            e.printStackTrace();
        }
    }

    private void showSuccess(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: #2E7D32; -fx-font-size: 13px;");
    }

    private void showError(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: #C62828; -fx-font-size: 13px;");
    }

    private void clearFields() {
        nameField.clear();
        emailField.clear();
        passwordField.clear();
        ageField.clear();
        heightField.clear();
        weightField.clear();
        genderBox.setValue(null);
        goalBox.setValue(null);
    }
}