package com.warewise.client.controller;

import com.warewise.client.network.DataHandler;
import com.warewise.common.model.Item;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    // KPI Labels
    @FXML private Label totalOrdersLabel;
    @FXML private Label inventoryLabel;
    @FXML private Label lowStockLabel;
    @FXML private Label salesLabel;

    // Chart Section
    @FXML private ComboBox<String> chartTypeComboBox;
    @FXML private StackPane chartContainer;

    // Recent Activity Feed
    @FXML private ListView<String> activityFeedList;

    // Quick Actions Buttons
    @FXML private Button createOrderBtn;
    @FXML private Button updateInventoryBtn;

    // Notifications List
    @FXML private ListView<String> notificationsList;

    private String[] nameLabels;
    private Number[] numberValues;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String[] kpi = calculateInventoryKpi();
        totalOrdersLabel.setText(String.valueOf(DataHandler.orderList.size()));
        inventoryLabel.setText(kpi[0]);
        lowStockLabel.setText(kpi[2]);
        salesLabel.setText(kpi[1]);

        chartTypeComboBox.setItems(FXCollections.observableArrayList("Bar Chart", "Pie Chart"));
        chartTypeComboBox.setValue("Bar Chart");

        chartTypeComboBox.setOnAction(e -> updateChart());
        updateChart();

        activityFeedList.setItems(FXCollections.observableArrayList(
                "Order #123 processed",
                "Inventory updated",
                "Low stock alert on item #456"
        ));

        createOrderBtn.setOnAction(e -> handleCreateOrder());
        updateInventoryBtn.setOnAction(e -> handleUpdateInventory());

        notificationsList.setItems(FXCollections.observableArrayList(
                "Low stock alert",
                "New order received"
        ));
    }

    private void updateChart() {
        String selectedChart = chartTypeComboBox.getValue();
        String seriesName = "Inventory by Category";

        if ("Bar Chart".equals(selectedChart)) {
            initBarChart(nameLabels, numberValues, seriesName);
        } else if ("Pie Chart".equals(selectedChart)) {
            initPieChart(nameLabels, numberValues);
        }
    }

    private void initBarChart(String[] labels, Number[] values, String seriesName) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Category");
        yAxis.setLabel("Quantity");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Bar Chart");
        barChart.getStyleClass().add("chart");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(seriesName);

        int len = Math.min(labels.length, values.length);
        for (int i = 0; i < len; i++) {
            if (labels[i] != null && values[i] != null) {
                series.getData().add(new XYChart.Data<>(labels[i], values[i]));
            }
        }

        barChart.getData().add(series);
        chartContainer.getChildren().clear();
        chartContainer.getChildren().add(barChart);
    }

    private void initPieChart(String[] labels, Number[] values) {
        PieChart pieChart = new PieChart();
        pieChart.getStyleClass().add("chart");
        pieChart.setTitle("Pie Chart");

        int len = Math.min(labels.length, values.length);
        for (int i = 0; i < len; i++) {
            if (labels[i] != null && values[i] != null) {
                pieChart.getData().add(new PieChart.Data(labels[i], values[i].doubleValue()));
            }
        }

        chartContainer.getChildren().clear();
        chartContainer.getChildren().add(pieChart);
    }

    public void addDataToBarChart(String[] labels, Number[] values, String seriesName) {
        initBarChart(labels, values, seriesName);
    }

    public void addDataToPieChart(String[] labels, Number[] values) {
        initPieChart(labels, values);
    }

    private void handleCreateOrder() {
        System.out.println("Create Order action triggered");
    }

    private void handleUpdateInventory() {
        System.out.println("Update Inventory action triggered");
    }

    private String[] calculateInventoryKpi() {
        int totalCount = 0;
        double totalValueCount = 0;
        int lowStockCount = 0;

        int categoryCount = DataHandler.categoryList.size();
        numberValues = new Number[categoryCount];
        nameLabels = new String[categoryCount];

        for (int i = 0; i < categoryCount; i++) {
            numberValues[i] = 0;
            nameLabels[i] = null;
        }

        for (Item item : DataHandler.itemList) {
            int categoryIndex = item.getCategoryID() - 1;

            if (categoryIndex >= 0 && categoryIndex < categoryCount) {
                numberValues[categoryIndex] = numberValues[categoryIndex].intValue() + item.getQuantity();

                if (nameLabels[categoryIndex] == null) {
                    nameLabels[categoryIndex] = DataHandler.categoryList.get(categoryIndex).getName();
                }
            }

            totalCount += item.getQuantity();
            totalValueCount += item.getTotal();
            if (item.getQuantity() <= 99) {
                lowStockCount++;
            }
        }

        return new String[]{
                String.valueOf(totalCount),
                totalValueCount + "$",
                String.valueOf(lowStockCount)
        };
    }
}
