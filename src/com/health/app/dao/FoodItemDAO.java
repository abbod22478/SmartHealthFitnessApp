package com.health.app.dao;

import com.health.app.database.DBConnection;
import com.health.app.model.FoodItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class FoodItemDAO {

    public List<FoodItem> getAllFoodItems() {
        List<FoodItem> foodItems = new ArrayList<>();

        String sql = "SELECT food_id, name, calories_per_100g, protein_per_100g, carbs_per_100g, fats_per_100g " +
                "FROM food_items " +
                "ORDER BY name";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                FoodItem foodItem = new FoodItem(
                        rs.getInt("food_id"),
                        rs.getString("name"),
                        rs.getDouble("calories_per_100g"),
                        rs.getDouble("protein_per_100g"),
                        rs.getDouble("carbs_per_100g"),
                        rs.getDouble("fats_per_100g")
                );

                foodItems.add(foodItem);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return foodItems;
    }

    public FoodItem getFoodItemById(int foodId) {
        String sql = "SELECT food_id, name, calories_per_100g, protein_per_100g, carbs_per_100g, fats_per_100g " +
                "FROM food_items " +
                "WHERE food_id = ?";

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
}