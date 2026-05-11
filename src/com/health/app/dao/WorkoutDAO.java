package com.health.app.dao;

import com.health.app.database.DBConnection;
import com.health.app.model.Exercise;
import com.health.app.model.WorkoutPlan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WorkoutDAO {

    // ─── Workout Plans ───────────────────────────────────────────

    public boolean createPlan(int userId, String planName) {
        String sql = "INSERT INTO workout_plans (user_id, plan_name) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, planName);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getLastPlanId(int userId) {
        String sql = "SELECT plan_id FROM workout_plans WHERE user_id = ? ORDER BY plan_id DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("plan_id");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public List<WorkoutPlan> getPlansForUser(int userId) {
        List<WorkoutPlan> plans = new ArrayList<>();
        String sql = "SELECT plan_id, plan_name FROM workout_plans WHERE user_id = ? ORDER BY plan_id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                WorkoutPlan plan = new WorkoutPlan();
                plan.setPlanId(rs.getInt("plan_id"));
                plan.setPlanName(rs.getString("plan_name"));
                plans.add(plan);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return plans;
    }

    public boolean deletePlan(int planId) {
        try (Connection conn = DBConnection.getConnection()) {
            // Delete exercises first
            PreparedStatement s1 = conn.prepareStatement("DELETE FROM exercises WHERE plan_id = ?");
            s1.setInt(1, planId);
            s1.executeUpdate();
            // Delete plan
            PreparedStatement s2 = conn.prepareStatement("DELETE FROM workout_plans WHERE plan_id = ?");
            s2.setInt(1, planId);
            return s2.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ─── Exercises ────────────────────────────────────────────────

    public boolean addExercise(int planId, String name, int sets, int reps, int restSecs) {
        String sql = "INSERT INTO exercises (plan_id, name, sets, repetitions, rest_interval_secs) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, planId);
            stmt.setString(2, name);
            stmt.setInt(3, sets);
            stmt.setInt(4, reps);
            stmt.setInt(5, restSecs);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Exercise> getExercisesForPlan(int planId) {
        List<Exercise> exercises = new ArrayList<>();
        String sql = "SELECT exercise_id, name, sets, repetitions, rest_interval_secs FROM exercises WHERE plan_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, planId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Exercise ex = new Exercise();
                ex.setExerciseId(rs.getInt("exercise_id"));
                ex.setName(rs.getString("name"));
                ex.setSets(rs.getInt("sets"));
                ex.setRepetitions(rs.getInt("repetitions"));
                ex.setRestIntervalSecs(rs.getInt("rest_interval_secs"));
                exercises.add(ex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exercises;
    }

    // ─── Sessions ─────────────────────────────────────────────────

    public boolean saveSession(int userId, int planId) {
        String sql = "INSERT INTO workout_sessions (user_id, plan_id, session_date, status) VALUES (?, ?, CURRENT_DATE, 'completed')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, planId);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getSessionCount(int userId) {
        String sql = "SELECT COUNT(*) FROM workout_sessions WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}