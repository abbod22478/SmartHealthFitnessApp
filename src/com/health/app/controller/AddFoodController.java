package com.health.app.controller;

import com.health.app.AppSession;
import com.health.app.dao.FoodItemDAO;
import com.health.app.dao.MealDAO;
import com.health.app.model.FoodItem;
import com.health.app.service.AIService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.List;

public class AddFoodController {

    @FXML private TextField textInputField;
    @FXML private Label textParseResultLabel;
    @FXML private ComboBox<FoodItem> foodItemBox;
    @FXML private ComboBox<String> mealTypeBox;
    @FXML private TextField quantityField;

    @FXML private Label calculatedCaloriesLabel;
    @FXML private Label calculatedProteinLabel;
    @FXML private Label calculatedCarbsLabel;
    @FXML private Label calculatedFatsLabel;
    @FXML private Label messageLabel;

    private final FoodItemDAO foodItemDAO = new FoodItemDAO();
    private final MealDAO mealDAO = new MealDAO();

    private double calculatedCalories = 0;
    private double calculatedProtein  = 0;
    private double calculatedCarbs    = 0;
    private double calculatedFats     = 0;

    // Cooking adjectives to strip before DB search
    private static final String[] COOKING_WORDS = {
            "grilled", "baked", "fried", "boiled", "steamed", "roasted",
            "smoked", "raw", "fresh", "cooked", "scrambled", "poached",
            "mixed", "chopped", "sliced", "diced", "mashed", "whole",
            "low-fat", "low fat", "fat-free", "fat free", "skimmed",
            "organic", "plain", "simple", "homemade", "extra"
    };

    @FXML
    public void initialize() {
        loadMealTypes();
        loadFoodItems();
        foodItemBox.setOnAction(event -> calculateNutrition());
        quantityField.textProperty().addListener(
                (obs, oldVal, newVal) -> calculateNutrition());
    }

    private void loadMealTypes() {
        mealTypeBox.setItems(FXCollections.observableArrayList(
                "Breakfast", "Lunch", "Dinner", "Snack"
        ));
    }

    private void loadFoodItems() {
        List<FoodItem> items = foodItemDAO.getAllFoodItems();
        foodItemBox.setItems(FXCollections.observableArrayList(items));
        if (items.isEmpty()) showMessage("No food items found in database.", true);
    }

    private void calculateNutrition() {
        FoodItem food = foodItemBox.getValue();
        if (food == null) { clearCalculatedValues(); return; }

        String qty = quantityField.getText();
        if (qty == null || qty.trim().isEmpty()) { clearCalculatedValues(); return; }

        try {
            double grams = Double.parseDouble(qty.trim());
            if (grams <= 0) {
                clearCalculatedValues();
                showMessage("Quantity must be greater than 0.", true);
                return;
            }

            calculatedCalories = food.getCaloriesPer100g() * grams / 100.0;
            calculatedProtein  = food.getProteinPer100g()  * grams / 100.0;
            calculatedCarbs    = food.getCarbsPer100g()    * grams / 100.0;
            calculatedFats     = food.getFatPer100g()      * grams / 100.0;

            calculatedCaloriesLabel.setText(String.valueOf(Math.round(calculatedCalories)));
            calculatedProteinLabel .setText(String.valueOf(Math.round(calculatedProtein)));
            calculatedCarbsLabel   .setText(String.valueOf(Math.round(calculatedCarbs)));
            calculatedFatsLabel    .setText(String.valueOf(Math.round(calculatedFats)));
            messageLabel.setText("");

        } catch (NumberFormatException e) {
            clearCalculatedValues();
            showMessage("Please enter a valid quantity.", true);
        }
    }

    private void clearCalculatedValues() {
        calculatedCalories = 0; calculatedProtein = 0;
        calculatedCarbs    = 0; calculatedFats    = 0;
        calculatedCaloriesLabel.setText("0");
        calculatedProteinLabel .setText("0");
        calculatedCarbsLabel   .setText("0");
        calculatedFatsLabel    .setText("0");
    }

