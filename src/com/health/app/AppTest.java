
package com.health.app;

import com.health.app.dao.FoodItemDAO;
import com.health.app.dao.MealDAO;
import com.health.app.dao.WorkoutDAO;
import com.health.app.model.FoodItem;
import com.health.app.model.User;
import com.health.app.model.WorkoutPlan;
import com.health.app.service.NutritionService;
import com.health.app.service.UserService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class AppTest {

    // =========================================================================
    //  UNIT TEST 1 — User Model
    // =========================================================================
    @Test
    public void testUserModel() {
        User user = new User();
        user.setName("Abdulla");
        user.setAge(22);
        user.setGender("Male");
        user.setHeight(175);
        user.setWeight(70);
        user.setFitnessGoal("Lose weight");
        user.setActivityLevel("Lightly active");

        assertEquals("Abdulla", user.getName());
        assertEquals(22, user.getAge());
        assertEquals("Male", user.getGender());
        assertEquals(175, user.getHeight(), 0.01);
        assertEquals(70, user.getWeight(), 0.01);
        assertEquals("Lose weight", user.getFitnessGoal());
        assertEquals("Lightly active", user.getActivityLevel());
    }

    // =========================================================================
    //  UNIT TEST 2 — Nutrition Service BMR Calculation
    // =========================================================================
    @Test
    public void testNutritionServiceCalculation() {
        NutritionService service = new NutritionService();

        User user = new User();
        user.setAge(25);
        user.setGender("Male");
        user.setHeight(175);
        user.setWeight(75);
        user.setFitnessGoal("Lose weight");
        user.setActivityLevel("Lightly active");
        user.setWeeklyGoal("0.5 kg per week");

        NutritionService.NutritionTargets targets = service.calculateTargets(user);

        assertTrue(targets.getCalories() >= 1200);
        assertTrue(targets.getProtein() > 0);
        assertTrue(targets.getCarbs() > 0);
        assertTrue(targets.getFats() > 0);
    }

    // =========================================================================
    //  UNIT TEST 3 — Nutrition targets for female user
    // =========================================================================
    @Test
    public void testNutritionServiceFemale() {
        NutritionService service = new NutritionService();

        User user = new User();
        user.setAge(30);
        user.setGender("Female");
        user.setHeight(165);
        user.setWeight(60);
        user.setFitnessGoal("Maintain weight");
        user.setActivityLevel("Active");

        NutritionService.NutritionTargets targets = service.calculateTargets(user);

        assertTrue(targets.getCalories() >= 1200);
        assertTrue(targets.getProtein() > 0);
    }

    // =========================================================================
    //  UNIT TEST 4 — Nutrition minimum calories enforced
    // =========================================================================
    @Test
    public void testNutritionMinimumCalories() {
        NutritionService service = new NutritionService();

        User user = new User();
        user.setAge(25);
        user.setGender("Female");
        user.setHeight(150);
        user.setWeight(45);
        user.setFitnessGoal("Lose weight");
        user.setActivityLevel("Not very active");
        user.setWeeklyGoal("1.0 kg per week");

        NutritionService.NutritionTargets targets = service.calculateTargets(user);

        // Should never go below 1200 kcal
        assertTrue(targets.getCalories() >= 1200);
    }

    // =========================================================================
    //  UNIT TEST 5 — FoodItemDAO get all items
    // =========================================================================
    @Test
    public void testFoodItemDAOGetAll() {
        FoodItemDAO dao = new FoodItemDAO();
        List<FoodItem> items = dao.getAllFoodItems();

        assertNotNull(items);
        assertTrue(items.size() > 0);
    }

    // =========================================================================
    //  UNIT TEST 6 — FoodItemDAO search by name
    // =========================================================================
    @Test
    public void testFoodItemDAOSearch() {
        FoodItemDAO dao = new FoodItemDAO();
        List<FoodItem> items = dao.getAllFoodItems();

        if (!items.isEmpty()) {
            String firstName = items.get(0).getName();
            String keyword = firstName.substring(0, 3);
            FoodItem found = dao.searchFoodByName(keyword);
            assertNotNull(found);
        }
    }

    // =========================================================================
    //  UNIT TEST 7 — FoodItemDAO get by ID
    // =========================================================================
    @Test
    public void testFoodItemDAOGetById() {
        FoodItemDAO dao = new FoodItemDAO();
        List<FoodItem> items = dao.getAllFoodItems();

        if (!items.isEmpty()) {
            int id = items.get(0).getFoodId();
            FoodItem found = dao.getFoodItemById(id);
            assertNotNull(found);
            assertEquals(id, found.getFoodId());
        }
    }

    // =========================================================================
    //  UNIT TEST 8 — MealDAO getTodayTotals
    // =========================================================================
    @Test
    public void testMealDAOGetTodayTotals() {
        MealDAO dao = new MealDAO();
        double[] totals = dao.getTodayTotals(1);

        assertNotNull(totals);
        assertEquals(4, totals.length);
        assertTrue(totals[0] >= 0); // calories
        assertTrue(totals[1] >= 0); // protein
        assertTrue(totals[2] >= 0); // carbs
        assertTrue(totals[3] >= 0); // fats
    }

    // =========================================================================
    //  UNIT TEST 9 — MealDAO getMealTypeCalories
    // =========================================================================
    @Test
    public void testMealDAOGetMealTypeCalories() {
        MealDAO dao = new MealDAO();
        double breakfast = dao.getMealTypeCalories(1, "Breakfast");
        double lunch     = dao.getMealTypeCalories(1, "Lunch");
        double dinner    = dao.getMealTypeCalories(1, "Dinner");
        double snack     = dao.getMealTypeCalories(1, "Snack");

        assertTrue(breakfast >= 0);
        assertTrue(lunch >= 0);
        assertTrue(dinner >= 0);
        assertTrue(snack >= 0);
    }

    // =========================================================================
    //  UNIT TEST 10 — UserService invalid login
    // =========================================================================
    @Test
    public void testUserServiceInvalidLogin() {
        UserService service = new UserService();
        User result = service.login("invalid@test.com", "wrongpassword");
        assertNull(result);
    }

    // =========================================================================
    //  UNIT TEST 11 — WorkoutDAO get plans
    // =========================================================================
    @Test
    public void testWorkoutDAOGetPlans() {
        WorkoutDAO dao = new WorkoutDAO();
        List<WorkoutPlan> plans = dao.getPlansForUser(1);
        assertNotNull(plans);
    }

    // =========================================================================
    //  UNIT TEST 12 — WorkoutDAO session count
    // =========================================================================
    @Test
    public void testWorkoutDAOSessionCount() {
        WorkoutDAO dao = new WorkoutDAO();
        int count = dao.getSessionCount(1);
        assertTrue(count >= 0);
    }

    // =========================================================================
    //  INTEGRATION TEST 1 — Login + Nutrition targets
    // =========================================================================
    @Test
    public void testLoginAndNutritionIntegration() {
        UserService userService = new UserService();
        NutritionService nutritionService = new NutritionService();

        // Test with invalid credentials first
        User invalidUser = userService.login("wrong@email.com", "wrongpass");
        assertNull(invalidUser);

        // If you have a real user, test with real credentials
        // User validUser = userService.login("real@email.com", "realpass");
        // if (validUser != null) {
        //     NutritionService.NutritionTargets t = nutritionService.calculateTargets(validUser);
        //     assertTrue(t.getCalories() > 0);
        // }
    }

    // =========================================================================
    //  INTEGRATION TEST 2 — Food search + Meal logging
    // =========================================================================
    @Test
    public void testFoodSearchAndMealLoggingIntegration() {
        FoodItemDAO foodDAO = new FoodItemDAO();
        MealDAO mealDAO = new MealDAO();

        List<FoodItem> foods = foodDAO.getAllFoodItems();
        assertNotNull(foods);

        if (!foods.isEmpty()) {
            FoodItem food = foods.get(0);

            double grams    = 100;
            double calories = food.getCaloriesPer100g() * grams / 100;
            double protein  = food.getProteinPer100g()  * grams / 100;
            double carbs    = food.getCarbsPer100g()     * grams / 100;
            double fats     = food.getFatPer100g()       * grams / 100;

            assertTrue(calories > 0);

            boolean saved = mealDAO.saveMealLog(
                    1, food.getFoodId(), "Breakfast",
                    grams, calories, protein, carbs, fats
            );

            assertTrue(saved);
        }
    }

    // =========================================================================
    //  INTEGRATION TEST 3 — Workout plan creation + exercise add
    // =========================================================================
    @Test
    public void testWorkoutPlanAndExerciseIntegration() {
        WorkoutDAO dao = new WorkoutDAO();

        // Create a test plan
        boolean created = dao.createPlan(1, "Test Plan JUnit");
        assertTrue(created);

        // Get the last plan
        int planId = dao.getLastPlanId(1);
        assertTrue(planId > 0);

        // Add exercise to it
        boolean added = dao.addExercise(planId, "Push Up", 3, 10, 60);
        assertTrue(added);

        // Verify exercises exist
        List<com.health.app.model.Exercise> exercises = dao.getExercisesForPlan(planId);
        assertNotNull(exercises);
        assertTrue(exercises.size() > 0);
    }
}