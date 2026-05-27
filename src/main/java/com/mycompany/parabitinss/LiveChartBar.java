package com.mycompany.parabitinss;

import java.sql.*;
import java.util.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

public class LiveChartBar extends Application {

    protected static int phaseIndex;
    private final double xStep = 1.0 / 6.0;
    private double x = xStep;
    private double baseValue = 0.0;
    private int counter = 0;
    private final int totalPoints = 8640;

    private final List<Integer> totalCounts = new ArrayList<>();
    private final XYChart.Series<Number, Number> seriesCapacity = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> seriesArrived = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> seriesLoad = new XYChart.Series<>();

    private int yValue1 = 0, yValue3 = 0, xValue = 1;
    private int count = 0, count1 = 0;
    double load = 0;

    ParabitDBC db1 = new ParabitDBC();
    BarChart<Number, Number> barChart;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = initChart();
        primaryStage.setTitle("Live Toll Bar Chart");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public Scene initChart() throws Exception {
        // X-axis = Hours of the Day
        NumberAxis xAxis = new NumberAxis(0, 24, 1);
        xAxis.setLabel("Hours of the Day");

        // Y-axis = Vehicle count
        NumberAxis yAxis = new NumberAxis(0, 1400, 50);
        yAxis.setLabel("Number of Vehicles");

        barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Live Toll Data (Bar Chart)");
        barChart.setCategoryGap(0);
        barChart.setBarGap(0.5);

        seriesCapacity.setName("Toll Hourly Capacity");
        seriesArrived.setName("Vehicle Arrived");
        seriesLoad.setName("Load");

        barChart.getData().addAll(seriesCapacity, seriesArrived, seriesLoad);

        // Initial dummy point
        seriesCapacity.getData().add(new XYChart.Data<>(0, 0));
        seriesArrived.getData().add(new XYChart.Data<>(0, 0));
        seriesLoad.getData().add(new XYChart.Data<>(0, 0));

        // --- Fetch static values from DB
        String sql = "SELECT NoOfGates FROM CheckPointToll WHERE CpNo = 2;";
        db1.rs = db1.stm.executeQuery(sql);
        if (db1.rs.next()) {
            count = (db1.rs.getInt("NoOfGates") / 2) * 10;
        }

        java.time.LocalDate currDate = java.time.LocalDate.now();
        java.sql.Date sqlDate = java.sql.Date.valueOf(currDate);

        String sql1 = "SELECT COUNT(*) AS cnt FROM personalvehreg WHERE ArrivalDate = ?;";
        db1.ps = db1.con.prepareStatement(sql1);
        db1.ps.setDate(1, sqlDate);
        db1.rs = db1.ps.executeQuery();

        if (db1.rs.next()) {
            count1 = db1.rs.getInt("cnt") / 24 * 10;
        }

        // Add hourly static values for capacity and load
        while (xValue <= 24) {
            yValue1 += count;
            yValue3 += count1;

            final int currentX = xValue;
            final int finalY1 = yValue1;
            final int finalY3 = yValue3;

            Platform.runLater(() -> {
                seriesCapacity.getData().add(new XYChart.Data<>(currentX, finalY1));
                seriesLoad.getData().add(new XYChart.Data<>(currentX, finalY3));
            });

            xValue++;
        }

        // Load DB counts
        loadDataFromDB();

        // Live update timer
        Timer phaseTimer = new Timer();
        phaseTimer.scheduleAtFixedRate(new TimerTask() {
            double lastValueFetched = 0.0;

            @Override
            public void run() {
                Platform.runLater(() -> {
                    if (counter >= totalPoints) {
                        phaseTimer.cancel();
                        return;
                    }

                    int intervalInPhase = counter % 6;
                    phaseIndex = counter / 6;

                    if (phaseIndex >= totalCounts.size()) {
                        phaseTimer.cancel();
                        return;
                    }

                    int rowValue = totalCounts.get(phaseIndex);
                    double y;
                    if (intervalInPhase == 5) {
                        lastValueFetched = rowValue + baseValue;
                        y = lastValueFetched;
                        baseValue = lastValueFetched;
                    } else {
                        y = rowValue + baseValue;
                    }

                    // Add a new bar for the current phase
                    seriesArrived.getData().add(new XYChart.Data<>(x, y));

                    x += xStep;
                    counter++;
                });
            }
        }, 1000, 1000);

        return new Scene(barChart, 600, 450);
    }

    private void loadDataFromDB() {
        java.time.LocalDate currDate = java.time.LocalDate.now();
        java.sql.Date sqlDate = java.sql.Date.valueOf(currDate);
        try {
            String sql = "SELECT Time, SUM(APCount + ACCount) AS TotalCount " +
                    "FROM tollpassedveh WHERE TollCpNo = 2 AND Date = ? " +
                    "GROUP BY Time ORDER BY CAST(Time AS UNSIGNED)";

            db1.ps = db1.con.prepareStatement(sql);
            db1.ps.setDate(1, sqlDate);
            db1.rs = db1.ps.executeQuery();

            while (db1.rs.next()) {
                totalCounts.add(db1.rs.getInt("TotalCount") * 10);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