    // =========================================================================
    //  SAVE (manual dropdown flow)
    // =========================================================================

    @FXML
    private void saveFood() {
        FoodItem selectedFood = foodItemBox.getValue();
        String mealType       = mealTypeBox.getValue();
        String quantityText   = quantityField.getText();

        if (selectedFood == null) {
            showMessage("Please select a food item.", true); return;
        }
        if (mealType == null || mealType.trim().isEmpty()) {
            showMessage("Please select a meal type.", true); return;
        }
        if (quantityText == null || quantityText.trim().isEmpty()) {
            showMessage("Please enter quantity in grams.", true); return;
        }

        try {
            double grams = Double.parseDouble(quantityText.trim());
            if (grams <= 0) {
                showMessage("Quantity must be greater than 0.", true); return;
            }

            calculateNutrition();
            boolean saved = saveMealLog(selectedFood, mealType, grams);

            if (saved) {
                showMessage("Saved! " + selectedFood.getName()
                        + " (" + (int) grams + "g) logged.", false);
                resetForm();
            } else {
                showMessage("Failed to save. Check database.", true);
            }

        } catch (NumberFormatException e) {
            showMessage("Please enter a valid quantity.", true);
        }
    }

    // =========================================================================
    //  AI-POWERED MEAL TEXT PARSING (FR18-FR21)
    // =========================================================================

    @FXML
    private void parseTextInput() {
        String input = textInputField.getText().trim();

        if (input.isEmpty()) {
            showMessage("Please enter a meal description.", true);
            return;
        }

        textParseResultLabel.setText("Analysing with AI...");
        textParseResultLabel.setStyle("-fx-text-fill: #FF8A3D; -fx-font-size: 12px;");
        showMessage("", false);

        Thread aiThread = new Thread(() -> {
            AIService.ParsedMeal parsed = AIService.parseMealText(input);

            Platform.runLater(() -> {
                if (!parsed.isSuccess()) {
                    textParseResultLabel.setText("Could not parse: " + parsed.getErrorMessage());
                    textParseResultLabel.setStyle("-fx-text-fill: #FF6B6B; -fx-font-size: 12px;");
                    showMessage("Try selecting food manually.", true);
                    return;
                }

                String rawKeyword = parsed.getFoodKeyword();
                double grams      = parsed.getGrams();

                // Step 1: search DB with original keyword
                FoodItem found = foodItemDAO.searchFoodByName(rawKeyword);

                // Step 2: strip cooking words and retry
                if (found == null) {
                    String cleaned = cleanFoodKeyword(rawKeyword);
                    if (!cleaned.equals(rawKeyword)) {
                        found = foodItemDAO.searchFoodByName(cleaned);
                    }
                }

                // Step 3: try each word individually
                if (found == null) {
                    for (String word : rawKeyword.split("\\s+")) {
                        if (word.length() > 2) {
                            found = foodItemDAO.searchFoodByName(word);
                            if (found != null) break;
                        }
                    }
                }

                // Step 4: food not in DB → insert it using AI nutrition data
                if (found == null) {
                    if (parsed.getCaloriesPer100g() > 0) {
                        // AI returned valid nutrition — insert into food_items
                        found = foodItemDAO.insertFoodItem(
                                rawKeyword,
                                parsed.getCaloriesPer100g(),
                                parsed.getProteinPer100g(),
                                parsed.getCarbsPer100g(),
                                parsed.getFatsPer100g()
                        );

                        if (found != null) {
                            // Reload dropdown so new food appears
                            loadFoodItems();
                            textParseResultLabel.setText(
                                    "AI added \"" + rawKeyword + "\" to database!");
                            textParseResultLabel.setStyle(
                                    "-fx-text-fill: #FF8A3D; -fx-font-size: 12px;");
                        }
                    }
                }

                if (found == null) {
                    // Could not find or insert
                    textParseResultLabel.setText(
                            "Could not find \"" + rawKeyword + "\". Try selecting manually.");
                    textParseResultLabel.setStyle(
                            "-fx-text-fill: #FF6B6B; -fx-font-size: 12px;");
                    showMessage("Food not found. Select manually.", true);
                    return;
                }

                // Step 5: calculate nutrition for the given grams
                double cal  = found.getCaloriesPer100g() * grams / 100.0;
                double pro  = found.getProteinPer100g()  * grams / 100.0;
                double carb = found.getCarbsPer100g()    * grams / 100.0;
                double fat  = found.getFatPer100g()      * grams / 100.0;

                // Step 6: auto-log if meal type is already selected
                String mealType = mealTypeBox.getValue();
                if (mealType != null && !mealType.trim().isEmpty()) {
                    int userId = AppSession.getCurrentUser() != null
                            ? AppSession.getCurrentUser().getUserId() : 0;

                    boolean saved = mealDAO.saveMealLog(
                            userId, found.getFoodId(), mealType,
                            grams, cal, pro, carb, fat
                    );

                    if (saved) {
                        textParseResultLabel.setText(
                                "✓ Logged: " + found.getName() + " " + (int) grams + "g → " + mealType);
                        textParseResultLabel.setStyle(
                                "-fx-text-fill: #43D18D; -fx-font-size: 12px;");
                        showMessage("Meal logged successfully!", false);
                        resetForm();
                        return;
                    }
                }

                // Step 7: meal type not selected — populate form for user to confirm
                foodItemBox.setValue(found);
                quantityField.setText(String.valueOf((int) grams));
                calculatedCaloriesLabel.setText(String.valueOf(Math.round(cal)));
                calculatedProteinLabel .setText(String.valueOf(Math.round(pro)));
                calculatedCarbsLabel   .setText(String.valueOf(Math.round(carb)));
                calculatedFatsLabel    .setText(String.valueOf(Math.round(fat)));

                calculatedCalories = cal;
                calculatedProtein  = pro;
                calculatedCarbs    = carb;
                calculatedFats     = fat;

                textParseResultLabel.setText(
                        "AI found: " + found.getName() + " — " + (int) grams + "g");
                textParseResultLabel.setStyle(
                        "-fx-text-fill: #43D18D; -fx-font-size: 12px;");
                showMessage("Select meal type and tap Save!", false);
            });
        });

        aiThread.setDaemon(true);
        aiThread.start();
    }

