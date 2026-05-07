package com.health.app.controller;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.health.app.model.OnboardingData;
import com.health.app.model.User;
import com.health.app.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

public class OnboardingController {

    @FXML private StackPane contentArea;
    @FXML private HBox progressTrack;
    @FXML private Label titleLabel;
    @FXML private Button backButton;
    @FXML private Button nextButton;

    @FXML private TextField nameField;
    @FXML private HBox card1, card2, card3, card4, card5;
    @FXML private StackPane check1, check2, check3, check4, check5;
    @FXML private HBox foodCard1, foodCard2, foodCard3, foodCard4, foodCard5, foodCard6;
    @FXML private StackPane foodCheck1, foodCheck2, foodCheck3, foodCheck4, foodCheck5, foodCheck6;
    @FXML private HBox habitCard1, habitCard2, habitCard3, habitCard4, habitCard5, habitCard6, habitCard7, habitCard8;
    @FXML private StackPane habitCheck1, habitCheck2, habitCheck3, habitCheck4, habitCheck5, habitCheck6, habitCheck7, habitCheck8;
    @FXML private HBox mealPlanCard1, mealPlanCard2, mealPlanCard3;
    @FXML private StackPane mealPlanCheck1, mealPlanCheck2, mealPlanCheck3;
    @FXML private HBox activityCard1, activityCard2, activityCard3, activityCard4;
    @FXML private StackPane activityCheck1, activityCheck2, activityCheck3, activityCheck4;
    @FXML private ComboBox<String> genderBox;
    @FXML private TextField ageField;
    @FXML private TextField heightField, currentWeightField, targetWeightField;
    @FXML private HBox weeklyCard1, weeklyCard2, weeklyCard3, weeklyCard4;
    @FXML private StackPane weeklyCheck1, weeklyCheck2, weeklyCheck3, weeklyCheck4;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label accountMessage;

    private int currentStep = 0;
    private boolean mainInitialized = false;
    private final OnboardingData data = new OnboardingData();

    private final String[] screens = {
            "/com/health/app/view/step_name.fxml",
            "/com/health/app/view/step_goal.fxml",
            "/com/health/app/view/step_food.fxml",
            "/com/health/app/view/step_habits.fxml",
            "/com/health/app/view/step_meal_plan.fxml",
            "/com/health/app/view/step_activity.fxml",
            "/com/health/app/view/step_gender_age.fxml",
            "/com/health/app/view/step_body.fxml",
            "/com/health/app/view/step_weekly_goal.fxml",
            "/com/health/app/view/step_account.fxml"
    };

    private final String[] titles = {
            "Welcome", "Your goal", "Food preference", "Healthy habits", "Meal plan",
            "Activity level", "About you", "Body measurements", "Weekly goal", "Create account"
    };

    @FXML
    public void initialize() {
        if (mainInitialized) {
            return;
        }

        if (contentArea == null || progressTrack == null || titleLabel == null) {
            return;
        }

        mainInitialized = true;

        buildProgressSegments();
        loadCurrentStep();
    }
    @FXML
    private void next() {
        saveCurrentStepData();
        printStepData();
        if (currentStep < screens.length - 1) {
            currentStep++;
            loadCurrentStep();
        } else {
            finishOnboarding();
        }
    }

    @FXML
    private void previous() {
        if (currentStep > 0) {
            saveCurrentStepData();
            currentStep--;
            loadCurrentStep();
        }
    }

    private void saveCurrentStepData() {
        switch (currentStep) {
            case 0:
                if (nameField != null && nameField.getText() != null)
                    data.setName(nameField.getText().trim());
                break;
            case 6:
                if (genderBox != null && genderBox.getValue() != null)
                    data.setGender(genderBox.getValue());
                if (ageField != null && ageField.getText() != null && !ageField.getText().trim().isEmpty()) {
                    try { data.setAge(Integer.parseInt(ageField.getText().trim())); }
                    catch (NumberFormatException ignored) {}
                }
                break;
            case 7:
                if (heightField != null && heightField.getText() != null && !heightField.getText().trim().isEmpty()) {
                    try { data.setHeight(Double.parseDouble(heightField.getText().trim())); }
                    catch (NumberFormatException ignored) {}
                }
                if (currentWeightField != null && currentWeightField.getText() != null && !currentWeightField.getText().trim().isEmpty()) {
                    try { data.setCurrentWeight(Double.parseDouble(currentWeightField.getText().trim())); }
                    catch (NumberFormatException ignored) {}
                }
                if (targetWeightField != null && targetWeightField.getText() != null && !targetWeightField.getText().trim().isEmpty()) {
                    try { data.setTargetWeight(Double.parseDouble(targetWeightField.getText().trim())); }
                    catch (NumberFormatException ignored) {}
                }
                break;
            case 9:
                if (emailField != null && emailField.getText() != null)
                    data.setEmail(emailField.getText().trim());
                if (passwordField != null && passwordField.getText() != null)
                    data.setPassword(passwordField.getText().trim());
                break;
        }
    }

