
package view;

import controllers.PaymentController;
import models.Payment;
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

public class PaymentView {

    private PaymentController controller =
        new PaymentController();

    private ObservableList<Payment> paymentList =
        FXCollections.observableArrayList();

    private TableView<Payment> table =
        new TableView<>();

    private ComboBox<String> rentalBox =
        new ComboBox<>();

    private TextField amountField =
        new TextField();

    private DatePicker paymentDatePicker =
        new DatePicker(LocalDate.now());

    private ComboBox<String> methodBox =
        new ComboBox<>();

    public Parent getView() {

        BorderPane root = new BorderPane();

        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: #F4F6FA;");

        // TITLE
        Label title = new Label("Payment Management");

        title.setStyle(
            "-fx-font-size: 25px;" +
            "-fx-font-weight: bold;"
        );

        // PAYMENT FORM
        VBox form = new VBox(15);

        form.setPadding(new Insets(20));

        form.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10px;"
        );

        Label formTitle = new Label("Record New Payment");

        formTitle.setStyle(
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;"
        );

        rentalBox.setPromptText("Select Rental");

        amountField.setPromptText("Payment Amount");

        paymentDatePicker.setPromptText("Payment Date");

        methodBox.setPromptText("Select Payment Method");

        methodBox.getItems().addAll(
            "Cash",
            "M-Pesa",
            "Credit Card",
            "Bank Transfer"
        );

        loadRentals();

        HBox firstRow = new HBox(
            15,
            rentalBox,
            amountField
        );

        HBox secondRow = new HBox(
            15,
            paymentDatePicker,
            methodBox
        );

        Button addButton = new Button("Record Payment");

        addButton.setStyle(
            "-fx-background-color: #172554;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 10px 20px;"
        );

        addButton.setOnAction(
            event -> addPayment()
        );

        form.getChildren().addAll(
            formTitle,
            firstRow,
            secondRow,
            addButton
        );

        // PAYMENT TABLE
        Label tableTitle = new Label("Payment Records");

        tableTitle.setStyle(
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;"
        );

        TableColumn<Payment, Integer> idColumn =
            new TableColumn<>("Payment ID");

        idColumn.setCellValueFactory(
            new PropertyValueFactory<>("paymentId")
        );

        TableColumn<Payment, Integer> rentalColumn =
            new TableColumn<>("Rental ID");

        rentalColumn.setCellValueFactory(
            new PropertyValueFactory<>("rentalId")
        );

        TableColumn<Payment, Double> amountColumn =
            new TableColumn<>("Amount");

        amountColumn.setCellValueFactory(
            new PropertyValueFactory<>("amount")
        );

        TableColumn<Payment, LocalDate> dateColumn =
            new TableColumn<>("Payment Date");

        dateColumn.setCellValueFactory(
            new PropertyValueFactory<>("paymentDate")
        );

        TableColumn<Payment, String> methodColumn =
            new TableColumn<>("Payment Method");

        methodColumn.setCellValueFactory(
            new PropertyValueFactory<>("paymentMethod")
        );

        table.getColumns().addAll(
            idColumn,
            rentalColumn,
            amountColumn,
            dateColumn,
            methodColumn
        );

        table.setItems(paymentList);

        table.setColumnResizePolicy(
            TableView.CONSTRAINED_RESIZE_POLICY
        );

        table.setPrefHeight(400);

        Button refreshButton = new Button("Refresh Payments");

        refreshButton.setOnAction(
            event -> {
                loadRentals();
                refreshPayments();
            }
        );

        VBox content = new VBox(18);

        content.getChildren().addAll(
            title,
            form,
            tableTitle,
            table,
            refreshButton
        );

        root.setCenter(content);

        refreshPayments();

        return root;
    }

    // LOAD RENTALS
    private void loadRentals() {

        rentalBox.getItems().clear();

        String sql =
            "SELECT rental_id, customer_id, car_id, total_amount " +
            "FROM rentals ORDER BY rental_id DESC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                 connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {

                rentalBox.getItems().add(
                    result.getInt("rental_id") +
                    " - Customer " +
                    result.getInt("customer_id") +
                    " - Car " +
                    result.getInt("car_id") +
                    " - KSh " +
                    result.getDouble("total_amount")
                );
            }

        } catch (SQLException e) {

            System.out.println("Error loading rentals!");
            e.printStackTrace();
        }
    }

    // ADD PAYMENT
    private void addPayment() {

        if (rentalBox.getValue() == null ||
            amountField.getText().trim().isEmpty() ||
            paymentDatePicker.getValue() == null ||
            methodBox.getValue() == null) {

            showAlert(
                Alert.AlertType.WARNING,
                "Missing Details",
                "Please complete all payment fields."
            );

            return;
        }

        try {

            int rentalId = Integer.parseInt(
                rentalBox.getValue().split(" - ")[0]
            );

            double amount = Double.parseDouble(
                amountField.getText().trim()
            );

            if (!Double.isFinite(amount) || amount <= 0) {
                throw new IllegalArgumentException(
                    "Payment amount must be a positive number."
                );
            }

            Payment payment = new Payment(
                0,
                rentalId,
                amount,
                paymentDatePicker.getValue(),
                methodBox.getValue()
            );

            if (controller.addPayment(payment)) {

                showAlert(
                    Alert.AlertType.INFORMATION,
                    "Success",
                    "Payment recorded successfully!"
                );

                amountField.clear();

                rentalBox.setValue(null);

                methodBox.setValue(null);

                paymentDatePicker.setValue(LocalDate.now());

                refreshPayments();

            } else {

                showAlert(
                    Alert.AlertType.ERROR,
                    "Error",
                    "Could not record payment."
                );
            }

        } catch (NumberFormatException e) {

            showAlert(
                Alert.AlertType.WARNING,
                "Invalid Amount",
                "Enter a valid payment amount."
            );

        } catch (IllegalArgumentException e) {

            showAlert(
                Alert.AlertType.WARNING,
                "Invalid Amount",
                e.getMessage()
            );
        }
    }

    // REFRESH TABLE
    private void refreshPayments() {

        paymentList.setAll(
            controller.getAllPayments()
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
