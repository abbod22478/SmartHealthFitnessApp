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

public class DashboardController {

    // Header
    @FXML private Label userNameLabel;
    @FXML private Label avatarLabel;

    // Calorie ring card
    @FXML private Label ringPercentLabel;
    @FXML private Label caloriesValueLabel;
    @FXML private Label caloriesTargetLabel;
    @FXML private Label caloriesRemainingLabel;

    // Macro value labels
    @FXML private Label proteinValueLabel;
    @FXML private Label carbsValueLabel;
    @FXML private Label fatsValueLabel;

    // Macro target labels (consumed / target)
    @FXML private Label proteinTargetLabel;
    @FXML private Label carbsTargetLabel;
    @FXML private Label fatsTargetLabel;

    // Macro bar fills
    @FXML private Region proteinBarFill;
    @FXML private Region carbsBarFill;
    @FXML private Region fatsBarFill;

    private static final double MACRO_BAR_MAX_WIDTH = 100;

    private User currentUser;

    private double targetCalories = 0;
    private double targetProtein  = 0;
    private double targetCarbs    = 0;
    private double targetFats     = 0;

    private double consumedCalories = 0;
    private double consumedProtein  = 0;
    private double consumedCarbs    = 0;
    private double consumedFats     = 0;

    private final NutritionService nutritionService = new NutritionService();

    @FXML
    public void initialize() {
        showEmptyDashboard();
    }

    public void setUser(User user) {
        this.currentUser = user;
        AppSession.setCurrentUser(user);

        if (user != null) {
            // Calculate nutrition targets
            NutritionService.NutritionTargets targets = nutritionService.calculateTargets(user);
            this.targetCalories = targets.getCalories();
            this.targetProtein  = targets.getProtein();
            this.targetCarbs    = targets.getCarbs();
            this.targetFats     = targets.getFats();

            AppSession.setTargetCalories(targetCalories);
            AppSession.setTargetProtein(targetProtein);
            AppSession.setTargetCarbs(targetCarbs);
            AppSession.setTargetFats(targetFats);

            // Load today's consumed totals from DB
            com.health.app.dao.MealDAO mealDAO = new com.health.app.dao.MealDAO();
            double[] totals = mealDAO.getTodayTotals(user.getUserId());
            this.consumedCalories = totals[0];
            this.consumedProtein  = totals[1];
            this.consumedCarbs    = totals[2];
            this.consumedFats     = totals[3];
        }

        refreshUI();
    }

    private void showEmptyDashboard() {
        userNameLabel.setText("Hi, User");
        avatarLabel.setText("U");

        caloriesValueLabel.setText("0");
        caloriesTargetLabel.setText("/ 0 kcal");
        caloriesRemainingLabel.setText("Complete onboarding to calculate your plan");
        ringPercentLabel.setText("0%");

        proteinValueLabel.setText("0");
        carbsValueLabel.setText("0");
        fatsValueLabel.setText("0");

        proteinTargetLabel.setText("/ 0g");
        carbsTargetLabel.setText("/ 0g");
        fatsTargetLabel.setText("/ 0g");

        proteinBarFill.setPrefWidth(0);
        carbsBarFill.setPrefWidth(0);
        fatsBarFill.setPrefWidth(0);
    }

    private void refreshUI() {
        if (currentUser == null) {
            showEmptyDashboard();
            return;
        }

        // Header
        String name = currentUser.getName();
        if (name == null || name.trim().isEmpty()) {
            userNameLabel.setText("Hi, User");
            avatarLabel.setText("U");
        } else {
            userNameLabel.setText("Hi, " + name.trim());
            avatarLabel.setText(name.substring(0, 1).toUpperCase());
        }

        // Calorie ring
        caloriesValueLabel.setText(String.valueOf((int) consumedCalories));
        caloriesTargetLabel.setText("/ " + (int) targetCalories + " kcal");

        double remaining = Math.max(0, targetCalories - consumedCalories);
        caloriesRemainingLabel.setText(
                (int) remaining + " kcal remaining • " + safeText(currentUser.getFitnessGoal())
        );

        int caloriePercent = calculatePercent(consumedCalories, targetCalories);
        ringPercentLabel.setText(caloriePercent + "%");

        // Macro values — consumed
        proteinValueLabel.setText(String.valueOf((int) consumedProtein));
        carbsValueLabel.setText(String.valueOf((int) consumedCarbs));
        fatsValueLabel.setText(String.valueOf((int) consumedFats));

        // Macro targets — "/ Xg"
        proteinTargetLabel.setText("/ " + (int) targetProtein + "g");
        carbsTargetLabel.setText("/ " + (int) targetCarbs + "g");
        fatsTargetLabel.setText("/ " + (int) targetFats + "g");

        // Macro bars — proportional fill
        proteinBarFill.setPrefWidth(calculatePercent(consumedProtein, targetProtein) * MACRO_BAR_MAX_WIDTH / 100.0);
        carbsBarFill.setPrefWidth(calculatePercent(consumedCarbs,    targetCarbs)    * MACRO_BAR_MAX_WIDTH / 100.0);
        fatsBarFill.setPrefWidth(calculatePercent(consumedFats,      targetFats)     * MACRO_BAR_MAX_WIDTH / 100.0);
    }

    private int calculatePercent(double consumed, double target) {
        if (target <= 0) return 0;
        double percent = (consumed / target) * 100.0;
        if (percent < 0)   return 0;
        if (percent > 100) return 100;
        return (int) Math.round(percent);
    }

    private String safeText(String value) {
        if (value == null || value.trim().isEmpty()) return "Not set";
        return value.trim();
    }

    // =========================================================================
    //  NAVIGATION
    // =========================================================================

    @FXML
    private void openMeals() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/health/app/view/meals.fxml"));
            Parent mealsRoot = loader.load();
            userNameLabel.getScene().setRoot(mealsRoot);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openWorkout() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/health/app/view/workouts.fxml"));
            Parent root = loader.load();
            userNameLabel.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openProgress() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/health/app/view/progress.fxml"));
            Parent root = loader.load();
            userNameLabel.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Progress page not built yet.");
        }
    }

    @FXML
    private void openReminders() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/health/app/view/reminders.fxml"));
            Parent root = loader.load();
            userNameLabel.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Reminders page not built yet.");
        }
    }
}