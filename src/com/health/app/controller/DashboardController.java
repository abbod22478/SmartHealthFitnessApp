package com.health.app.controller;

import com.health.app.AppSession;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import com.health.app.model.User;
import com.health.app.service.NutritionService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.shape.Arc;

public class DashboardController {

    // Header
    @FXML private Label userNameLabel;
    @FXML private Label avatarLabel;

    // Calorie ring card
    @FXML private Arc caloriesArc;
    @FXML private Arc proteinArc;
    @FXML private Label ringPercentLabel;
    @FXML private Label caloriesValueLabel;
    @FXML private Label caloriesTargetLabel;
    @FXML private Label caloriesRemainingLabel;

    // Macro cards
    @FXML private Label proteinValueLabel;
    @FXML private Label carbsValueLabel;
    @FXML private Label fatsValueLabel;
    @FXML private Region proteinBarFill;
    @FXML private Region carbsBarFill;
    @FXML private Region fatsBarFill;

    private static final double MACRO_BAR_MAX_WIDTH = 100;

    private User currentUser;

    private double targetCalories = 0;
    private double targetProtein = 0;
    private double targetCarbs = 0;
    private double targetFats = 0;

    // Later, these will come from meal logs.
    // For now, they start at 0 because the user has not logged food yet.
    private double consumedCalories = 0;
    private double consumedProtein = 0;
    private double consumedCarbs = 0;
    private double consumedFats = 0;

    private final NutritionService nutritionService = new NutritionService();

    @FXML
    public void initialize() {
        showEmptyDashboard();
    }
    public void setUser(User user) {
        this.currentUser = user;
        AppSession.setCurrentUser(user);

        if (user != null) {
            NutritionService.NutritionTargets targets = nutritionService.calculateTargets(user);

            this.targetCalories = targets.getCalories();
            this.targetProtein = targets.getProtein();
            this.targetCarbs = targets.getCarbs();
            this.targetFats = targets.getFats();

            AppSession.setTargetCalories(targetCalories);
            AppSession.setTargetProtein(targetProtein);
            AppSession.setTargetCarbs(targetCarbs);
            AppSession.setTargetFats(targetFats);

            this.consumedCalories = 0;
            this.consumedProtein = 0;
            this.consumedCarbs = 0;
            this.consumedFats = 0;
        }

        refreshUI();
    }

    private void showEmptyDashboard() {
        userNameLabel.setText("Hi, User");
        avatarLabel.setText("U");

        caloriesValueLabel.setText("0");
        caloriesTargetLabel.setText("daily target");
        caloriesRemainingLabel.setText("Complete onboarding to calculate your plan");

        ringPercentLabel.setText("0%");
        caloriesArc.setLength(0);
        proteinArc.setLength(0);

        proteinValueLabel.setText("0");
        carbsValueLabel.setText("0");
        fatsValueLabel.setText("0");

        proteinBarFill.setPrefWidth(0);
        carbsBarFill.setPrefWidth(0);
        fatsBarFill.setPrefWidth(0);
    }

    private void refreshUI() {
        if (currentUser == null) {
            showEmptyDashboard();
            return;
        }

        String name = currentUser.getName();

        if (name == null || name.trim().isEmpty()) {
            userNameLabel.setText("Hi, User");
            avatarLabel.setText("U");
        } else {
            userNameLabel.setText("Hi, " + name.trim());
            avatarLabel.setText(name.substring(0, 1).toUpperCase());
        }

        // Main dashboard number = calculated daily calorie target
        caloriesValueLabel.setText(String.valueOf((int) targetCalories));
        caloriesTargetLabel.setText("daily target");

        caloriesRemainingLabel.setText(
                "Goal: " + safeText(currentUser.getFitnessGoal()) +
                        " • " + safeText(currentUser.getActivityLevel())
        );

        // Since meals are not logged yet, progress is 0%.
        int caloriePercent = calculatePercent(consumedCalories, targetCalories);
        int proteinPercent = calculatePercent(consumedProtein, targetProtein);

        ringPercentLabel.setText(caloriePercent + "%");

        caloriesArc.setLength(-360.0 * caloriePercent / 100.0);
        proteinArc.setLength(-360.0 * proteinPercent / 100.0);

        // Macro cards show target macro needs
        proteinValueLabel.setText(String.valueOf((int) targetProtein));
        carbsValueLabel.setText(String.valueOf((int) targetCarbs));
        fatsValueLabel.setText(String.valueOf((int) targetFats));

        // Fill bars fully because these are target values, not consumed values yet.
        proteinBarFill.setPrefWidth(MACRO_BAR_MAX_WIDTH);
        carbsBarFill.setPrefWidth(MACRO_BAR_MAX_WIDTH);
        fatsBarFill.setPrefWidth(MACRO_BAR_MAX_WIDTH);
    }

    private int calculatePercent(double consumed, double target) {
        if (target <= 0) {
            return 0;
        }

        double percent = (consumed / target) * 100.0;

        if (percent < 0) {
            return 0;
        }

        if (percent > 100) {
            return 100;
        }

        return (int) Math.round(percent);
    }

    private String safeText(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "Not set";
        }

        return value.trim();
    }

    // ---------- Quick Actions ----------


    @FXML
    private void openMeals() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/health/app/view/meals.fxml"));
            Parent mealsRoot = loader.load();

            MealController mealController = loader.getController();
            mealController.setTargets(
                    targetCalories,
                    targetProtein,
                    targetCarbs,
                    targetFats
            );

            Scene scene = userNameLabel.getScene();
            scene.setRoot(mealsRoot);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openWorkout() {
        System.out.println("Open Workout page");
    }

    @FXML
    private void openProgress() {
        System.out.println("Open Progress page");
    }

    @FXML
    private void openReminders() {
        System.out.println("Open Reminders page");
    }
}