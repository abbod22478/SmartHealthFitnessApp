package com.health.app.controller;

import com.health.app.dao.FoodItemDAO;
import com.health.app.model.FoodItem;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.List;

public class AddFoodController {

    @FXML private ComboBox<FoodItem> foodItemBox;
    @FXML private ComboBox<String> mealTypeBox;
    @FXML private TextField quantityField;

    @FXML private Label calculatedCaloriesLabel;
    @FXML private Label calculatedProteinLabel;
    @FXML private Label calculatedCarbsLabel;
    @FXML private Label calculatedFatsLabel;
    @FXML private Label messageLabel;

    private final FoodItemDAO foodItemDAO = new FoodItemDAO();

    private double calculatedCalories = 0;
    private double calculatedProtein = 0;
    private double calculatedCarbs = 0;
    private double calculatedFats = 0;

    @FXML
    public void initialize() {
        loadMealTypes();
        loadFoodItems();

        foodItemBox.setOnAction(event -> calculateNutrition());
        quantityField.textProperty().addListener((observable, oldValue, newValue) -> calculateNutrition());
    }

    private void loadMealTypes() {
        mealTypeBox.setItems(FXCollections.observableArrayList(
                "Breakfast",
                "Lunch",
                "Dinner",
                "Snack"
        ));
    }

    private void loadFoodItems() {
        List<FoodItem> foodItems = foodItemDAO.getAllFoodItems();

        foodItemBox.setItems(FXCollections.observableArrayList(foodItems));

        if (foodItems.isEmpty()) {
            showMessage("No food items found in database.", true);
        }
    }

    private void calculateNutrition() {
        FoodItem selectedFood = foodItemBox.getValue();

        if (selectedFood == null) {
            clearCalculatedValues();
            return;
        }

        String quantityText = quantityField.getText();

        if (quantityText == null || quantityText.trim().isEmpty()) {
            clearCalculatedValues();
            return;
        }

        try {
            double grams = Double.parseDouble(quantityText.trim());

            if (grams <= 0) {
                clearCalculatedValues();
                showMessage("Quantity must be greater than 0.", true);
                return;
            }

            calculatedCalories = selectedFood.getCaloriesPer100g() * grams / 100.0;
            calculatedProtein = selectedFood.getProteinPer100g() * grams / 100.0;
            calculatedCarbs = selectedFood.getCarbsPer100g() * grams / 100.0;
            calculatedFats = selectedFood.getFatPer100g() * grams / 100.0;

            calculatedCaloriesLabel.setText(String.valueOf(Math.round(calculatedCalories)));
            calculatedProteinLabel.setText(String.valueOf(Math.round(calculatedProtein)));
            calculatedCarbsLabel.setText(String.valueOf(Math.round(calculatedCarbs)));
            calculatedFatsLabel.setText(String.valueOf(Math.round(calculatedFats)));

            messageLabel.setText("");

        } catch (NumberFormatException e) {
            clearCalculatedValues();
            showMessage("Please enter a valid quantity in grams.", true);
        }
    }

    private void clearCalculatedValues() {
        calculatedCalories = 0;
        calculatedProtein = 0;
        calculatedCarbs = 0;
        calculatedFats = 0;

        calculatedCaloriesLabel.setText("0");
        calculatedProteinLabel.setText("0");
        calculatedCarbsLabel.setText("0");
        calculatedFatsLabel.setText("0");
    }

    @FXML
    private void saveFood() {
        FoodItem selectedFood = foodItemBox.getValue();
        String mealType = mealTypeBox.getValue();
        String quantityText = quantityField.getText();

        if (selectedFood == null) {
            showMessage("Please select a food item.", true);
            return;
        }

        if (mealType == null || mealType.trim().isEmpty()) {
            showMessage("Please select a meal type.", true);
            return;
        }

        if (quantityText == null || quantityText.trim().isEmpty()) {
            showMessage("Please enter quantity in grams.", true);
            return;
        }

        try {
            double grams = Double.parseDouble(quantityText.trim());

            if (grams <= 0) {
                showMessage("Quantity must be greater than 0.", true);
                return;
            }

            calculateNutrition();

            com.health.app.dao.MealDAO mealDAO = new com.health.app.dao.MealDAO();
            int userId = 0;
            if (com.health.app.AppSession.getCurrentUser() != null) {
                userId = com.health.app.AppSession.getCurrentUser().getUserId();
            }
            boolean saved = mealDAO.saveMealLog(
                    userId,
                    selectedFood.getFoodId(),
                    mealType,
                    grams,
                    calculatedCalories,
                    calculatedProtein,
                    calculatedCarbs,
                    calculatedFats
            );

            if (saved) {
                showMessage("Saved! " + selectedFood.getName() + " (" + (int) grams + "g) logged.", false);
                foodItemBox.setValue(null);
                mealTypeBox.setValue(null);
                quantityField.clear();
                clearCalculatedValues();
            } else {
                showMessage("Failed to save. Check database.", true);
            }

        } catch (NumberFormatException e) {
            showMessage("Please enter a valid quantity.", true);
        }
    }

    @FXML
    private void backToMeals() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/health/app/view/meals.fxml"));
            Parent mealsRoot = loader.load();

            Scene scene = foodItemBox.getScene();
            scene.setRoot(mealsRoot);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showMessage(String message, boolean isError) {
        messageLabel.setText(message);

        if (isError) {
            messageLabel.setStyle("-fx-text-fill: #FF6B6B; -fx-font-size: 13px; -fx-font-weight: bold;");
        } else {
            messageLabel.setStyle("-fx-text-fill: #43D18D; -fx-font-size: 13px; -fx-font-weight: bold;");
        }
    }
}