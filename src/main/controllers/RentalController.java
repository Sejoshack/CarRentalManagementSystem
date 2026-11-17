
package controllers;

import database.DBConnection;
import models.Rental;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

import java.util.ArrayList;
import java.util.List;

public class RentalController {

    // ADD A NEW RENTAL
    public boolean addRental(Rental rental) {

        String checkCarSQL =
            "UPDATE cars SET status = 'Rented' " +
            "WHERE car_id = ? AND status = 'Available'";

        String insertRentalSQL =
            "INSERT INTO rentals " +
            "(customer_id, car_id, employee_id, rental_date, " +
            "return_date, total_amount, status) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                return false;
            }

            // Begin database transaction
            connection.setAutoCommit(false);

            try {
                // 1. Check availability and reserve the car
                try (PreparedStatement checkStatement =
                         connection.prepareStatement(checkCarSQL)) {

                    checkStatement.setInt(1, rental.getCarId());

                    int updatedRows = checkStatement.executeUpdate();

                    if (updatedRows == 0) {
                        connection.rollback();
                        return false;
                    }
                }

                // 2. Save rental details
                try (PreparedStatement insertStatement =
                         connection.prepareStatement(insertRentalSQL)) {

                    insertStatement.setInt(1, rental.getCustomerId());
                    insertStatement.setInt(2, rental.getCarId());
                    insertStatement.setInt(3, rental.getEmployeeId());

                    insertStatement.setDate(
                        4, Date.valueOf(rental.getRentalDate())
                    );

                    insertStatement.setDate(
                        5, Date.valueOf(rental.getReturnDate())
                    );

                    insertStatement.setDouble(
                        6, rental.getTotalAmount()
                    );

                    insertStatement.setString(
                        7, rental.getStatus()
                    );

                    insertStatement.executeUpdate();
                }

                // 3. Commit both operations
                connection.commit();

                return true;

            } catch (SQLException e) {

                connection.rollback();
                throw e;

            } finally {

                connection.setAutoCommit(true);
            }

        } catch (SQLException e) {

            System.out.println("Error adding rental!");
            e.printStackTrace();

            return false;
        }
    }

    // GET ALL RENTALS
    public List<Rental> getAllRentals() {

        List<Rental> rentals = new ArrayList<>();

        String sql =
            "SELECT * FROM rentals ORDER BY rental_id DESC";

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                return rentals;
            }

            try (PreparedStatement statement =
                     connection.prepareStatement(sql);
                 ResultSet result = statement.executeQuery()) {

                while (result.next()) {

                    Rental rental = new Rental(
                        result.getInt("rental_id"),
                        result.getInt("customer_id"),
                        result.getInt("car_id"),
                        result.getInt("employee_id"),
                        result.getDate("rental_date").toLocalDate(),
                        result.getDate("return_date").toLocalDate(),
                        result.getDouble("total_amount"),
                        result.getString("status")
                    );

                    rentals.add(rental);
                }
            }

        } catch (SQLException e) {

            System.out.println("Error retrieving rentals!");
            e.printStackTrace();
        }

        return rentals;
    }
    

    public boolean returnCar(int rentalId) {

        String findRental =
            "SELECT car_id FROM rentals " +
            "WHERE rental_id = ? AND status = 'Active'";

        String updateRental =
            "UPDATE rentals SET status = 'Completed' " +
            "WHERE rental_id = ? AND status = 'Active'";

        String updateCar =
            "UPDATE cars SET status = 'Available' " +
            "WHERE car_id = ? AND status = 'Rented'";

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                return false;
            }

            connection.setAutoCommit(false);

            try {

                int carId;

                try (PreparedStatement findStatement =
                        connection.prepareStatement(findRental)) {

                    findStatement.setInt(1, rentalId);

                    try (ResultSet result = findStatement.executeQuery()) {

                        if (!result.next()) {
                            connection.rollback();
                            return false;
                        }

                        carId = result.getInt("car_id");
                    }
                }

                try (PreparedStatement rentalStatement =
                        connection.prepareStatement(updateRental)) {

                    rentalStatement.setInt(1, rentalId);

                    if (rentalStatement.executeUpdate() != 1) {
                        connection.rollback();
                        return false;
                    }
                }

                try (PreparedStatement carStatement =
                        connection.prepareStatement(updateCar)) {

                    carStatement.setInt(1, carId);

                    if (carStatement.executeUpdate() != 1) {
                        connection.rollback();
                        return false;
                    }
                }

                connection.commit();

                return true;

            } catch (SQLException e) {

                connection.rollback();

                throw e;

            } finally {

                connection.setAutoCommit(true);
            }

        } catch (SQLException e) {

            System.out.println("Error returning car!");
            e.printStackTrace();

            return false;
        }
    }

}
