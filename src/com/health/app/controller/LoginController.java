package com.health.app.controller;

import com.health.app.model.User;
import com.health.app.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;
    @FXML private Label signUpLink;

    private final UserService userService = new UserService();

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty()) {
            showMessage("Please enter your email.", true);
            return;
        }

        if (password.isEmpty()) {
            showMessage("Please enter your password.", true);
            return;
        }

        User user = userService.login(email, password);

        if (user != null) {
            showMessage("Welcome back, " + user.getName() + "!", false);

            // Navigate to dashboard
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/health/app/view/dashboard.fxml")
                );
                Parent dashboardRoot = loader.load();

                DashboardController dashCtrl = loader.getController();
                dashCtrl.setUser(user);

                emailField.getScene().setRoot(dashboardRoot);

            } catch (Exception e) {
                e.printStackTrace();
                showMessage("Login successful but dashboard failed to load.", true);
            }
        } else {
            showMessage("Invalid email or password.", true);
        }
    }

    @FXML
    private void goToOnboarding() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/health/app/view/onboarding.fxml")
            );
            Parent onboardingRoot = loader.load();

            emailField.getScene().setRoot(onboardingRoot);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showMessage(String text, boolean isError) {
        messageLabel.setText(text);
        messageLabel.setStyle(isError
                ? "-fx-text-fill: #FF6B6B; -fx-font-size: 13px; -fx-font-weight: bold;"
                : "-fx-text-fill: #43D18D; -fx-font-size: 13px; -fx-font-weight: bold;");
    }
}