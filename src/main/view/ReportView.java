
package view;

import database.DBConnection;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReportView {

    private Label carsLabel = new Label("0");
    private Label customersLabel = new Label("0");
    private Label rentalsLabel = new Label("0");
    private Label paymentsLabel = new Label("KSh 0.00");

    public Parent getView() {

        VBox root = new VBox(25);

        root.setPadding(new Insets(30));

        root.setStyle("-fx-background-color: #F4F6FA;");

        Label title = new Label("System Reports");

        title.setStyle(
            "-fx-font-size: 26px;" +
            "-fx-font-weight: bold;"
        );

        GridPane cards = new GridPane();

        cards.setHgap(20);
        cards.setVgap(20);

        cards.add(createCard("Total Cars", carsLabel), 0, 0);
        cards.add(createCard("Total Customers", customersLabel), 1, 0);
        cards.add(createCard("Total Rentals", rentalsLabel), 0, 1);
        cards.add(createCard("Total Payments", paymentsLabel), 1, 1);

        Button refreshButton = new Button("Refresh Reports");

        refreshButton.setOnAction(event -> loadReports());

        root.getChildren().addAll(
            title,
            cards,
            refreshButton
        );

        loadReports();

        return root;
    }

    private VBox createCard(String heading, Label value) {

        VBox card = new VBox(15);

        card.setPadding(new Insets(25));

        card.setPrefWidth(250);
        card.setPrefHeight(130);

        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12px;"
        );

        Label headingLabel = new Label(heading);

        headingLabel.setStyle(
            "-fx-font-size: 16px;" +
            "-fx-text-fill: #64748B;"
        );

        value.setStyle(
            "-fx-font-size: 25px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #172554;"
        );

        card.getChildren().addAll(headingLabel, value);

        return card;
    }

    private void loadReports() {

        String sql =
            "SELECT " +
            "(SELECT COUNT(*) FROM cars) AS total_cars, " +
            "(SELECT COUNT(*) FROM customers) AS total_customers, " +
            "(SELECT COUNT(*) FROM rentals) AS total_rentals, " +
            "(SELECT COALESCE(SUM(amount), 0) FROM payments) AS total_payments";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                 connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            if (result.next()) {

                carsLabel.setText(
                    String.valueOf(result.getInt("total_cars"))
                );

                customersLabel.setText(
                    String.valueOf(result.getInt("total_customers"))
                );

                rentalsLabel.setText(
                    String.valueOf(result.getInt("total_rentals"))
                );

                paymentsLabel.setText(
                    String.format(
                        "KSh %,.2f",
                        result.getDouble("total_payments")
                    )
                );
            }

        } catch (SQLException e) {

            System.out.println("Error loading reports!");
            e.printStackTrace();

        }
    }
}
