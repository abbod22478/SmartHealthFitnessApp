package com.health.app.controller;

import com.health.app.AppSession;
import com.health.app.dao.WorkoutDAO;
import com.health.app.model.Exercise;
import com.health.app.model.WorkoutPlan;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.util.List;

public class WorkoutController {

    // ── Plans view ──
    @FXML private VBox plansView;
    @FXML private VBox plansListContainer;
    @FXML private TextField planNameField;
    @FXML private Label totalSessionsLabel;
    @FXML private Label totalPlansLabel;

    // ── Plan detail view ──
    @FXML private VBox planDetailView;
    @FXML private Label planDetailNameLabel;
    @FXML private VBox exercisesListContainer;
    @FXML private TextField exerciseNameField;
    @FXML private TextField setsField;
    @FXML private TextField repsField;
    @FXML private TextField restField;
    @FXML private Label planMessageLabel;

    // ── Active workout view ──
    @FXML private VBox workoutActiveView;
    @FXML private Label activeWorkoutNameLabel;
    @FXML private Label currentExerciseLabel;
    @FXML private Label setsProgressLabel;
    @FXML private Label repsLabel;
    @FXML private Label exercisesCompleteLabel;
    @FXML private Label exercisesRemainingLabel;
    @FXML private Label totalExercisesLabel;
    @FXML private VBox restTimerBox;
    @FXML private Label timerLabel;
    @FXML private Label workoutMessageLabel;
    @FXML private Button completeSetButton;
    @FXML private Button skipExerciseButton;

    private final WorkoutDAO workoutDAO = new WorkoutDAO();
    private WorkoutPlan currentPlan;
    private List<Exercise> currentExercises;
    private int currentExerciseIndex = 0;
    private Timeline restTimer;
    private int restSeconds = 0;

    private int getUserId() {
        if (AppSession.getCurrentUser() != null)
            return AppSession.getCurrentUser().getUserId();
        return 0;
    }

    @FXML
    public void initialize() {
        loadPlansView();
    }

    // ═══════════════════════════════════
    //  PLANS VIEW
    // ═══════════════════════════════════

    private void loadPlansView() {
        int userId = getUserId();
        List<WorkoutPlan> plans = workoutDAO.getPlansForUser(userId);

        totalPlansLabel.setText(String.valueOf(plans.size()));
        totalSessionsLabel.setText(String.valueOf(workoutDAO.getSessionCount(userId)));

        plansListContainer.getChildren().clear();

        if (plans.isEmpty()) {
            Label empty = new Label("No plans yet. Create your first workout plan!");
            empty.setStyle("-fx-text-fill: #9A9AA8; -fx-font-size: 13px;");
            plansListContainer.getChildren().add(empty);
        } else {
            for (WorkoutPlan plan : plans) {
                plansListContainer.getChildren().add(buildPlanCard(plan));
            }
        }
    }

    private HBox buildPlanCard(WorkoutPlan plan) {
        HBox card = new HBox(12);
        card.setStyle(
                "-fx-background-color: rgba(255,255,255,0.04);" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-color: rgba(255,255,255,0.08);" +
                        "-fx-border-radius: 14;" +
                        "-fx-border-width: 1;" +
                        "-fx-padding: 14 12 14 12;" +
                        "-fx-cursor: hand;"
        );
        card.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);
        Label nameLabel = new Label(plan.getPlanName());
        nameLabel.setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 15px; -fx-font-weight: bold;");

        List<Exercise> exList = workoutDAO.getExercisesForPlan(plan.getPlanId());
        Label countLabel = new Label(exList.size() + " exercises");
        countLabel.setStyle("-fx-text-fill: #9A9AA8; -fx-font-size: 12px;");
        info.getChildren().addAll(nameLabel, countLabel);

        Button viewBtn = new Button("View");
        viewBtn.setStyle(
                "-fx-background-color: linear-gradient(to right, #FF8A3D, #FF3B5C);" +
                        "-fx-background-radius: 10; -fx-text-fill: white;" +
                        "-fx-font-size: 12px; -fx-font-weight: bold; -fx-cursor: hand;"
        );
        viewBtn.setOnAction(e -> openPlanDetail(plan));

