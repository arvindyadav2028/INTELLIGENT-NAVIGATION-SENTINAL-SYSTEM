package com.mycompany.parabitinss;

import java.sql.*;
import java.util.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

public class Chart extends Application {
    protected static int phaseIndex;
    private final double xStep = 1.0 / 6.0;
    private double x = xStep;
    private double baseValue = 0.0;
    private int counter = 0;
    private final int totalPoints = 8640;

    private final List<Integer> totalCounts = new ArrayList<>();
    private final XYChart.Series<Number, Number> series1 = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> series2 = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> series3 = new XYChart.Series<>();

    private int yValue1 = 0, yValue3 = 0, xValue = 1;
    private int count = 0, count1 = 0;
    double load = 0;

    ParabitDBC db1 = new ParabitDBC();
    LineChart<Number, Number> lineChart;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = initChart();
        primaryStage.setTitle("Live Toll Chart");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public Scene initChart() throws Exception {
        NumberAxis xAxis = new NumberAxis(0,24,1);
        xAxis.setLabel("Hours Of the Day");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("No Of Vehicles");
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(120);
        yAxis.setTickUnit(5);
        
        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Live Toll Data");
        lineChart.setCreateSymbols(true);

        series1.setName("Toll hourly Capacity");
        series2.setName("Vehicle Arrived");
        series3.setName("Load");

        lineChart.getData().addAll(series1, series2, series3);

        // Initial point
        series1.getData().add(new XYChart.Data<>(0, 0));
        series2.getData().add(new XYChart.Data<>(0, 0));
        series3.getData().add(new XYChart.Data<>(0, 0));

        // --- Load static values
        String sql = "SELECT NoOfGates FROM CheckPointToll WHERE CpNo = 2;";
        db1.rs = db1.stm.executeQuery(sql);
        if (db1.rs.next()) {
            count = (db1.rs.getInt("NoOfGates") / 2);
//             count = 5;
        }

        java.time.LocalDate currDate = java.time.LocalDate.now();
        java.sql.Date sqlDate = java.sql.Date.valueOf(currDate);

//        String sql1 = "SELECT Count(*) as cnt FROM personalvehreg WHERE CpInFk = 2 AND ArrivalDate =?;";
        String sql1 = "SELECT Count(*) as cnt FROM personalvehreg WHERE ArrivalDate =?;";
        db1.ps = db1.con.prepareStatement(sql1);
        db1.ps.setDate(1, sqlDate);
        db1.rs = db1.ps.executeQuery();

        if (db1.rs.next()) {
            count1 =(db1.rs.getInt("cnt")/24);
            System.out.println(db1.rs.getInt("cnt")/24);
            System.out.println(db1.rs.getInt("cnt"));
        }

        // Add hourly capacity and load (series1 and series3)
        while (xValue <= 24) {
            yValue1 += count;
           
            yValue3 += count1;
 System.out.println(yValue3);
            final int currentX = xValue;
            final int finalY1 = yValue1;
            final int finalY3 = yValue3;

//            Platform.runLater(() -> {
                series1.getData().add(new XYChart.Data<>(currentX, finalY1));
                series3.getData().add(new XYChart.Data<>(currentX, finalY3));
                System.out.println("Series1 x=" + currentX + " -> y=" + finalY1);
                System.out.println("Series3 x=" + currentX + " -> y=" + finalY3);
//            });

            xValue++;
        }

        // Load hourly actual counts
        loadDataFromDB();

        // Start live updates
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
//                    int phaseIndex = counter / 6;
                     phaseIndex= counter / 6;
                      

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
                        
                        if(0<phaseIndex && phaseIndex<=24){          
                            int i = phaseIndex + 1; // for x = 1 to 24
                            double Y2 = series2.getData().get(i).getYValue().doubleValue(); // series2 current
                            double Y3 = series3.getData().get(i).getYValue().doubleValue(); // series3 current
                            // Step 2: Calculate adjustment
                            double load = Y3- Y2;
                            
//                            if (load >0) {
//                               lineChart.setStyle("-fx-background-color: red;");
//                            } else {
//                               lineChart.setStyle("");
//                            }
                            
                            load = load/(24 - i);
                            double lastLoad=0;
                            System.out.println("Load="+load);
                            for (int j = i + 1; j < series3.getData().size()-1; j++) {
                                double Y=series3.getData().get(j).getYValue().doubleValue();
                                if(load>=count1){
                                   load=count1;
                                 }
                                if(lastLoad>=count1){
                                   load=0;
                                 }
                                 lastLoad+=load;
                                System.out.println("Y="+Y);
                                 double newLoad=Y+load; 
                                 if(newLoad>count1*(j+1)){
                                   newLoad=count1*(j+1);
                                 }
                                 System.out.println("CummutativeLoad="+newLoad);
                                 series3.getData().get(j).setYValue(newLoad);
                               }
                        }

                    } else {
                        y = rowValue + baseValue;
                    }

                    series2.getData().add(new XYChart.Data<>(x, y));

                    if (!series1.getData().isEmpty()) {
                        if (yValue1 > y) {
//                            lineChart.setStyle("-fx-background-color: red;");
                        } else {
//                            lineChart.setStyle("");
                        }
                    }

                    x += xStep;
                    counter++;
                });
            }
        }, 1000, 1000);

        return new Scene(lineChart, 450, 445);
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
                totalCounts.add(db1.rs.getInt("TotalCount"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // Entry point
    public static void main(String[] args) {
        launch(args);
    }
}
