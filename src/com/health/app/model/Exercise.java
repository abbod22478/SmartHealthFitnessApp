package com.health.app.model;

public class Exercise {

    private int exerciseId;
    private int planId;
    private String name;
    private int sets;
    private int repetitions;
    private int restIntervalSecs;
    private int completedSets;

    public Exercise() {}

    public int getExerciseId() { return exerciseId; }
    public void setExerciseId(int exerciseId) { this.exerciseId = exerciseId; }

    public int getPlanId() { return planId; }
    public void setPlanId(int planId) { this.planId = planId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getSets() { return sets; }
    public void setSets(int sets) { this.sets = sets; }

    public int getRepetitions() { return repetitions; }
    public void setRepetitions(int repetitions) { this.repetitions = repetitions; }

    public int getRestIntervalSecs() { return restIntervalSecs; }
    public void setRestIntervalSecs(int restIntervalSecs) { this.restIntervalSecs = restIntervalSecs; }

    public int getCompletedSets() { return completedSets; }
    public void setCompletedSets(int completedSets) { this.completedSets = completedSets; }

    public int getRemainingsets() { return sets - completedSets; }

    public boolean isCompleted() { return completedSets >= sets; }

    public void completeSet() {
        if (completedSets < sets) completedSets++;
    }

    @Override
    public String toString() { return name; }
}