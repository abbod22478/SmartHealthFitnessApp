package com.health.app.dao;

import com.health.app.database.DBConnection;
import com.health.app.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {

    public boolean addUser(User user, String password) {
        String sql = "INSERT INTO users " +
                "(name, email, password, age, gender, height, weight, target_weight, goal, activity_level, weekly_goal, food_preference, meal_plan_choice) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getCredentials());
            stmt.setString(3, password);
            stmt.setInt(4, user.getAge());
            stmt.setString(5, user.getGender());
            stmt.setDouble(6, user.getHeight());
            stmt.setDouble(7, user.getWeight());
            stmt.setDouble(8, user.getTargetWeight());
            stmt.setString(9, user.getFitnessGoal());
            stmt.setString(10, user.getActivityLevel());
            stmt.setString(11, user.getWeeklyGoal());
            stmt.setString(12, user.getFoodPreference());
            stmt.setString(13, user.getMealPlanChoice());

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getString("gender"),
                        rs.getDouble("height"),
                        rs.getDouble("weight"),
                        rs.getString("email"),
                        rs.getString("goal")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    public User getUserByEmailAndPassword(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User(
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getString("gender"),
                        rs.getDouble("height"),
                        rs.getDouble("weight"),
                        rs.getString("email"),
                        rs.getString("goal")
                );
                user.setTargetWeight(rs.getDouble("target_weight"));
                user.setActivityLevel(rs.getString("activity_level"));
                user.setWeeklyGoal(rs.getString("weekly_goal"));
                user.setFoodPreference(rs.getString("food_preference"));
                user.setMealPlanChoice(rs.getString("meal_plan_choice"));
                return user;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}