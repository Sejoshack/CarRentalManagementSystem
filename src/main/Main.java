
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.chart.*;
import javafx.geometry.*;
import javafx.collections.FXCollections;
import javafx.beans.property.ReadOnlyStringWrapper;

import view.CarView;
import view.CustomerView;
import view.RentalView;
import view.PaymentView;
import view.ReportView;
import database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.time.Year;
import java.util.Map;
import java.util.HashMap;

public class Main extends Application {

    // DASHBOARD SUMMARY VALUES
    private Label totalCarsValue = new Label("0");
    private Label availableCarsValue = new Label("0");
    private Label rentedCarsValue = new Label("0");
    private Label totalCustomersValue = new Label("0");
    private Label activeRentalsValue = new Label("0");
    private Label totalPaymentsValue = new Label("KSh 0.00");

    // CHARTS
    private BarChart<String, Number> barChart;
    private PieChart pieChart;

    // RECENT RENTALS TABLE
    private TableView<String[]> recentTable = new TableView<>();

    @Override
    public void start(Stage stage) {

        // 1. MAIN LAYOUT
        BorderPane root = new BorderPane();

        root.setStyle("-fx-background-color: #F4F6FA;");

        // 2. LEFT SIDEBAR
        VBox sidebar = new VBox(18);

        sidebar.setPadding(new Insets(25));
        sidebar.setPrefWidth(220);

        sidebar.setStyle("-fx-background-color: #172554;");

        Label logo = new Label("CAR RENTAL");

        logo.setStyle(
            "-fx-text-fill: white;" +
            "-fx-font-size: 21px;" +
            "-fx-font-weight: bold;"
        );

        Button dashboardBtn = navButton("Dashboard");
        Button carsBtn = navButton("Cars");
        Button customersBtn = navButton("Customers");
        Button rentalsBtn = navButton("Rentals");
        Button paymentsBtn = navButton("Payments");
        Button reportsBtn = navButton("Reports");

        sidebar.getChildren().addAll(
            logo,
            new Separator(),
            dashboardBtn,
            carsBtn,
            customersBtn,
            rentalsBtn,
            paymentsBtn,
            reportsBtn
        );

        root.setLeft(sidebar);

        // 3. TOP HEADER
        HBox header = new HBox();

        header.setPadding(new Insets(20));
        header.setAlignment(Pos.CENTER_LEFT);

        header.setStyle("-fx-background-color: white;");

        Label heading = new Label("Dashboard");

        heading.setStyle(
            "-fx-font-size: 25px;" +
            "-fx-font-weight: bold;"
        );

        Region spacer = new Region();

        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label admin = new Label("Administrator");

        header.getChildren().addAll(
            heading,
            spacer,
            admin
        );

        root.setTop(header);

        // 4. DASHBOARD CONTENT
        VBox content = new VBox(22);

        content.setPadding(new Insets(25));

        Label overview = new Label("Overview");

        overview.setStyle(
            "-fx-font-size: 20px;" +
            "-fx-font-weight: bold;"
        );

        Button refreshButton = new Button("Refresh Dashboard");

        refreshButton.setOnAction(
            event -> loadDashboardData()
        );

        HBox overviewRow = new HBox(15);

        overviewRow.setAlignment(Pos.CENTER_LEFT);

        Region overviewSpacer = new Region();

        HBox.setHgrow(overviewSpacer, Priority.ALWAYS);

        overviewRow.getChildren().addAll(
            overview,
            overviewSpacer,
            refreshButton
        );

        // 5. SUMMARY CARDS
        GridPane cards = new GridPane();

        cards.setHgap(15);
        cards.setVgap(15);

        cards.add(
            createCard("Total Cars", totalCarsValue),
            0, 0
        );

        cards.add(
            createCard("Available Cars", availableCarsValue),
            1, 0
        );

        cards.add(
            createCard("Rented Cars", rentedCarsValue),
            2, 0
        );

        cards.add(
            createCard("Total Customers", totalCustomersValue),
            0, 1
        );

        cards.add(
            createCard("Active Rentals", activeRentalsValue),
            1, 1
        );

        cards.add(
            createCard("Total Payments", totalPaymentsValue),
            2, 1
        );

        // 6. BAR CHART
        CategoryAxis xAxis = new CategoryAxis();

        xAxis.setLabel("Month");

        NumberAxis yAxis = new NumberAxis();

        yAxis.setLabel("Number of Rentals");

        barChart = new BarChart<>(xAxis, yAxis);

        barChart.setTitle("Monthly Rentals");

        barChart.setLegendVisible(false);

        barChart.setPrefHeight(300);

        // 7. PIE CHART
        pieChart = new PieChart();

        pieChart.setTitle("Car Availability");

        pieChart.setPrefHeight(300);

        // 8. ARRANGE CHARTS
        HBox charts = new HBox(20);

        charts.getChildren().addAll(
            barChart,
            pieChart
        );

        HBox.setHgrow(barChart, Priority.ALWAYS);
        HBox.setHgrow(pieChart, Priority.ALWAYS);

        // 9. RECENT RENTALS TABLE
        Label recentTitle = new Label("Recent Rentals");

        recentTitle.setStyle(
            "-fx-font-size: 20px;" +
            "-fx-font-weight: bold;"
        );

        TableColumn<String[], String> rentalColumn =
            new TableColumn<>("Rental ID");

        rentalColumn.setCellValueFactory(
            data -> new ReadOnlyStringWrapper(
                data.getValue()[0]
            )
        );

        TableColumn<String[], String> customerColumn =
            new TableColumn<>("Customer");

        customerColumn.setCellValueFactory(
            data -> new ReadOnlyStringWrapper(
                data.getValue()[1]
            )
        );

        TableColumn<String[], String> carColumn =
            new TableColumn<>("Car");

        carColumn.setCellValueFactory(
            data -> new ReadOnlyStringWrapper(
                data.getValue()[2]
            )
        );

        TableColumn<String[], String> statusColumn =
            new TableColumn<>("Status");

        statusColumn.setCellValueFactory(
            data -> new ReadOnlyStringWrapper(
                data.getValue()[3]
            )
        );

        recentTable.getColumns().addAll(
            rentalColumn,
            customerColumn,
            carColumn,
            statusColumn
        );

        recentTable.setColumnResizePolicy(
            TableView.CONSTRAINED_RESIZE_POLICY
        );

        recentTable.setPrefHeight(200);

        // 10. ARRANGE DASHBOARD
        content.getChildren().addAll(
            overviewRow,
            cards,
            charts,
            recentTitle,
            recentTable
        );

        ScrollPane scrollPane = new ScrollPane(content);

        scrollPane.setFitToWidth(true);

        scrollPane.setStyle(
            "-fx-background: #F4F6FA;" +
            "-fx-background-color: #F4F6FA;"
        );

        root.setCenter(scrollPane);

        // LOAD REAL DATABASE INFORMATION
        loadDashboardData();

        // 11. NAVIGATION ACTIONS

        dashboardBtn.setOnAction(event -> {

            heading.setText("Dashboard");

            loadDashboardData();

            root.setCenter(scrollPane);
        });

        carsBtn.setOnAction(event -> {

            heading.setText("Cars Management");

            CarView carView = new CarView();

            root.setCenter(carView.getView());
        });

        customersBtn.setOnAction(event -> {

            heading.setText("Customers Management");

            CustomerView customerView = new CustomerView();

            root.setCenter(customerView.getView());
        });

        rentalsBtn.setOnAction(event -> {

            heading.setText("Rentals Management");

            RentalView rentalView = new RentalView();

            root.setCenter(rentalView.getView());
        });

        paymentsBtn.setOnAction(event -> {

            heading.setText("Payments Management");

            PaymentView paymentView = new PaymentView();

            root.setCenter(paymentView.getView());
        });

        reportsBtn.setOnAction(event -> {

            heading.setText("Reports");

            ReportView reportView = new ReportView();

            root.setCenter(reportView.getView());
        });

        // 12. CREATE WINDOW
        Scene scene = new Scene(root, 1200, 750);

        stage.setTitle("Car Rental Management System");

        stage.setScene(scene);

        stage.show();
    }

