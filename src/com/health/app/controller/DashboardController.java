package com.health.app.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DashboardController {

    @FXML
    private Label dashboardNameLabel;

    public void setUserName(String name) {
        if (dashboardNameLabel != null) {
            dashboardNameLabel.setText(name);
        }
    }
}