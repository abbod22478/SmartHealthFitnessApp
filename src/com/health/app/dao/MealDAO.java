package com.health.app.dao;

import com.health.app.database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MealDAO {

    public boolean saveMealLog(int userId, int foodId, String mealType, double grams,
                               double calories, double protein,
                               double carbs, double fats) {

        String sql = "INSERT INTO meal_logs (user_id, food_id, text_input, quantity_grams, " +
                "total_calories, total_protein, total_carbs, total_fats, log_date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_DATE)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, foodId);
            stmt.setString(3, mealType);
            stmt.setDouble(4, grams);
            stmt.setDouble(5, calories);
            stmt.setDouble(6, protein);
            stmt.setDouble(7, carbs);
            stmt.setDouble(8, fats);

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public double[] getTodayTotals(int userId) {
        String sql = "SELECT " +
                "COALESCE(SUM(total_calories), 0) AS cal, " +
                "COALESCE(SUM(total_protein), 0) AS pro, " +
                "COALESCE(SUM(total_carbs), 0) AS carb, " +
                "COALESCE(SUM(total_fats), 0) AS fat " +
                "FROM meal_logs WHERE user_id = ? AND log_date = CURRENT_DATE";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new double[]{
                        rs.getDouble("cal"),
                        rs.getDouble("pro"),
                        rs.getDouble("carb"),
                        rs.getDouble("fat")
                };
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new double[]{0, 0, 0, 0};
    }

    public double getMealTypeCalories(int userId, String mealType) {
        String sql = "SELECT COALESCE(SUM(total_calories), 0) AS cal " +
                "FROM meal_logs WHERE user_id = ? AND text_input = ? AND log_date = CURRENT_DATE";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, mealType);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("cal");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }
}