    private void printStepData() {
        System.out.println("──────── Step " + (currentStep + 1) + " saved ────────");
        switch (currentStep) {
            case 0: System.out.println("Name: " + data.getName()); break;
            case 1: System.out.println("Goal: " + data.getGoal()); break;
            case 2: System.out.println("Food: " + data.getFoodPreference()); break;
            case 3: System.out.println("Habits: " + data.getHealthyHabits()); break;
            case 4: System.out.println("Meal plan: " + data.getMealPlanChoice()); break;
            case 5: System.out.println("Activity: " + data.getActivityLevel()); break;
            case 6: System.out.println("Gender: " + data.getGender() + " | Age: " + data.getAge()); break;
            case 7: System.out.println("Height: " + data.getHeight() + " | Current: " + data.getCurrentWeight() + " | Target: " + data.getTargetWeight()); break;
            case 8: System.out.println("Weekly goal: " + data.getWeeklyGoal()); break;
            case 9: System.out.println("Email: " + data.getEmail()); break;
        }
    }

    private void buildProgressSegments() {
        progressTrack.getChildren().clear();
        for (int i = 0; i < screens.length; i++) {
            Region segment = new Region();
            segment.getStyleClass().add("progress-segment");
            HBox.setHgrow(segment, Priority.ALWAYS);
            segment.setMaxWidth(Double.MAX_VALUE);
            progressTrack.getChildren().add(segment);
        }
        updateProgressSegments();
    }

    private void updateProgressSegments() {
        for (int i = 0; i < progressTrack.getChildren().size(); i++) {
            Region segment = (Region) progressTrack.getChildren().get(i);
            segment.getStyleClass().removeAll("progress-segment", "progress-segment-active");
            segment.getStyleClass().add(i <= currentStep ? "progress-segment-active" : "progress-segment");
        }
    }

