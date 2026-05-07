package com.health.app.service;

import com.health.app.model.User;

public class NutritionService {

    public static class NutritionTargets {
        private final double calories;
        private final double protein;
        private final double carbs;
        private final double fats;

        public NutritionTargets(double calories, double protein, double carbs, double fats) {
            this.calories = calories;
            this.protein = protein;
            this.carbs = carbs;
            this.fats = fats;
        }

        public double getCalories() {
            return calories;
        }

        public double getProtein() {
            return protein;
        }

        public double getCarbs() {
            return carbs;
        }

        public double getFats() {
            return fats;
        }
    }

    public NutritionTargets calculateTargets(User user) {
        double bmr = calculateBMR(user);
        double maintenanceCalories = bmr * getActivityMultiplier(user.getActivityLevel());
        double targetCalories = maintenanceCalories + getGoalAdjustment(user);

        if (targetCalories < 1200) {
            targetCalories = 1200;
        }

        double protein = calculateProtein(user);
        double fats = (targetCalories * 0.25) / 9;
        double carbs = (targetCalories - (protein * 4) - (fats * 9)) / 4;

        if (carbs < 0) {
            carbs = 0;
        }

        return new NutritionTargets(
                Math.round(targetCalories),
                Math.round(protein),
                Math.round(carbs),
                Math.round(fats)
        );
    }

    private double calculateBMR(User user) {
        double weight = user.getWeight();
        double height = user.getHeight();
        int age = user.getAge();

        if ("Female".equalsIgnoreCase(user.getGender())) {
            return 10 * weight + 6.25 * height - 5 * age - 161;
        }

        return 10 * weight + 6.25 * height - 5 * age + 5;
    }

    private double getActivityMultiplier(String activityLevel) {
        if (activityLevel == null) {
            return 1.2;
        }

        switch (activityLevel) {
            case "Not very active":
                return 1.2;
            case "Lightly active":
                return 1.375;
            case "Active":
                return 1.55;
            case "Very active":
                return 1.725;
            default:
                return 1.2;
        }
    }

    private double getGoalAdjustment(User user) {
        String goal = user.getFitnessGoal();

        if (goal == null) {
            return 0;
        }

        if ("Lose weight".equalsIgnoreCase(goal)) {
            return -getWeeklyDeficit(user.getWeeklyGoal());
        }

        if ("Gain muscle".equalsIgnoreCase(goal)) {
            return 300;
        }

        return 0;
    }

    private double getWeeklyDeficit(String weeklyGoal) {
        if (weeklyGoal == null) {
            return 250;
        }

        switch (weeklyGoal) {
            case "0.2 kg per week":
                return 220;
            case "0.5 kg per week":
                return 550;
            case "0.8 kg per week":
                return 880;
            case "1.0 kg per week":
                return 1100;
            default:
                return 250;
        }
    }

    private double calculateProtein(User user) {
        String goal = user.getFitnessGoal();
        double weight = user.getWeight();

        if ("Gain muscle".equalsIgnoreCase(goal)) {
            return weight * 2.0;
        }

        if ("Lose weight".equalsIgnoreCase(goal)) {
            return weight * 1.8;
        }

        return weight * 1.6;
    }
}