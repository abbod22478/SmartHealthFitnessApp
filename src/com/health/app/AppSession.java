package com.health.app;

import com.health.app.model.User;

public class AppSession {

    private static User currentUser;

    private static double targetCalories;
    private static double targetProtein;
    private static double targetCarbs;
    private static double targetFats;

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static double getTargetCalories() {
        return targetCalories;
    }

    public static void setTargetCalories(double targetCalories) {
        AppSession.targetCalories = targetCalories;
    }

    public static double getTargetProtein() {
        return targetProtein;
    }

    public static void setTargetProtein(double targetProtein) {
        AppSession.targetProtein = targetProtein;
    }

    public static double getTargetCarbs() {
        return targetCarbs;
    }

    public static void setTargetCarbs(double targetCarbs) {
        AppSession.targetCarbs = targetCarbs;
    }

    public static double getTargetFats() {
        return targetFats;
    }

    public static void setTargetFats(double targetFats) {
        AppSession.targetFats = targetFats;
    }
}