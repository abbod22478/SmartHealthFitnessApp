package com.health.app.util;

import com.health.app.model.User;

public class CalorieCalculator {

    public static int getDailyTarget(User user) {
        double bmr;
        if ("Female".equalsIgnoreCase(user.getGender())) {
            bmr = 10 * user.getWeight() + 6.25 * user.getHeight() - 5 * user.getAge() - 161;
        } else {
            bmr = 10 * user.getWeight() + 6.25 * user.getHeight() - 5 * user.getAge() + 5;
        }
        double tdee = bmr * 1.55;
        String goal = user.getFitnessGoal();
        if (goal != null && goal.toLowerCase().contains("lose")) tdee -= 500;
        else if (goal != null && (goal.toLowerCase().contains("gain") || goal.toLowerCase().contains("muscle"))) tdee += 300;
        return Math.max(1200, (int) Math.round(tdee));
    }

    public static int getProteinGrams(int calories) {
        return (int) Math.round(calories * 0.30 / 4.0);
    }

    public static int getCarbGrams(int calories) {
        return (int) Math.round(calories * 0.40 / 4.0);
    }

    public static int getFatGrams(int calories) {
        return (int) Math.round(calories * 0.30 / 9.0);
    }
}
