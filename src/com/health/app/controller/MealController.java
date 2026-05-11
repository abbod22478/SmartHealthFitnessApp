package com.health.app.controller;

import com.health.app.AppSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;

public class MealController {
    @FXML private Label rec1Label;
    @FXML private Label rec2Label;
    @FXML private Label rec3Label;
    @FXML private Label rec4Label;
    @FXML private Label breakfastCalLabel;
    @FXML private Label lunchCalLabel;
    @FXML private Label dinnerCalLabel;
    @FXML private Label snackCalLabel;

    @FXML private Label breakfastSubLabel;
    @FXML private Label lunchSubLabel;
    @FXML private Label dinnerSubLabel;
    @FXML private Label snackSubLabel;

    @FXML private Label loggedCaloriesLabel;
    @FXML private Label targetCaloriesLabel;
    @FXML private Label remainingCaloriesLabel;
    @FXML private Label mealPercentLabel;

    @FXML private Label loggedProteinLabel;
    @FXML private Label targetProteinLabel;
    @FXML private Label loggedCarbsLabel;
    @FXML private Label targetCarbsLabel;
    @FXML private Label loggedFatsLabel;
    @FXML private Label targetFatsLabel;

    @FXML private Region calorieProgressFill;

    private double targetCalories = 2852;
    private double targetProtein = 132;
    private double targetCarbs = 403;
    private double targetFats = 79;

    private double loggedCalories = 0;
    private double loggedProtein = 0;
    private double loggedCarbs = 0;
    private double loggedFats = 0;

    @FXML
    public void initialize() {
        loadTargetsFromSession();
        loadTodayTotals();
        refreshUI();
    }

    private void loadTodayTotals() {
        com.health.app.dao.MealDAO mealDAO = new com.health.app.dao.MealDAO();

        int userId = 0;
        if (com.health.app.AppSession.getCurrentUser() != null) {
            userId = com.health.app.AppSession.getCurrentUser().getUserId();
        }

        double[] totals = mealDAO.getTodayTotals(userId);

        this.loggedCalories = totals[0];
        this.loggedProtein = totals[1];
        this.loggedCarbs = totals[2];
        this.loggedFats = totals[3];

        double breakfastCal = mealDAO.getMealTypeCalories(userId, "Breakfast");
        double lunchCal = mealDAO.getMealTypeCalories(userId, "Lunch");
        double dinnerCal = mealDAO.getMealTypeCalories(userId, "Dinner");
        double snackCal = mealDAO.getMealTypeCalories(userId, "Snack");

        if (breakfastCalLabel != null) {
            breakfastCalLabel.setText((int) breakfastCal + " kcal");
            breakfastSubLabel.setText(breakfastCal > 0 ? "Food logged" : "No food logged yet");
        }
        if (lunchCalLabel != null) {
            lunchCalLabel.setText((int) lunchCal + " kcal");
            lunchSubLabel.setText(lunchCal > 0 ? "Food logged" : "No food logged yet");
        }
        if (dinnerCalLabel != null) {
            dinnerCalLabel.setText((int) dinnerCal + " kcal");
            dinnerSubLabel.setText(dinnerCal > 0 ? "Food logged" : "No food logged yet");
        }
        if (snackCalLabel != null) {
            snackCalLabel.setText((int) snackCal + " kcal");
            snackSubLabel.setText(snackCal > 0 ? "Food logged" : "No food logged yet");
        }
        loadRecommendations();

    }

    private void refreshUI() {
        loggedCaloriesLabel.setText(String.valueOf((int) loggedCalories));
        targetCaloriesLabel.setText("/ " + (int) targetCalories + " kcal");

        double remainingCalories = Math.max(0, targetCalories - loggedCalories);
        remainingCaloriesLabel.setText((int) remainingCalories + " kcal remaining today");

        int percent = calculatePercent(loggedCalories, targetCalories);
        mealPercentLabel.setText(percent + "%");

        double progressWidth = 350.0 * percent / 100.0;
        calorieProgressFill.setPrefWidth(progressWidth);

        loggedProteinLabel.setText(String.valueOf((int) loggedProtein));
        targetProteinLabel.setText("/ " + (int) targetProtein + "g");

        loggedCarbsLabel.setText(String.valueOf((int) loggedCarbs));
        targetCarbsLabel.setText("/ " + (int) targetCarbs + "g");

        loggedFatsLabel.setText(String.valueOf((int) loggedFats));
        targetFatsLabel.setText("/ " + (int) targetFats + "g");
    }

    private int calculatePercent(double logged, double target) {
        if (target <= 0) {
            return 0;
        }

        double percent = (logged / target) * 100.0;

        if (percent < 0) {
            return 0;
        }

        if (percent > 100) {
            return 100;
        }

        return (int) Math.round(percent);
    }

    @FXML
    private void openAddFood() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/health/app/view/add_food.fxml"));
            Parent addFoodRoot = loader.load();

            Scene scene = loggedCaloriesLabel.getScene();
            scene.setRoot(addFoodRoot);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void backToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/health/app/view/dashboard.fxml"));
            Parent dashboardRoot = loader.load();

            DashboardController dashboardController = loader.getController();

            if (AppSession.getCurrentUser() != null) {
                dashboardController.setUser(AppSession.getCurrentUser());
            }

            Scene scene = loggedCaloriesLabel.getScene();
            scene.setRoot(dashboardRoot);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void loadTargetsFromSession() {
        if (AppSession.getTargetCalories() > 0) {
            this.targetCalories = AppSession.getTargetCalories();
            this.targetProtein = AppSession.getTargetProtein();
            this.targetCarbs = AppSession.getTargetCarbs();
            this.targetFats = AppSession.getTargetFats();
        }
    }
    private void loadRecommendations() {
        double remaining = Math.max(0, targetCalories - loggedCalories);

        String goal = "";
        if (com.health.app.AppSession.getCurrentUser() != null) {
            goal = com.health.app.AppSession.getCurrentUser().getFitnessGoal();
        }

        com.health.app.dao.MealDAO mealDAO = new com.health.app.dao.MealDAO();
        java.util.List<com.health.app.model.FoodItem> recs =
                mealDAO.getRecommendedFoods(remaining, goal);

        Label[] labels = {rec1Label, rec2Label, rec3Label, rec4Label};

        for (int i = 0; i < labels.length; i++) {
            if (labels[i] == null) continue;
            if (i < recs.size()) {
                com.health.app.model.FoodItem food = recs.get(i);
                labels[i].setText(food.getName() +
                        " — " + (int) food.getCaloriesPer100g() + " kcal / 100g" +
                        " | P: " + (int) food.getProteinPer100g() + "g");
                labels[i].setVisible(true);
            } else {
                labels[i].setVisible(false);
            }
        }
    }
}