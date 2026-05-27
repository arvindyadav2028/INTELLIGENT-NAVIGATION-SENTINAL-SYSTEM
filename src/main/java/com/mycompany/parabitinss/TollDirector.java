package com.mycompany.parabitinss;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.stage.Stage;

public class TollDirector extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Crowd Monitoring Dashboard");

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // === TOP: Bar Chart (Weekly Crowd) ===
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Days of Week");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Crowd (in Lakh)");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Weekly Crowd Overview");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>("Day 1", 5));
        series.getData().add(new XYChart.Data<>("Day 2", 2));
        series.getData().add(new XYChart.Data<>("Day 3", 8));
        series.getData().add(new XYChart.Data<>("Day 4", 6));
        series.getData().add(new XYChart.Data<>("Day 5", 7));
        series.getData().add(new XYChart.Data<>("Day 6", 5));
        barChart.getData().add(series);

        barChart.setLegendVisible(false);
        barChart.setPrefHeight(250);
        root.setTop(barChart);

        // === RIGHT: Alerts / Emergency Transmission ===
        TextArea alerts = new TextArea("Emergency Transmission:\nNo current alerts...");
        alerts.setStyle("-fx-font-size: 14px; -fx-text-fill: red; -fx-control-inner-background: #ffeeee;");
        alerts.setPrefWidth(250);
        root.setRight(alerts);

        // === CENTER: Toll Indicators ===
        GridPane tollGrid = new GridPane();
        tollGrid.setHgap(25);
        tollGrid.setVgap(25);
        tollGrid.setAlignment(Pos.CENTER);
        tollGrid.setPadding(new Insets(20));

        int[] tollValues = {504, 300, 241, 510, 150, 120};
        for (int i = 0; i < tollValues.length; i++) {
            VBox tollBox = new VBox(5);
            tollBox.setAlignment(Pos.CENTER);
            tollBox.getChildren().add(createTollCircle("Toll-" + (i + 1), tollValues[i]));
            tollGrid.add(tollBox, i % 3, i / 3);
        }
        root.setCenter(tollGrid);

        // === BOTTOM: LiveChart Integration ===
        VBox bottomBox = new VBox(10);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(10));

        Label liveLabel = new Label("Live Toll Vehicle Flow");
        liveLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        StackPane liveChartPane = new StackPane();
        liveChartPane.setPrefSize(900, 300);
        bottomBox.getChildren().addAll(liveLabel, liveChartPane);
        root.setBottom(bottomBox);

        // Run LiveChart async and embed its chart root
        Platform.runLater(() -> {
            try {
                LiveChart chart = new LiveChart();
                Scene liveScene = chart.initChart(); // returns Scene
                liveChartPane.getChildren().add(liveScene.getRoot()); // embed chart’s root
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // === Final Scene Setup ===
        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // === Custom Toll Circle Drawing ===
    private StackPane createTollCircle(String tollName, int value) {
        double percent = value / 600.0; // assume max capacity 600
        double angle = percent * 360;

        Canvas canvas = new Canvas(120, 120);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Background circle
        gc.setFill(Color.LIGHTGRAY);
        gc.fillOval(10, 10, 100, 100);

        // Filled portion
        gc.setFill(Color.RED);
        gc.fillArc(10, 10, 100, 100, 90, -angle, ArcType.ROUND);

        // Inner white circle (for better look)
        gc.setFill(Color.WHITE);
        gc.fillOval(30, 30, 60, 60);

        // Value text
        gc.setFill(Color.BLACK);
        gc.fillText(String.valueOf(value), 52, 70);

        Label label = new Label(tollName);
        VBox vbox = new VBox(canvas, label);
        vbox.setAlignment(Pos.CENTER);

        StackPane stack = new StackPane(vbox);
        stack.setAlignment(Pos.CENTER);
        return stack;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