    private void loadCurrentStep() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(screens[currentStep]));
            loader.setController(this);
            Node screen = loader.load();
            contentArea.getChildren().setAll(screen);
            titleLabel.setText(titles[currentStep]);
            updateProgressSegments();
            backButton.setDisable(currentStep == 0);
            backButton.setOpacity(currentStep == 0 ? 0.3 : 1.0);
            nextButton.setText(currentStep == screens.length - 1 ? "Finish" : "Next");
            restoreCardSelections();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void restoreCardSelections() {
        // Restore highlight when user goes back
        if (currentStep == 1 && data.getGoal() != null) {
            String g = data.getGoal();
            if (g.equals("Lose weight")) setCardSelected(card1, check1, true);
            else if (g.equals("Gain muscle")) setCardSelected(card2, check2, true);
            else if (g.equals("Maintain weight")) setCardSelected(card3, check3, true);
            else if (g.equals("Improve fitness")) setCardSelected(card4, check4, true);
            else if (g.equals("Eat healthier")) setCardSelected(card5, check5, true);
        }
        if (currentStep == 6) {
            if (genderBox != null && data.getGender() != null) genderBox.setValue(data.getGender());
            if (ageField != null && data.getAge() > 0) ageField.setText(String.valueOf(data.getAge()));
        }
        if (currentStep == 7) {
            if (heightField != null && data.getHeight() > 0) heightField.setText(String.valueOf(data.getHeight()));
            if (currentWeightField != null && data.getCurrentWeight() > 0) currentWeightField.setText(String.valueOf(data.getCurrentWeight()));
            if (targetWeightField != null && data.getTargetWeight() > 0) targetWeightField.setText(String.valueOf(data.getTargetWeight()));
        }
        if (currentStep == 0 && nameField != null && data.getName() != null) {
            nameField.setText(data.getName());
        }
        if (currentStep == 9) {
            if (emailField != null && data.getEmail() != null) emailField.setText(data.getEmail());
            if (passwordField != null && data.getPassword() != null) passwordField.setText(data.getPassword());
        }
    }

    private void setCardSelected(HBox card, StackPane check, boolean selected) {
        if (card == null) {
            return;
        }

        card.getStyleClass().remove("option-card-selected");

        if (selected) {
            if (!card.getStyleClass().contains("option-card-selected")) {
                card.getStyleClass().add("option-card-selected");
            }
        }

        if (check != null) {
            check.setVisible(selected);
        }
    }
    private void selectOne(HBox chosenCard, HBox[] allCards, StackPane[] allChecks) {
        for (int i = 0; i < allCards.length; i++) {
            setCardSelected(allCards[i], allChecks[i], allCards[i] == chosenCard);
        }
    }

    @FXML private void selectCard1() { handleGoal(card1, "Lose weight"); }
    @FXML private void selectCard2() { handleGoal(card2, "Gain muscle"); }
    @FXML private void selectCard3() { handleGoal(card3, "Maintain weight"); }
    @FXML private void selectCard4() { handleGoal(card4, "Improve fitness"); }
    @FXML private void selectCard5() { handleGoal(card5, "Eat healthier"); }
    private void handleGoal(HBox card, String name) {
        selectOne(card, new HBox[]{card1, card2, card3, card4, card5},
                new StackPane[]{check1, check2, check3, check4, check5});
        data.setGoal(name);
    }

    @FXML private void selectFood1() { handleFood(foodCard1, "Balanced"); }
    @FXML private void selectFood2() { handleFood(foodCard2, "High protein"); }
    @FXML private void selectFood3() { handleFood(foodCard3, "Low carb"); }
    @FXML private void selectFood4() { handleFood(foodCard4, "Vegetarian"); }
    @FXML private void selectFood5() { handleFood(foodCard5, "Mediterranean"); }
    @FXML private void selectFood6() { handleFood(foodCard6, "No preference"); }
    private void handleFood(HBox card, String name) {
        selectOne(card, new HBox[]{foodCard1, foodCard2, foodCard3, foodCard4, foodCard5, foodCard6},
                new StackPane[]{foodCheck1, foodCheck2, foodCheck3, foodCheck4, foodCheck5, foodCheck6});
        data.setFoodPreference(name);
    }

    @FXML private void toggleHabit1() { handleHabit(habitCard1, habitCheck1, "Eat more protein"); }
    @FXML private void toggleHabit2() { handleHabit(habitCard2, habitCheck2, "Track macros"); }
    @FXML private void toggleHabit3() { handleHabit(habitCard3, habitCheck3, "Track calories"); }
    @FXML private void toggleHabit4() { handleHabit(habitCard4, habitCheck4, "Plan more meals"); }
    @FXML private void toggleHabit5() { handleHabit(habitCard5, habitCheck5, "Drink more water"); }
    @FXML private void toggleHabit6() { handleHabit(habitCard6, habitCheck6, "Workout more"); }
    @FXML private void toggleHabit7() { handleHabit(habitCard7, habitCheck7, "Eat more vegetables"); }
    @FXML private void toggleHabit8() { handleHabit(habitCard8, habitCheck8, "Prioritize sleep"); }
    private void handleHabit(HBox card, StackPane check, String habit) {
        boolean nowSelected = !check.isVisible();
        setCardSelected(card, check, nowSelected);
        if (nowSelected) data.addHabit(habit);
        else data.removeHabit(habit);
    }

    @FXML private void selectMealPlan1() { handleMealPlan(mealPlanCard1, "Yes, definitely"); }
    @FXML private void selectMealPlan2() { handleMealPlan(mealPlanCard2, "Open to trying"); }
    @FXML private void selectMealPlan3() { handleMealPlan(mealPlanCard3, "No thanks"); }
    private void handleMealPlan(HBox card, String name) {
        selectOne(card, new HBox[]{mealPlanCard1, mealPlanCard2, mealPlanCard3},
                new StackPane[]{mealPlanCheck1, mealPlanCheck2, mealPlanCheck3});
        data.setMealPlanChoice(name);
    }

    @FXML private void selectActivity1() { handleActivity(activityCard1, "Not very active"); }
    @FXML private void selectActivity2() { handleActivity(activityCard2, "Lightly active"); }
    @FXML private void selectActivity3() { handleActivity(activityCard3, "Active"); }
    @FXML private void selectActivity4() { handleActivity(activityCard4, "Very active"); }
    private void handleActivity(HBox card, String name) {
        selectOne(card, new HBox[]{activityCard1, activityCard2, activityCard3, activityCard4},
                new StackPane[]{activityCheck1, activityCheck2, activityCheck3, activityCheck4});
        data.setActivityLevel(name);
    }

    @FXML private void selectWeekly1() { handleWeekly(weeklyCard1, "0.2 kg per week"); }
    @FXML private void selectWeekly2() { handleWeekly(weeklyCard2, "0.5 kg per week"); }
    @FXML private void selectWeekly3() { handleWeekly(weeklyCard3, "0.8 kg per week"); }
    @FXML private void selectWeekly4() { handleWeekly(weeklyCard4, "1.0 kg per week"); }
    private void handleWeekly(HBox card, String name) {
        selectOne(card, new HBox[]{weeklyCard1, weeklyCard2, weeklyCard3, weeklyCard4},
                new StackPane[]{weeklyCheck1, weeklyCheck2, weeklyCheck3, weeklyCheck4});
        data.setWeeklyGoal(name);
    }

    private void finishOnboarding() {
        if (data.getName() == null || data.getName().isEmpty()) {
            showAccountMessage("Please go back and enter your name.", true); return;
        }
        if (data.getEmail() == null || data.getEmail().isEmpty()) {
            showAccountMessage("Please enter your email.", true); return;
        }
        if (data.getPassword() == null || data.getPassword().length() < 4) {
            showAccountMessage("Password must be at least 4 characters.", true); return;
        }
        if (data.getAge() <= 0 || data.getHeight() <= 0 || data.getCurrentWeight() <= 0) {
            showAccountMessage("Some body info is missing. Please go back.", true); return;
        }

        User user = new User();
        user.setName(data.getName());
        user.setAge(data.getAge());
        user.setGender(data.getGender() == null ? "Male" : data.getGender());
        user.setHeight(data.getHeight());
        user.setWeight(data.getCurrentWeight());
        user.setTargetWeight(data.getTargetWeight());
        user.setCredentials(data.getEmail());
        user.setFitnessGoal(data.getGoal() == null ? "Improve fitness" : data.getGoal());
        user.setActivityLevel(data.getActivityLevel());
        user.setWeeklyGoal(data.getWeeklyGoal());
        user.setFoodPreference(data.getFoodPreference());
        user.setMealPlanChoice(data.getMealPlanChoice());

        boolean created = new UserService().createAccount(user, data.getPassword());

        if (created) {
            System.out.println("===== USER SAVED TO DATABASE =====");
            System.out.println("Name: " + data.getName());
            System.out.println("Email: " + data.getEmail());
            System.out.println("Age: " + data.getAge());
            System.out.println("Gender: " + data.getGender());
            System.out.println("Height: " + data.getHeight());
            System.out.println("Weight: " + data.getCurrentWeight());
            System.out.println("Goal: " + data.getGoal());
            System.out.println("==================================");

            openDashboard(user);

        } else {
            showAccountMessage("Couldn't create account. Email may already exist.", true);
        }
    }

    private void showAccountMessage(String text, boolean isError) {
        if (accountMessage == null) return;
        accountMessage.setText(text);
        accountMessage.setStyle(isError
                ? "-fx-text-fill: #FF6B6B; -fx-font-size: 13px;"
                : "-fx-text-fill: #43D18D; -fx-font-size: 13px;");
    }
    private void openDashboard(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/health/app/view/dashboard.fxml"));
            Parent dashboardRoot = loader.load();

            DashboardController dashboardController = loader.getController();
            dashboardController.setUser(user);

            Scene scene = nextButton.getScene();
            scene.getStylesheets().clear();
            scene.getStylesheets().add(
                    getClass().getResource("/com/health/app/view/dashboard.css").toExternalForm()
            );
            scene.setRoot(dashboardRoot);

        } catch (Exception e) {
            e.printStackTrace();
            showAccountMessage("Account created, but dashboard could not open.", true);
        }
    }
}