package com.health.app.model;

public class WorkoutPlan {

    private int planId;
    private String planName;
    private int userId;

    public WorkoutPlan() {}

    public WorkoutPlan(int planId, String planName, int userId) {
        this.planId = planId;
        this.planName = planName;
        this.userId = userId;
    }

    public int getPlanId() { return planId; }
    public void setPlanId(int planId) { this.planId = planId; }

    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    @Override
    public String toString() { return planName; }
}