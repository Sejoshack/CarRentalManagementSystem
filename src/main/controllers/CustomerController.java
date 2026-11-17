
package controllers;

import database.DBConnection;
import models.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

public class CustomerController {

    // ADD CUSTOMER
    public boolean addCustomer(Customer customer) {

        String sql = "INSERT INTO customers " +
                     "(first_name, last_name, phone, email) " +
                     "VALUES (?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                 connection.prepareStatement(sql)) {

            statement.setString(1, customer.getFirstName());
            statement.setString(2, customer.getLastName());
            statement.setString(3, customer.getPhone());
            statement.setString(4, customer.getEmail());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error adding customer!");
            e.printStackTrace();
            return false;
        }
    }

    // GET ALL CUSTOMERS
    public List<Customer> getAllCustomers() {

        List<Customer> customers = new ArrayList<>();

        String sql = "SELECT * FROM customers ORDER BY customer_id DESC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                 connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {

                Customer customer = new Customer(
                    result.getInt("customer_id"),
                    result.getString("first_name"),
                    result.getString("last_name"),
                    result.getString("phone"),
                    result.getString("email")
                );

                customers.add(customer);
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving customers!");
            e.printStackTrace();
        }

        return customers;
    }
}
