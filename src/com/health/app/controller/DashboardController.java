package com.health.app.controller;

import com.health.app.model.User;
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

    // The maximum width a macro bar can fill (matches the card inner width)
    private static final double MACRO_BAR_MAX_WIDTH = 100;

    // Defaults if no user is loaded yet
    private User currentUser;
    private double targetCalories = 2520;
    private double consumedCalories = 1847;
    private double targetProtein = 180;  // grams
    private double consumedProtein = 128;
    private double targetCarbs = 320;
    private double consumedCarbs = 186;
    private double targetFats = 70;
    private double consumedFats = 52;

    @FXML
    public void initialize() {
        // Will be called once when the FXML loads
        applyDefaults();
        refreshUI();
    }

    /**
     * Called from outside (e.g. after onboarding) to provide the logged-in user.
     */
    public void setUser(User user) {
        this.currentUser = user;
        if (user != null) {
            calculateTargetsFromUser(user);
        }
        refreshUI();
    }

    private void applyDefaults() {
        // Demo defaults so the dashboard looks alive even before connecting a real user
    }

    /**
     * Mifflin-St Jeor BMR formula + activity multiplier.
     * This calculates daily calorie target based on user data.
     */
    private void calculateTargetsFromUser(User user) {
        double bmr;
        if ("Female".equalsIgnoreCase(user.getGender())) {
            bmr = 10 * user.getWeight() + 6.25 * user.getHeight() - 5 * user.getAge() - 161;
        } else {
            bmr = 10 * user.getWeight() + 6.25 * user.getHeight() - 5 * user.getAge() + 5;
        }

        // Default to "Lightly active" multiplier (1.375) — can be expanded later
        double activityMultiplier = 1.375;
        double tdee = bmr * activityMultiplier;

        // Adjust for goal
        String goal = user.getFitnessGoal();
        if ("Lose weight".equalsIgnoreCase(goal)) {
            tdee -= 500; // 500 kcal deficit
        } else if ("Gain muscle".equalsIgnoreCase(goal)) {
            tdee += 300; // small surplus
        }

        this.targetCalories = Math.round(tdee);

        // Macros: 30% protein, 45% carbs, 25% fat
        this.targetProtein = Math.round((tdee * 0.30) / 4);  // 4 kcal per g of protein
        this.targetCarbs   = Math.round((tdee * 0.45) / 4);  // 4 kcal per g of carbs
        this.targetFats    = Math.round((tdee * 0.25) / 9);  // 9 kcal per g of fat

        // For demo — assume the user has consumed ~73% of target
        this.consumedCalories = Math.round(this.targetCalories * 0.73);
        this.consumedProtein  = Math.round(this.targetProtein * 0.70);
        this.consumedCarbs    = Math.round(this.targetCarbs   * 0.55);
        this.consumedFats     = Math.round(this.targetFats    * 0.80);
    }

    private void refreshUI() {
        // Header
        if (currentUser != null) {
            userNameLabel.setText("Hi, " + currentUser.getName());
            avatarLabel.setText(currentUser.getName().substring(0, 1).toUpperCase());
        } else {
            userNameLabel.setText("Hi, User");
            avatarLabel.setText("U");
        }

        // Big numbers
        caloriesValueLabel.setText(String.valueOf((int) consumedCalories));
        caloriesTargetLabel.setText("of " + (int) targetCalories + " kcal");

        double remaining = Math.max(0, targetCalories - consumedCalories);
        caloriesRemainingLabel.setText((int) remaining + " kcal left today");

        // Percent of goal (in center of ring)
        int percent = (int) Math.round((consumedCalories / targetCalories) * 100);
        ringPercentLabel.setText(String.valueOf(percent));

        // Animate the rings (set arc length based on percent)
        // Outer ring: calories (max -360 = full circle)
        double caloriesPercent = Math.min(1.0, consumedCalories / targetCalories);
        caloriesArc.setLength(-360 * caloriesPercent);

        // Inner ring: protein
        double proteinPercent = Math.min(1.0, consumedProtein / targetProtein);
        proteinArc.setLength(-360 * proteinPercent);

        // Macro cards
        proteinValueLabel.setText(String.valueOf((int) consumedProtein));
        carbsValueLabel.setText(String.valueOf((int) consumedCarbs));
        fatsValueLabel.setText(String.valueOf((int) consumedFats));

        // Macro progress bars
        proteinBarFill.setPrefWidth(MACRO_BAR_MAX_WIDTH * proteinPercent);
        carbsBarFill.setPrefWidth(MACRO_BAR_MAX_WIDTH * Math.min(1.0, consumedCarbs / targetCarbs));
        fatsBarFill.setPrefWidth(MACRO_BAR_MAX_WIDTH * Math.min(1.0, consumedFats / targetFats));
    }

    // ---------- Quick Actions ----------
    @FXML
    private void openMeals() {
        System.out.println("Open Meals page (will build next)");
    }

    @FXML
    private void openWorkout() {
        System.out.println("Open Workout page (will build next)");
    }

    @FXML
    private void openProgress() {
        System.out.println("Open Progress page (will build next)");
    }

    @FXML
    private void openReminders() {
        System.out.println("Open Reminders page (will build next)");
    }
}