    // =========================================================================
    //  HELPERS
    // =========================================================================

    private boolean saveMealLog(FoodItem food, String mealType, double grams) {
        int userId = AppSession.getCurrentUser() != null
                ? AppSession.getCurrentUser().getUserId() : 0;

        return mealDAO.saveMealLog(
                userId, food.getFoodId(), mealType, grams,
                calculatedCalories, calculatedProtein, calculatedCarbs, calculatedFats
        );
    }

    private void resetForm() {
        foodItemBox.setValue(null);
        mealTypeBox.setValue(null);
        quantityField.clear();
        textInputField.clear();
        textParseResultLabel.setText("");
        clearCalculatedValues();
    }

    private String cleanFoodKeyword(String keyword) {
        String cleaned = keyword.toLowerCase().trim();
        for (String word : COOKING_WORDS) {
            cleaned = cleaned.replace(word + " ", "").replace(" " + word, "");
        }
        return cleaned.trim();
    }

    private void showMessage(String message, boolean isError) {
        messageLabel.setText(message);
        messageLabel.setStyle(isError
                ? "-fx-text-fill: #FF6B6B; -fx-font-size: 13px; -fx-font-weight: bold;"
                : "-fx-text-fill: #43D18D; -fx-font-size: 13px; -fx-font-weight: bold;");
    }

    @FXML
    private void backToMeals() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/health/app/view/meals.fxml"));
            Parent mealsRoot = loader.load();
            foodItemBox.getScene().setRoot(mealsRoot);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}