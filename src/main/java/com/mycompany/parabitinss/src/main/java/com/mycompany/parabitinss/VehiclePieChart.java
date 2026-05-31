package com.mycompany.parabitinss;

import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.StackPane;

public class VehiclePieChart {

    public Scene getScene() {

        PieChart pie = new PieChart();
        pie.getData().add(new PieChart.Data("Passed", 400));
        pie.getData().add(new PieChart.Data("To passed", 250));

        pie.setTitle("Total Crowd");
        pie.setLabelsVisible(true);

        StackPane pane = new StackPane(pie);
        return new Scene(pane, 650, 300);
    }
}