        card.getChildren().addAll(info, viewBtn);
        return card;
    }

    @FXML
    private void createPlan() {
        String name = planNameField.getText().trim();
        if (name.isEmpty()) {
            planNameField.setPromptText("Enter a plan name first!");
            return;
        }

        boolean created = workoutDAO.createPlan(getUserId(), name);
        if (created) {
            planNameField.clear();
            loadPlansView();
        }
    }

    // ═══════════════════════════════════
    //  PLAN DETAIL VIEW
    // ═══════════════════════════════════

    private void openPlanDetail(WorkoutPlan plan) {
        currentPlan = plan;
        planDetailNameLabel.setText(plan.getPlanName());
        loadExercisesList();
        showView(planDetailView);
    }

    private void loadExercisesList() {
        exercisesListContainer.getChildren().clear();
        List<Exercise> exercises = workoutDAO.getExercisesForPlan(currentPlan.getPlanId());

        if (exercises.isEmpty()) {
            Label empty = new Label("No exercises yet. Add your first exercise!");
            empty.setStyle("-fx-text-fill: #9A9AA8; -fx-font-size: 13px;");
            exercisesListContainer.getChildren().add(empty);
        } else {
            for (Exercise ex : exercises) {
                exercisesListContainer.getChildren().add(buildExerciseCard(ex));
            }
        }
    }

    private HBox buildExerciseCard(Exercise ex) {
        HBox card = new HBox(10);
        card.setStyle(
                "-fx-background-color: rgba(255,255,255,0.04);" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: rgba(255,255,255,0.08);" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-width: 1;" +
                        "-fx-padding: 12 14 12 14;"
        );
        card.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        VBox info = new VBox(2);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label nameLabel = new Label(ex.getName());
        nameLabel.setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 14px; -fx-font-weight: bold;");

        Label detailLabel = new Label(
                ex.getSets() + " sets × " + ex.getRepetitions() + " reps  |  Rest: " + ex.getRestIntervalSecs() + "s"
        );
        detailLabel.setStyle("-fx-text-fill: #9A9AA8; -fx-font-size: 12px;");

        info.getChildren().addAll(nameLabel, detailLabel);
        card.getChildren().add(info);
        return card;
    }

    @FXML
    private void addExercise() {
        String name = exerciseNameField.getText().trim();
        if (name.isEmpty()) {
            showPlanMessage("Please enter exercise name.", true);
            return;
        }

        int sets = 3, reps = 10, rest = 60;
        try {
            if (!setsField.getText().trim().isEmpty())
                sets = Integer.parseInt(setsField.getText().trim());
            if (!repsField.getText().trim().isEmpty())
                reps = Integer.parseInt(repsField.getText().trim());
            if (!restField.getText().trim().isEmpty())
                rest = Integer.parseInt(restField.getText().trim());
        } catch (NumberFormatException e) {
            showPlanMessage("Sets, reps, and rest must be numbers.", true);
            return;
        }

        boolean added = workoutDAO.addExercise(currentPlan.getPlanId(), name, sets, reps, rest);
        if (added) {
            exerciseNameField.clear();
            setsField.clear();
            repsField.clear();
            restField.clear();
            loadExercisesList();
            showPlanMessage("Exercise added!", false);
        } else {
            showPlanMessage("Failed to add exercise.", true);
        }
    }

    private void showPlanMessage(String msg, boolean isError) {
        planMessageLabel.setText(msg);
        planMessageLabel.setStyle(isError
                ? "-fx-text-fill: #FF6B6B; -fx-font-size: 13px;"
                : "-fx-text-fill: #43D18D; -fx-font-size: 13px;");
    }

    // ═══════════════════════════════════
    //  ACTIVE WORKOUT VIEW
    // ═══════════════════════════════════

    @FXML
    private void startWorkout() {
        currentExercises = workoutDAO.getExercisesForPlan(currentPlan.getPlanId());

        if (currentExercises.isEmpty()) {
            showPlanMessage("Add exercises before starting!", true);
            return;
        }

        // Reset all
        for (Exercise ex : currentExercises) ex.setCompletedSets(0);
        currentExerciseIndex = 0;

        activeWorkoutNameLabel.setText(currentPlan.getPlanName());
        showView(workoutActiveView);
        updateActiveWorkoutUI();
    }

    private void updateActiveWorkoutUI() {
        if (currentExerciseIndex >= currentExercises.size()) {
            finishWorkout();
            return;
        }

        Exercise current = currentExercises.get(currentExerciseIndex);

        currentExerciseLabel.setText(current.getName());
        setsProgressLabel.setText("Set " + (current.getCompletedSets() + 1) + " / " + current.getSets());
        repsLabel.setText(current.getRepetitions() + " reps");

        int done = 0;
        for (Exercise ex : currentExercises) {
            if (ex.isCompleted()) done++;
        }
        int remaining = currentExercises.size() - done;

        exercisesCompleteLabel.setText(String.valueOf(done));
        exercisesRemainingLabel.setText(String.valueOf(remaining));
        totalExercisesLabel.setText(String.valueOf(currentExercises.size()));

        restTimerBox.setVisible(false);
        restTimerBox.setManaged(false);
        completeSetButton.setDisable(false);
        workoutMessageLabel.setText("");
    }

    @FXML
    private void completeSet() {
        if (currentExerciseIndex >= currentExercises.size()) return;

        Exercise current = currentExercises.get(currentExerciseIndex);
        current.completeSet();

        setsProgressLabel.setText("Set " + current.getCompletedSets() + " / " + current.getSets());

        if (current.isCompleted()) {
            currentExerciseIndex++;
            if (currentExerciseIndex < currentExercises.size()) {
                workoutMessageLabel.setText("✓ " + current.getName() + " complete! Moving to next...");
                workoutMessageLabel.setStyle("-fx-text-fill: #43D18D; -fx-font-size: 13px;");
            }
            updateActiveWorkoutUI();
        } else {
            // Start rest timer
            startRestTimer(current.getRestIntervalSecs());
        }
    }

    @FXML
    private void skipExercise() {
        if (currentExerciseIndex >= currentExercises.size()) return;
        stopRestTimer();
        currentExerciseIndex++;
        updateActiveWorkoutUI();
    }

    private void startRestTimer(int seconds) {
        stopRestTimer();
        restSeconds = seconds;

        restTimerBox.setVisible(true);
        restTimerBox.setManaged(true);
        completeSetButton.setDisable(true);
        timerLabel.setText(String.valueOf(restSeconds));

        restTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            restSeconds--;
            timerLabel.setText(String.valueOf(restSeconds));

            if (restSeconds <= 0) {
                stopRestTimer();
                restTimerBox.setVisible(false);
                restTimerBox.setManaged(false);
                completeSetButton.setDisable(false);
                workoutMessageLabel.setText("Rest done! Complete your next set.");
                workoutMessageLabel.setStyle("-fx-text-fill: #FF8A3D; -fx-font-size: 13px;");

                if (currentExerciseIndex < currentExercises.size()) {
                    Exercise current = currentExercises.get(currentExerciseIndex);
                    setsProgressLabel.setText("Set " + (current.getCompletedSets() + 1) + " / " + current.getSets());
                }
            }
        }));
        restTimer.setCycleCount(Timeline.INDEFINITE);
        restTimer.play();
    }

    private void stopRestTimer() {
        if (restTimer != null) {
            restTimer.stop();
            restTimer = null;
        }
    }

    private void finishWorkout() {
        stopRestTimer();

        workoutDAO.saveSession(getUserId(), currentPlan.getPlanId());

        currentExerciseLabel.setText("🎉 Workout Complete!");
        setsProgressLabel.setText("All exercises done!");
        repsLabel.setText("");
        completeSetButton.setDisable(true);
        skipExerciseButton.setDisable(true);
        restTimerBox.setVisible(false);
        restTimerBox.setManaged(false);
        workoutMessageLabel.setText("Great work! Session saved. Going back in 3 seconds...");
        workoutMessageLabel.setStyle("-fx-text-fill: #43D18D; -fx-font-size: 13px;");

        // Auto go back to plans after 3 seconds
        Timeline delay = new Timeline(new KeyFrame(Duration.seconds(3), e -> {
            currentExerciseIndex = 0;
            completeSetButton.setDisable(false);
            skipExerciseButton.setDisable(false);
            loadPlansView();
            showView(plansView);
        }));
        delay.setCycleCount(1);
        delay.play();
    }

    // ═══════════════════════════════════
    //  NAVIGATION
    // ═══════════════════════════════════

    private void showView(VBox view) {
        plansView.setVisible(false);        plansView.setManaged(false);
        planDetailView.setVisible(false);   planDetailView.setManaged(false);
        workoutActiveView.setVisible(false); workoutActiveView.setManaged(false);

        view.setVisible(true);
        view.setManaged(true);
    }

    @FXML
    private void backToPlans() {
        stopRestTimer();
        loadPlansView();
        showView(plansView);
    }

    @FXML
    private void backToDashboard() {
        stopRestTimer();
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/health/app/view/dashboard.fxml"));
            Parent root = loader.load();
            DashboardController dc = loader.getController();
            if (AppSession.getCurrentUser() != null) {
                dc.setUser(AppSession.getCurrentUser());
            }
            Scene scene = plansView.getScene();
            scene.setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}