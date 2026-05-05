package com.health.app.model;

import java.util.ArrayList;
import java.util.List;

public class OnboardingData {

    private String name;
    private String goal;
    private String foodPreference;
    private List<String> healthyHabits = new ArrayList<>();
    private String mealPlanChoice;
    private String activityLevel;
    private String gender;
    private int age;
    private double height;
    private double currentWeight;
    private double targetWeight;
    private String weeklyGoal;
    private String email;
    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getFoodPreference() {
        return foodPreference;
    }

    public void setFoodPreference(String foodPreference) {
        this.foodPreference = foodPreference;
    }

    public List<String> getHealthyHabits() {
        return healthyHabits;
    }

    public void setHealthyHabits(List<String> healthyHabits) {
        if (healthyHabits == null) {
            this.healthyHabits = new ArrayList<>();
        } else {
            this.healthyHabits = healthyHabits;
        }
    }

    public void addHabit(String habit) {
        if (habit != null && !healthyHabits.contains(habit)) {
            healthyHabits.add(habit);
        }
    }

    public void removeHabit(String habit) {
        healthyHabits.remove(habit);
    }

    public String getMealPlanChoice() {
        return mealPlanChoice;
    }

    public void setMealPlanChoice(String mealPlanChoice) {
        this.mealPlanChoice = mealPlanChoice;
    }

    public String getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(String activityLevel) {
        this.activityLevel = activityLevel;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getCurrentWeight() {
        return currentWeight;
    }

    public void setCurrentWeight(double currentWeight) {
        this.currentWeight = currentWeight;
    }

    public double getTargetWeight() {
        return targetWeight;
    }

    public void setTargetWeight(double targetWeight) {
        this.targetWeight = targetWeight;
    }

    public String getWeeklyGoal() {
        return weeklyGoal;
    }

    public void setWeeklyGoal(String weeklyGoal) {
        this.weeklyGoal = weeklyGoal;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}