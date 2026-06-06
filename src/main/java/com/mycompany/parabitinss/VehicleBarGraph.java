package com.mycompany.parabitinss;

import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.layout.StackPane;

public class VehicleBarGraph {

    public Scene getScene() {

        CategoryAxis x = new CategoryAxis();
        NumberAxis y = new NumberAxis();

        x.setLabel("Zones");
        y.setLabel("Vehicles Passed");

        BarChart<String, Number> bar = new BarChart<>(x, y);
        bar.setTitle("Zone Wise Crowd");

        XYChart.Series<String, Number> data = new XYChart.Series<>();
        data.setName("Today");

        data.getData().add(new XYChart.Data<>("Zone 1", 120));
        data.getData().add(new XYChart.Data<>("Zone 2", 180));
        data.getData().add(new XYChart.Data<>("Zone 3", 260));
        data.getData().add(new XYChart.Data<>("Zone 4", 320));
        data.getData().add(new XYChart.Data<>("Zone 5", 410));
        data.getData().add(new XYChart.Data<>("Zone 6", 370));
        data.getData().add(new XYChart.Data<>("Zone 7", 370));
        data.getData().add(new XYChart.Data<>("Zone 8", 370));

        bar.getData().add(data);

        StackPane pane = new StackPane(bar);
        return new Scene(pane, 650, 300);
    }
}
