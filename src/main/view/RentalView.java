
package view;

import controllers.RentalController;
import models.Rental;
import database.DBConnection;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class RentalView {

    private RentalController controller = new RentalController();

    private ObservableList<Rental> rentalList =
        FXCollections.observableArrayList();

    private TableView<Rental> table = new TableView<>();

    private ComboBox<String> customerBox = new ComboBox<>();
    private ComboBox<String> carBox = new ComboBox<>();
    private ComboBox<String> employeeBox = new ComboBox<>();

    private DatePicker rentalDatePicker = new DatePicker();
    private DatePicker returnDatePicker = new DatePicker();

    private Label totalLabel = new Label("Total: KSh 0.00");

    // GET VIEW
    public Parent getView() {

        BorderPane root = new BorderPane();

        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: #F4F6FA;");

        Label title = new Label("Rental Management");

        title.setStyle(
            "-fx-font-size: 25px;" +
            "-fx-font-weight: bold;"
        );

        // RENTAL FORM
        VBox form = new VBox(15);

        form.setPadding(new Insets(20));

        form.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10px;"
        );

        Label formTitle = new Label("Create New Rental");

        formTitle.setStyle(
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;"
        );

        customerBox.setPromptText("Select Customer");
        carBox.setPromptText("Select Available Car");
        employeeBox.setPromptText("Select Employee");

        rentalDatePicker.setPromptText("Rental Date");
        returnDatePicker.setPromptText("Return Date");

        loadCustomers();
        loadCars();
        loadEmployees();

        HBox firstRow = new HBox(15,
            customerBox,
            carBox
        );

        HBox secondRow = new HBox(15,
            employeeBox,
            rentalDatePicker,
            returnDatePicker
        );

        totalLabel.setStyle(
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #172554;"
        );

        Button calculateButton = new Button("Calculate Cost");

        calculateButton.setOnAction(
            event -> calculateCost()
        );

        Button addButton = new Button("Create Rental");

        addButton.setStyle(
            "-fx-background-color: #172554;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 10px 20px;"
        );

        addButton.setOnAction(
            event -> addRental()
        );

        form.getChildren().addAll(
            formTitle,
            firstRow,
            secondRow,
            calculateButton,
            totalLabel,
            addButton
        );

        // RENTALS TABLE
        Label tableTitle = new Label("Rental Records");

        tableTitle.setStyle(
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;"
        );

        TableColumn<Rental, Integer> idColumn =
            new TableColumn<>("Rental ID");

        idColumn.setCellValueFactory(
            new PropertyValueFactory<>("rentalId")
        );

        TableColumn<Rental, Integer> customerColumn =
            new TableColumn<>("Customer ID");

        customerColumn.setCellValueFactory(
            new PropertyValueFactory<>("customerId")
        );

        TableColumn<Rental, Integer> carColumn =
            new TableColumn<>("Car ID");

        carColumn.setCellValueFactory(
            new PropertyValueFactory<>("carId")
        );

        TableColumn<Rental, Integer> employeeColumn =
            new TableColumn<>("Employee ID");

        employeeColumn.setCellValueFactory(
            new PropertyValueFactory<>("employeeId")
        );

        TableColumn<Rental, LocalDate> rentalColumn =
            new TableColumn<>("Rental Date");

        rentalColumn.setCellValueFactory(
            new PropertyValueFactory<>("rentalDate")
        );

        TableColumn<Rental, LocalDate> returnColumn =
            new TableColumn<>("Return Date");

        returnColumn.setCellValueFactory(
            new PropertyValueFactory<>("returnDate")
        );

        TableColumn<Rental, Double> amountColumn =
            new TableColumn<>("Total Amount");

        amountColumn.setCellValueFactory(
            new PropertyValueFactory<>("totalAmount")
        );

        TableColumn<Rental, String> statusColumn =
            new TableColumn<>("Status");

        statusColumn.setCellValueFactory(
            new PropertyValueFactory<>("status")
        );

        table.getColumns().addAll(
            idColumn,
            customerColumn,
            carColumn,
            employeeColumn,
            rentalColumn,
            returnColumn,
            amountColumn,
            statusColumn
        );

        table.setItems(rentalList);

        table.setColumnResizePolicy(
            TableView.CONSTRAINED_RESIZE_POLICY
        );

        table.setPrefHeight(350);

        // REFRESH BUTTON
        Button refreshButton = new Button("Refresh Rentals");

        refreshButton.setOnAction(
            event -> {
                loadCars();
                refreshRentals();
            }
        );

        // RETURN CAR BUTTON
        Button returnButton = new Button("Return Selected Car");

        returnButton.setStyle(
            "-fx-background-color: #15803D;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 10px 20px;"
        );

        returnButton.setOnAction(event -> returnSelectedCar());

        // BUTTON LAYOUT
        HBox buttons = new HBox(15);

        buttons.getChildren().addAll(
            refreshButton,
            returnButton
        );

        VBox content = new VBox(18);

        content.getChildren().addAll(
            title,
            form,
            tableTitle,
            table,
            buttons
        );

        root.setCenter(content);

        refreshRentals();

        return root;
    }

    // LOAD CUSTOMERS
    private void loadCustomers() {

        customerBox.getItems().clear();

        String sql =
            "SELECT customer_id, first_name, last_name FROM customers";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                 connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {

                customerBox.getItems().add(
                    result.getInt("customer_id") + " - " +
                    result.getString("first_name") + " " +
                    result.getString("last_name")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // LOAD AVAILABLE CARS
    private void loadCars() {

        carBox.getItems().clear();

        String sql =
            "SELECT car_id, brand, model, plate_number, daily_rate " +
            "FROM cars WHERE status = 'Available'";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                 connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {

                carBox.getItems().add(
                    result.getInt("car_id") + " - " +
                    result.getString("brand") + " " +
                    result.getString("model") + " - " +
                    result.getString("plate_number") + " - KSh " +
                    result.getDouble("daily_rate")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // LOAD EMPLOYEES
    private void loadEmployees() {

        employeeBox.getItems().clear();

        String sql =
            "SELECT employee_id, first_name, last_name FROM employees";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                 connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {

                employeeBox.getItems().add(
                    result.getInt("employee_id") + " - " +
                    result.getString("first_name") + " " +
                    result.getString("last_name")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // CALCULATE RENTAL COST
    private double calculateAmount() {

        if (carBox.getValue() == null ||
            rentalDatePicker.getValue() == null ||
            returnDatePicker.getValue() == null) {

            throw new IllegalArgumentException(
                "Select a car and both rental dates."
            );
        }

        LocalDate start = rentalDatePicker.getValue();
        LocalDate end = returnDatePicker.getValue();

        if (end.isBefore(start)) {

            throw new IllegalArgumentException(
                "Return date cannot be before rental date."
            );
        }

        String selectedCar = carBox.getValue();

        String[] parts = selectedCar.split(" - ");

        double dailyRate = Double.parseDouble(
            parts[parts.length - 1].replace("KSh ", "")
        );

        long days = ChronoUnit.DAYS.between(start, end);

        if (days == 0) {
            days = 1;
        }

        return days * dailyRate;
    }

    private void calculateCost() {

        try {

            double total = calculateAmount();

            totalLabel.setText(
                String.format("Total: KSh %.2f", total)
            );

        } catch (IllegalArgumentException e) {

            showAlert(
                Alert.AlertType.WARNING,
                "Invalid Details",
                e.getMessage()
            );
        }
    }

    // CREATE RENTAL
    private void addRental() {

        if (customerBox.getValue() == null ||
            carBox.getValue() == null ||
            employeeBox.getValue() == null ||
            rentalDatePicker.getValue() == null ||
            returnDatePicker.getValue() == null) {

            showAlert(
                Alert.AlertType.WARNING,
                "Missing Details",
                "Please complete all rental fields."
            );

            return;
        }

        try {

            int customerId = Integer.parseInt(
                customerBox.getValue().split(" - ")[0]
            );

            int carId = Integer.parseInt(
                carBox.getValue().split(" - ")[0]
            );

            int employeeId = Integer.parseInt(
                employeeBox.getValue().split(" - ")[0]
            );

            double total = calculateAmount();

            Rental rental = new Rental(
                0,
                customerId,
                carId,
                employeeId,
                rentalDatePicker.getValue(),
                returnDatePicker.getValue(),
                total,
                "Active"
            );

            if (controller.addRental(rental)) {

                showAlert(
                    Alert.AlertType.INFORMATION,
                    "Success",
                    "Rental created successfully!"
                );

                loadCars();
                refreshRentals();

                customerBox.setValue(null);
                carBox.setValue(null);
                employeeBox.setValue(null);

                rentalDatePicker.setValue(null);
                returnDatePicker.setValue(null);

                totalLabel.setText("Total: KSh 0.00");

            } else {

                showAlert(
                    Alert.AlertType.ERROR,
                    "Rental Failed",
                    "Could not create rental. The car may already be rented or the database rejected the request."
                );
            }

        } catch (IllegalArgumentException e) {

            showAlert(
                Alert.AlertType.WARNING,
                "Invalid Details",
                e.getMessage()
            );
        }
    }

    // RETURN SELECTED CAR
    private void returnSelectedCar() {

        Rental selectedRental =
            table.getSelectionModel().getSelectedItem();

        if (selectedRental == null) {

            showAlert(
                Alert.AlertType.WARNING,
                "No Rental Selected",
                "Please select a rental from the table first."
            );

            return;
        }

        if (!"Active".equalsIgnoreCase(selectedRental.getStatus())) {

            showAlert(
                Alert.AlertType.WARNING,
                "Invalid Rental",
                "This rental is not active."
            );

            return;
        }

        Alert confirmation =
            new Alert(Alert.AlertType.CONFIRMATION);

        confirmation.setTitle("Confirm Car Return");
        confirmation.setHeaderText("Return this car?");
        confirmation.setContentText(
            "Rental ID: " + selectedRental.getRentalId()
        );

        confirmation.showAndWait().ifPresent(response -> {

            if (response == ButtonType.OK) {

                boolean returned = controller.returnCar(
                    selectedRental.getRentalId()
                );

                if (returned) {

                    showAlert(
                        Alert.AlertType.INFORMATION,
                        "Success",
                        "Car returned successfully! It is now available."
                    );

                    refreshRentals();
                    loadCars();

                } else {

                    showAlert(
                        Alert.AlertType.ERROR,
                        "Return Failed",
                        "Could not return the car. Check its rental status."
                    );
                }
            }
        });
    }

    // REFRESH RENTALS
    private void refreshRentals() {

        rentalList.setAll(
            controller.getAllRentals()
        );
    }

    // ALERT
    private void showAlert(
        Alert.AlertType type,
        String title,
        String message
    ) {

        Alert alert = new Alert(type);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }
}
