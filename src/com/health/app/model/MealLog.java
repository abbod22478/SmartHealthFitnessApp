package com.health.app.model;

public class MealLog {

    private int logId;
    private int userId;
    private int foodId;
    private String mealType;
    private double quantityGrams;
    private double totalCalories;
    private double totalProtein;
    private double totalCarbs;
    private double totalFats;
    private String logDate;

    public MealLog() {

    }

    public MealLog(int logId, int userId, int foodId, String mealType,
                   double quantityGrams, double totalCalories,
                   double totalProtein, double totalCarbs,
                   double totalFats, String logDate) {
        this.logId         = logId;
        this.userId        = userId;
        this.foodId        = foodId;
        this.mealType      = mealType;
        this.quantityGrams = quantityGrams;
        this.totalCalories = totalCalories;
        this.totalProtein  = totalProtein;
        this.totalCarbs    = totalCarbs;
        this.totalFats     = totalFats;
        this.logDate       = logDate;
    }

    public int getLogId()
    { return logId;            }
    public void setLogId(int id)
    { this.logId = id;         }

    public int getUserId()
    { return userId;           }
    public void setUserId(int id)
    { this.userId = id;        }

    public int getFoodId()
    { return foodId;           }
    public void setFoodId(int id)
    { this.foodId = id;        }

    public String getMealType()
    { return mealType;         }
    public void setMealType(String type)
    { this.mealType = type;    }

    public double getQuantityGrams()
    { return quantityGrams;    }
    public void setQuantityGrams(double g)
    { this.quantityGrams = g;  }

    public double getTotalCalories()
    { return totalCalories;    }
    public void setTotalCalories(double cal)
    { this.totalCalories = cal;}

    public double getTotalProtein()
    { return totalProtein;     }
    public void setTotalProtein(double p)
    { this.totalProtein = p;   }

    public double getTotalCarbs()
    { return totalCarbs;       }
    public void setTotalCarbs(double c)
    { this.totalCarbs = c;     }

    public double getTotalFats()
    { return totalFats;        }
    public void setTotalFats(double f)
    { this.totalFats = f;      }

    public String getLogDate()
    { return logDate;          }
    public void setLogDate(String date)
    { this.logDate = date;     }

    @Override
    public String toString() {
        return mealType + " - " + (int) totalCalories + " kcal (" + logDate + ")";
    }
}