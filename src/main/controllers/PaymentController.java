
package controllers;

import database.DBConnection;
import models.Payment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

import java.util.ArrayList;
import java.util.List;

public class PaymentController {

    // ADD PAYMENT
    public boolean addPayment(Payment payment) {

        String sql =
            "INSERT INTO payments " +
            "(rental_id, amount, payment_date, payment_method) " +
            "VALUES (?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                return false;
            }

            try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

                statement.setInt(1, payment.getRentalId());
                statement.setDouble(2, payment.getAmount());

                statement.setDate(
                    3, Date.valueOf(payment.getPaymentDate())
                );

                statement.setString(
                    4, payment.getPaymentMethod()
                );

                return statement.executeUpdate() > 0;
            }

        } catch (SQLException e) {

            System.out.println("Error adding payment!");
            e.printStackTrace();

            return false;
        }
    }

    // GET ALL PAYMENTS
    public List<Payment> getAllPayments() {

        List<Payment> payments = new ArrayList<>();

        String sql =
            "SELECT * FROM payments ORDER BY payment_id DESC";

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                return payments;
            }

            try (PreparedStatement statement =
                     connection.prepareStatement(sql);
                 ResultSet result = statement.executeQuery()) {

                while (result.next()) {

                    Payment payment = new Payment(
                        result.getInt("payment_id"),
                        result.getInt("rental_id"),
                        result.getDouble("amount"),
                        result.getDate("payment_date").toLocalDate(),
                        result.getString("payment_method")
                    );

                    payments.add(payment);
                }
            }

        } catch (SQLException e) {

            System.out.println("Error retrieving payments!");
            e.printStackTrace();
        }

        return payments;
    }
}
