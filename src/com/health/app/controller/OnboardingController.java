package com.health.app.controller;

import com.health.app.model.OnboardingData;
import com.health.app.model.User;
import com.health.app.service.UserService;
import javafx.css.PseudoClass;
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

    // ---------- Shell controls ----------
    @FXML private StackPane contentArea;
    @FXML private HBox progressTrack;
    @FXML private Label titleLabel;
    @FXML private Button backButton;
    @FXML private Button nextButton;

    // ---------- Step 2 (Goal) ----------
    @FXML private HBox card1, card2, card3, card4, card5;
    @FXML private StackPane check1, check2, check3, check4, check5;

    // ---------- Step 3 (Food) ----------
    @FXML private HBox foodCard1, foodCard2, foodCard3, foodCard4, foodCard5, foodCard6;
    @FXML private StackPane foodCheck1, foodCheck2, foodCheck3, foodCheck4, foodCheck5, foodCheck6;

    // ---------- Step 4 (Habits, multi-select) ----------
    @FXML private HBox habitCard1, habitCard2, habitCard3, habitCard4, habitCard5, habitCard6, habitCard7, habitCard8;
    @FXML private StackPane habitCheck1, habitCheck2, habitCheck3, habitCheck4, habitCheck5, habitCheck6, habitCheck7, habitCheck8;

    // ---------- Step 5 (Meal plan) ----------
    @FXML private HBox mealPlanCard1, mealPlanCard2, mealPlanCard3;
    @FXML private StackPane mealPlanCheck1, mealPlanCheck2, mealPlanCheck3;

    // ---------- Step 6 (Activity) ----------
    @FXML private HBox activityCard1, activityCard2, activityCard3, activityCard4;
    @FXML private StackPane activityCheck1, activityCheck2, activityCheck3, activityCheck4;

    // ---------- Step 7 (Gender + Age) ----------
    @FXML private ComboBox<String> genderBox;
    @FXML private TextField ageField;

    // ---------- Step 8 (Body) ----------
    @FXML private TextField heightField;
    @FXML private TextField currentWeightField;
    @FXML private TextField targetWeightField;

    // ---------- Step 9 (Weekly goal) ----------
    @FXML private HBox weeklyCard1, weeklyCard2, weeklyCard3, weeklyCard4;
    @FXML private StackPane weeklyCheck1, weeklyCheck2, weeklyCheck3, weeklyCheck4;

    // ---------- Step 10 (Account) ----------
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label accountMessage;

    // ---------- Internal state ----------
    private int currentStep = 0;
    private final OnboardingData data = new OnboardingData();
    private static final PseudoClass SELECTED = PseudoClass.getPseudoClass("selected");

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

    // Step 1 (Name)
    @FXML private TextField nameField;

    // ============================================================
    //  LIFECYCLE
    // ============================================================
    @FXML
    public void initialize() {
        buildProgressSegments();
        loadCurrentStep();
    }

    @FXML
    private void next() {
        // Capture the name on step 1 before moving on
        if (currentStep == 0 && nameField != null) {
            data.setName(nameField.getText().trim());
        }
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
            currentStep--;
            loadCurrentStep();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setCardSelected(HBox card, StackPane check, boolean selected) {
        if (card == null) return;

        card.setStyle("");

        if (selected) {
            card.setStyle(
                    "-fx-background-color: #FF6B3D;" +
                            "-fx-background-radius: 18;" +
                            "-fx-border-color: #FFFFFF;" +
                            "-fx-border-radius: 18;" +
                            "-fx-border-width: 3;" +
                            "-fx-padding: 18 20 18 20;" +
                            "-fx-cursor: hand;" +
                            "-fx-effect: dropshadow(gaussian, rgba(255, 107, 61, 0.8), 25, 0.5, 0, 0);"
            );
        }

        if (check != null) check.setVisible(selected);
    }
    // ============================================================
    //  CARD HELPERS (the magic that fixes selection)
    // ============================================================
    private void selectOne(HBox chosenCard, StackPane chosenCheck,
                           HBox[] allCards, StackPane[] allChecks) {
        for (int i = 0; i < allCards.length; i++) {
            setCardSelected(allCards[i], allChecks[i], allCards[i] == chosenCard);
        }
    }

    // ============================================================
    //  STEP 2 — Goal (single-select)
    // ============================================================
    @FXML private void selectCard1() { handleGoal(card1, check1, "Lose weight"); }
    @FXML private void selectCard2() { handleGoal(card2, check2, "Gain muscle"); }
    @FXML private void selectCard3() { handleGoal(card3, check3, "Maintain weight"); }
    @FXML private void selectCard4() { handleGoal(card4, check4, "Improve fitness"); }
    @FXML private void selectCard5() { handleGoal(card5, check5, "Eat healthier"); }

    private void handleGoal(HBox card, StackPane check, String name) {
        selectOne(card, check,
                new HBox[]{card1, card2, card3, card4, card5},
                new StackPane[]{check1, check2, check3, check4, check5});
        data.setGoal(name);
        System.out.println("=========================");
        System.out.println("CURRENT SELECTED GOAL: " + data.getGoal());
        System.out.println("=========================");
    }
    // ============================================================
    //  STEP 3 — Food (single-select)
    // ============================================================
    @FXML private void selectFood1() { handleFood(foodCard1, foodCheck1, "Balanced"); }
    @FXML private void selectFood2() { handleFood(foodCard2, foodCheck2, "High protein"); }
    @FXML private void selectFood3() { handleFood(foodCard3, foodCheck3, "Low carb"); }
    @FXML private void selectFood4() { handleFood(foodCard4, foodCheck4, "Vegetarian"); }
    @FXML private void selectFood5() { handleFood(foodCard5, foodCheck5, "Mediterranean"); }
    @FXML private void selectFood6() { handleFood(foodCard6, foodCheck6, "No preference"); }

    private void handleFood(HBox card, StackPane check, String name) {
        selectOne(card, check,
                new HBox[]{foodCard1, foodCard2, foodCard3, foodCard4, foodCard5, foodCard6},
                new StackPane[]{foodCheck1, foodCheck2, foodCheck3, foodCheck4, foodCheck5, foodCheck6});
        data.setFoodPreference(name);
        System.out.println("Food: " + name);
    }

    // ============================================================
    //  STEP 4 — Habits (multi-select)
    // ============================================================
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
        System.out.println("Habits: " + data.getHealthyHabits());
    }

    // ============================================================
    //  STEP 5 — Meal plan (single-select)
    // ============================================================
    @FXML private void selectMealPlan1() { handleMealPlan(mealPlanCard1, mealPlanCheck1, "Yes, definitely"); }
    @FXML private void selectMealPlan2() { handleMealPlan(mealPlanCard2, mealPlanCheck2, "Open to trying"); }
    @FXML private void selectMealPlan3() { handleMealPlan(mealPlanCard3, mealPlanCheck3, "No thanks"); }

    private void handleMealPlan(HBox card, StackPane check, String name) {
        selectOne(card, check,
                new HBox[]{mealPlanCard1, mealPlanCard2, mealPlanCard3},
                new StackPane[]{mealPlanCheck1, mealPlanCheck2, mealPlanCheck3});
        data.setMealPlanChoice(name);
        System.out.println("Meal plan: " + name);
    }

    // ============================================================
    //  STEP 6 — Activity (single-select)
    // ============================================================
    @FXML private void selectActivity1() { handleActivity(activityCard1, activityCheck1, "Not very active"); }
    @FXML private void selectActivity2() { handleActivity(activityCard2, activityCheck2, "Lightly active"); }
    @FXML private void selectActivity3() { handleActivity(activityCard3, activityCheck3, "Active"); }
    @FXML private void selectActivity4() { handleActivity(activityCard4, activityCheck4, "Very active"); }

    private void handleActivity(HBox card, StackPane check, String name) {
        selectOne(card, check,
                new HBox[]{activityCard1, activityCard2, activityCard3, activityCard4},
                new StackPane[]{activityCheck1, activityCheck2, activityCheck3, activityCheck4});
        data.setActivityLevel(name);
        System.out.println("Activity: " + name);
    }

    // ============================================================
    //  STEP 7 — Gender + Age
    // ============================================================
    @FXML
    private void onGenderSelected() {
        if (genderBox != null && genderBox.getValue() != null) {
            data.setGender(genderBox.getValue());
            System.out.println("Gender: " + data.getGender());
        }
    }

    @FXML
    private void onAgeChanged() {
        if (ageField == null) return;
        String text = ageField.getText().trim();
        if (text.isEmpty()) return;
        try {
            data.setAge(Integer.parseInt(text));
            System.out.println("Age: " + data.getAge());
        } catch (NumberFormatException ignored) {}
    }

    // ============================================================
    //  STEP 8 — Body measurements
    // ============================================================
    @FXML
    private void onHeightChanged() {
        if (heightField == null) return;
        try {
            if (!heightField.getText().trim().isEmpty()) {
                data.setHeight(Double.parseDouble(heightField.getText().trim()));
            }
        } catch (NumberFormatException ignored) {}
    }

    @FXML
    private void onCurrentWeightChanged() {
        if (currentWeightField == null) return;
        try {
            if (!currentWeightField.getText().trim().isEmpty()) {
                data.setCurrentWeight(Double.parseDouble(currentWeightField.getText().trim()));
            }
        } catch (NumberFormatException ignored) {}
    }

    @FXML
    private void onTargetWeightChanged() {
        if (targetWeightField == null) return;
        try {
            if (!targetWeightField.getText().trim().isEmpty()) {
                data.setTargetWeight(Double.parseDouble(targetWeightField.getText().trim()));
            }
        } catch (NumberFormatException ignored) {}
    }

    // ============================================================
    //  STEP 9 — Weekly goal (single-select)
    // ============================================================
    @FXML private void selectWeekly1() { handleWeekly(weeklyCard1, weeklyCheck1, "0.2 kg per week"); }
    @FXML private void selectWeekly2() { handleWeekly(weeklyCard2, weeklyCheck2, "0.5 kg per week"); }
    @FXML private void selectWeekly3() { handleWeekly(weeklyCard3, weeklyCheck3, "0.8 kg per week"); }
    @FXML private void selectWeekly4() { handleWeekly(weeklyCard4, weeklyCheck4, "1.0 kg per week"); }

    private void handleWeekly(HBox card, StackPane check, String name) {
        selectOne(card, check,
                new HBox[]{weeklyCard1, weeklyCard2, weeklyCard3, weeklyCard4},
                new StackPane[]{weeklyCheck1, weeklyCheck2, weeklyCheck3, weeklyCheck4});
        data.setWeeklyGoal(name);
        System.out.println("Weekly: " + name);
    }

    // ============================================================
    //  STEP 10 — Account
    // ============================================================
    @FXML
    private void onEmailChanged() {
        if (emailField != null) data.setEmail(emailField.getText().trim());
    }

    @FXML
    private void onPasswordChanged() {
        if (passwordField != null) data.setPassword(passwordField.getText().trim());
    }

    // ============================================================
    //  FINISH — saves user to DB
    // ============================================================
    private void finishOnboarding() {
        if (emailField != null) data.setEmail(emailField.getText().trim());
        if (passwordField != null) data.setPassword(passwordField.getText().trim());

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
            showAccountMessage("Some body info is missing. Please go back and check.", true); return;
        }

        User user = new User();
        user.setName(data.getName());
        user.setAge(data.getAge());
        user.setGender(data.getGender() == null ? "Male" : data.getGender());
        user.setHeight(data.getHeight());
        user.setWeight(data.getCurrentWeight());
        user.setCredentials(data.getEmail());
        user.setFitnessGoal(data.getGoal() == null ? "Improve fitness" : data.getGoal());

        boolean created = new UserService().createAccount(user, data.getPassword());

        if (created) {
            showAccountMessage("Account created! Welcome, " + data.getName(), false);
            System.out.println("===== USER SAVED TO DATABASE =====");
            System.out.println("Name: " + data.getName());
            System.out.println("Email: " + data.getEmail());
            System.out.println("Goal: " + data.getGoal());
            System.out.println("Habits: " + data.getHealthyHabits());
            System.out.println("==================================");
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
}