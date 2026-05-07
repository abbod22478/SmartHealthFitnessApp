package com.health.app.model;

public class User {

    private int userId;
    private String name;
    private int age;
    private String gender;
    private double height;
    private double weight;
    private String credentials;
    private String fitnessGoal;
    private double targetWeight;
    private String activityLevel;
    private String weeklyGoal;
    private String foodPreference;
    private String mealPlanChoice;

    // Empty Constructor
    public User() {
    }

    // Full Constructor
    public User(int userId, String name, int age, String gender,
                double height, double weight, String credentials, String fitnessGoal) {
        this.userId = userId;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.credentials = credentials;
        this.fitnessGoal = fitnessGoal;
    }

    // Getters and Setters

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getCredentials() {
        return credentials;
    }

    public void setCredentials(String credentials) {
        this.credentials = credentials;
    }

    public String getFitnessGoal() {
        return fitnessGoal;
    }

    public void setFitnessGoal(String fitnessGoal) {
        this.fitnessGoal = fitnessGoal;
    }

    public double getTargetWeight() {
        return targetWeight;
    }

    public void setTargetWeight(double targetWeight) {
        this.targetWeight = targetWeight;
    }

    public String getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(String activityLevel) {
        this.activityLevel = activityLevel;
    }

    public String getWeeklyGoal() {
        return weeklyGoal;
    }

    public void setWeeklyGoal(String weeklyGoal) {
        this.weeklyGoal = weeklyGoal;
    }

    public String getFoodPreference() {
        return foodPreference;
    }

    public void setFoodPreference(String foodPreference) {
        this.foodPreference = foodPreference;
    }

    public String getMealPlanChoice() {
        return mealPlanChoice;
    }

    public void setMealPlanChoice(String mealPlanChoice) {
        this.mealPlanChoice = mealPlanChoice;
    }
}