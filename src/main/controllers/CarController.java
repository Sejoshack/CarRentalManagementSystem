
package controllers;

import database.DBConnection;
import models.Car;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

public class CarController {

    // ADD A CAR
    public boolean addCar(Car car) {

        String sql = "INSERT INTO cars "
                + "(brand, model, plate_number, daily_rate, status) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                return false;
            }

            try (PreparedStatement statement =
                    connection.prepareStatement(sql)) {

                statement.setString(1, car.getBrand());
                statement.setString(2, car.getModel());
                statement.setString(3, car.getPlateNumber());
                statement.setDouble(4, car.getDailyRate());
                statement.setString(5, car.getStatus());

                int rows = statement.executeUpdate();

                return rows > 0;
            }

        } catch (SQLException e) {
            System.out.println("Error adding car!");
            e.printStackTrace();
            return false;
        }
    }


    // GET ALL CARS
    public List<Car> getAllCars() {

        List<Car> cars = new ArrayList<>();

        String sql = "SELECT * FROM cars ORDER BY car_id DESC";

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                return cars;
            }

            try (PreparedStatement statement =
                    connection.prepareStatement(sql);
                 ResultSet result = statement.executeQuery()) {

                while (result.next()) {

                    Car car = new Car(
                        result.getInt("car_id"),
                        result.getString("brand"),
                        result.getString("model"),
                        result.getString("plate_number"),
                        result.getDouble("daily_rate"),
                        result.getString("status")
                    );

                    cars.add(car);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving cars!");
            e.printStackTrace();
        }

        return cars;
    }
}