    // NAVIGATION BUTTON
    private Button navButton(String name) {

        Button button = new Button(name);

        button.setMaxWidth(Double.MAX_VALUE);

        button.setAlignment(Pos.CENTER_LEFT);

        button.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 15px;" +
            "-fx-padding: 12px;"
        );

        return button;
    }

    // DASHBOARD CARD
    private VBox createCard(String title, Label valueLabel) {

        VBox card = new VBox(12);

        card.setPadding(new Insets(20));

        card.setPrefWidth(200);

        card.setMaxWidth(Double.MAX_VALUE);

        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10px;"
        );

        Label titleLabel = new Label(title);

        titleLabel.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-text-fill: #64748B;"
        );

        valueLabel.setStyle(
            "-fx-font-size: 25px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #172554;"
        );

        card.getChildren().addAll(
            titleLabel,
            valueLabel
        );

        GridPane.setHgrow(card, Priority.ALWAYS);

        return card;
    }

    // LOAD ALL DASHBOARD DATA
    private void loadDashboardData() {

        String sql =
            "SELECT " +
            "(SELECT COUNT(*) FROM cars) AS total_cars, " +
            "(SELECT COUNT(*) FROM cars WHERE status = 'Available') AS available_cars, " +
            "(SELECT COUNT(*) FROM cars WHERE status = 'Rented') AS rented_cars, " +
            "(SELECT COUNT(*) FROM customers) AS total_customers, " +
            "(SELECT COUNT(*) FROM rentals WHERE status = 'Active') AS active_rentals, " +
            "(SELECT COALESCE(SUM(amount), 0) FROM payments) AS total_payments";

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                return;
            }

            try (PreparedStatement statement =
                     connection.prepareStatement(sql);
                 ResultSet result = statement.executeQuery()) {

                if (result.next()) {

                    totalCarsValue.setText(
                        String.valueOf(
                            result.getInt("total_cars")
                        )
                    );

                    availableCarsValue.setText(
                        String.valueOf(
                            result.getInt("available_cars")
                        )
                    );

                    rentedCarsValue.setText(
                        String.valueOf(
                            result.getInt("rented_cars")
                        )
                    );

                    totalCustomersValue.setText(
                        String.valueOf(
                            result.getInt("total_customers")
                        )
                    );

                    activeRentalsValue.setText(
                        String.valueOf(
                            result.getInt("active_rentals")
                        )
                    );

                    totalPaymentsValue.setText(
                        String.format(
                            "KSh %,.2f",
                            result.getDouble("total_payments")
                        )
                    );
                }
            }

            loadMonthlyRentals(connection);

            loadCarAvailability(connection);

            loadRecentRentals(connection);

        } catch (SQLException e) {

            System.out.println(
                "Error loading dashboard information!"
            );

            e.printStackTrace();
        }
    }

    // LOAD MONTHLY RENTALS CHART
    private void loadMonthlyRentals(Connection connection)
        throws SQLException {

        String[] months = {
            "Jan", "Feb", "Mar", "Apr",
            "May", "Jun", "Jul", "Aug",
            "Sep", "Oct", "Nov", "Dec"
        };

        Map<Integer, Integer> monthlyData = new HashMap<>();

        for (int i = 1; i <= 12; i++) {
            monthlyData.put(i, 0);
        }

        String sql =
            "SELECT MONTH(rental_date) AS month_number, " +
            "COUNT(*) AS rental_count " +
            "FROM rentals " +
            "WHERE YEAR(rental_date) = ? " +
            "GROUP BY MONTH(rental_date)";

        try (PreparedStatement statement =
                 connection.prepareStatement(sql)) {

            statement.setInt(1, Year.now().getValue());

            try (ResultSet result = statement.executeQuery()) {

                while (result.next()) {

                    monthlyData.put(
                        result.getInt("month_number"),
                        result.getInt("rental_count")
                    );
                }
            }
        }

        XYChart.Series<String, Number> series =
            new XYChart.Series<>();

        for (int i = 1; i <= 12; i++) {

            series.getData().add(
                new XYChart.Data<>(
                    months[i - 1],
                    monthlyData.get(i)
                )
            );
        }

        barChart.getData().clear();

        barChart.getData().add(series);
    }

    // LOAD CAR AVAILABILITY PIE CHART
    private void loadCarAvailability(Connection connection)
        throws SQLException {

        pieChart.getData().clear();

        String sql =
            "SELECT status, COUNT(*) AS total " +
            "FROM cars GROUP BY status";

        try (PreparedStatement statement =
                 connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {

                pieChart.getData().add(
                    new PieChart.Data(
                        result.getString("status"),
                        result.getInt("total")
                    )
                );
            }
        }
    }

    // LOAD RECENT RENTALS
    private void loadRecentRentals(Connection connection)
        throws SQLException {

        recentTable.getItems().clear();

        String sql =
            "SELECT r.rental_id, " +
            "CONCAT(c.first_name, ' ', c.last_name) AS customer_name, " +
            "CONCAT(ca.brand, ' ', ca.model) AS car_name, " +
            "r.status " +
            "FROM rentals r " +
            "JOIN customers c ON r.customer_id = c.customer_id " +
            "JOIN cars ca ON r.car_id = ca.car_id " +
            "ORDER BY r.rental_id DESC LIMIT 5";

        try (PreparedStatement statement =
                 connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {

                String[] row = {
                    String.valueOf(result.getInt("rental_id")),
                    result.getString("customer_name"),
                    result.getString("car_name"),
                    result.getString("status")
                };

                recentTable.getItems().add(row);
            }
        }
    }

    // MAIN METHOD
    public static void main(String[] args) {

        launch(args);
    }
}
