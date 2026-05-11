package com.health.app.service;

import com.health.app.dao.MealDAO;
import com.health.app.model.FoodItem;

import java.util.List;

public class MealService {

    private final MealDAO mealDAO = new MealDAO();

    public boolean logMeal(int userId, int foodId, String mealType,
                           double grams, double calories,
                           double protein, double carbs, double fats) {
        return mealDAO.saveMealLog(userId, foodId, mealType,
                grams, calories, protein, carbs, fats);
    }

    public double[] getTodayTotals(int userId) {
        return mealDAO.getTodayTotals(userId);
    }

    public double getMealTypeCalories(int userId, String mealType) {
        return mealDAO.getMealTypeCalories(userId, mealType);
    }

    public List<FoodItem> getRecommendedFoods(double remaining, String goal) {
        return mealDAO.getRecommendedFoods(remaining, goal);
    }
}