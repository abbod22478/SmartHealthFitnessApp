package com.health.app;

import com.health.app.database.DBConnection;
import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        Connection con = DBConnection.getConnection();

        if (con != null) {
            System.out.println("Connected ✅");
        } else {
            System.out.println("Failed ❌");
        }
    }
}