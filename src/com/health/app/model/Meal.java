package com.health.app.model;

public class Meal {

    private int mealId;
    private int userId;
    private String mealType; // Breakfast, Lunch, Dinner, Snack
    private String logDate;

    public Meal() {

    }

    public Meal(int mealId, int userId, String mealType, String logDate) {
        this.mealId   = mealId;
        this.userId   = userId;
        this.mealType = mealType;
        this.logDate  = logDate;
    }

    public int getMealId()
    { return mealId;
    }
    public void setMealId(int id)
    { this.mealId = id;
    }

    public int getUserId()
    { return userId;
    }
    public void setUserId(int id)
    { this.userId = id;
    }

    public String getMealType()
    { return mealType;
    }
    public void setMealType(String type)
    { this.mealType = type;
    }

    public String getLogDate()              {
        return logDate;
    }
    public void setLogDate(String date)  { this.logDate = date;
    }

    @Override
    public String toString() {
        return mealType + " (" + logDate + ")";
    }
}