package com.health.app.dao;

import com.health.app.database.DBConnection;
import com.health.app.model.FoodItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class FoodItemDAO {

    public List<FoodItem> getAllFoodItems() {
        List<FoodItem> foodItems = new ArrayList<>();

        String sql = "SELECT food_id, name, calories_per_100g, protein_per_100g, " +
                "carbs_per_100g, fats_per_100g FROM food_items ORDER BY name";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                foodItems.add(new FoodItem(
                        rs.getInt("food_id"),
                        rs.getString("name"),
                        rs.getDouble("calories_per_100g"),
                        rs.getDouble("protein_per_100g"),
                        rs.getDouble("carbs_per_100g"),
                        rs.getDouble("fats_per_100g")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return foodItems;
    }

    public FoodItem getFoodItemById(int foodId) {
        String sql = "SELECT food_id, name, calories_per_100g, protein_per_100g, " +
                "carbs_per_100g, fats_per_100g FROM food_items WHERE food_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, foodId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new FoodItem(
                        rs.getInt("food_id"),
                        rs.getString("name"),
                        rs.getDouble("calories_per_100g"),
                        rs.getDouble("protein_per_100g"),
                        rs.getDouble("carbs_per_100g"),
                        rs.getDouble("fats_per_100g")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public FoodItem searchFoodByName(String keyword) {
        String sql = "SELECT food_id, name, calories_per_100g, protein_per_100g, " +
                "carbs_per_100g, fats_per_100g FROM food_items " +
                "WHERE LOWER(name) LIKE LOWER(?) LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + keyword.trim() + "%");
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new FoodItem(
                        rs.getInt("food_id"),
                        rs.getString("name"),
                        rs.getDouble("calories_per_100g"),
                        rs.getDouble("protein_per_100g"),
                        rs.getDouble("carbs_per_100g"),
                        rs.getDouble("fats_per_100g")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Inserts a new food item into the database (from AI nutrition data).
     * Returns the newly created FoodItem with its generated food_id,
     * or null if the insert failed.
     */
    public FoodItem insertFoodItem(String name, double caloriesPer100g,
                                   double proteinPer100g, double carbsPer100g,
                                   double fatsPer100g) {

        String sql = "INSERT INTO food_items " +
                "(name, calories_per_100g, protein_per_100g, carbs_per_100g, fats_per_100g) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, name);
            stmt.setDouble(2, caloriesPer100g);
            stmt.setDouble(3, proteinPer100g);
            stmt.setDouble(4, carbsPer100g);
            stmt.setDouble(5, fatsPer100g);

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    int newId = keys.getInt(1);
                    System.out.println("[FoodItemDAO] Inserted new food: "
                            + name + " (id=" + newId + ")");
                    return new FoodItem(newId, name, caloriesPer100g,
                            proteinPer100g, carbsPer100g, fatsPer100g);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}