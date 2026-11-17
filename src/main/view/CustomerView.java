
package view;

import controllers.CustomerController;
import models.Customer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

public class CustomerView {

    private CustomerController controller =
        new CustomerController();

    private ObservableList<Customer> customerList =
        FXCollections.observableArrayList();

    private TableView<Customer> table =
        new TableView<>();

    private TextField firstNameField = new TextField();
    private TextField lastNameField = new TextField();
    private TextField phoneField = new TextField();
    private TextField emailField = new TextField();

    public Parent getView() {

        BorderPane root = new BorderPane();

        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: #F4F6FA;");

        // TITLE
        Label title = new Label("Customer Management");

        title.setStyle(
            "-fx-font-size: 25px;" +
            "-fx-font-weight: bold;"
        );

        // REGISTRATION FORM
        VBox form = new VBox(15);

        form.setPadding(new Insets(20));

        form.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10px;"
        );

        Label formTitle = new Label("Register New Customer");

        formTitle.setStyle(
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;"
        );

        firstNameField.setPromptText("First Name");
        lastNameField.setPromptText("Last Name");
        phoneField.setPromptText("Phone Number");
        emailField.setPromptText("Email Address");

        HBox nameFields = new HBox(15);

        nameFields.getChildren().addAll(
            firstNameField,
            lastNameField
        );

        HBox contactFields = new HBox(15);

        contactFields.getChildren().addAll(
            phoneField,
            emailField
        );

        Button addButton = new Button("Add Customer");

        addButton.setStyle(
            "-fx-background-color: #172554;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 10px 20px;"
        );

        addButton.setOnAction(event -> addCustomer());

        form.getChildren().addAll(
            formTitle,
            nameFields,
            contactFields,
            addButton
        );

        // CUSTOMER TABLE
        Label tableTitle = new Label("Registered Customers");

        tableTitle.setStyle(
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;"
        );

        TableColumn<Customer, Integer> idColumn =
            new TableColumn<>("Customer ID");

        idColumn.setCellValueFactory(
            new PropertyValueFactory<>("customerId")
        );

        TableColumn<Customer, String> firstNameColumn =
            new TableColumn<>("First Name");

        firstNameColumn.setCellValueFactory(
            new PropertyValueFactory<>("firstName")
        );

        TableColumn<Customer, String> lastNameColumn =
            new TableColumn<>("Last Name");

        lastNameColumn.setCellValueFactory(
            new PropertyValueFactory<>("lastName")
        );

        TableColumn<Customer, String> phoneColumn =
            new TableColumn<>("Phone");

        phoneColumn.setCellValueFactory(
            new PropertyValueFactory<>("phone")
        );

        TableColumn<Customer, String> emailColumn =
            new TableColumn<>("Email");

        emailColumn.setCellValueFactory(
            new PropertyValueFactory<>("email")
        );

        table.getColumns().addAll(
            idColumn,
            firstNameColumn,
            lastNameColumn,
            phoneColumn,
            emailColumn
        );

        table.setItems(customerList);

        table.setColumnResizePolicy(
            TableView.CONSTRAINED_RESIZE_POLICY
        );

        table.setPrefHeight(400);

        // REFRESH BUTTON
        Button refreshButton = new Button("Refresh Customers");

        refreshButton.setOnAction(event -> refreshCustomers());

        VBox center = new VBox(15);

        center.getChildren().addAll(
            title,
            form,
            tableTitle,
            table,
            refreshButton
        );

        root.setCenter(center);

        refreshCustomers();

        return root;
    }

    // ADD CUSTOMER
    private void addCustomer() {

        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();

        if (firstName.isEmpty() ||
            lastName.isEmpty() ||
            phone.isEmpty() ||
            email.isEmpty()) {

            showAlert(
                Alert.AlertType.WARNING,
                "Validation Error",
                "Please fill in all customer details."
            );

            return;
        }

        Customer customer = new Customer(
            0,
            firstName,
            lastName,
            phone,
            email
        );

        if (controller.addCustomer(customer)) {

            showAlert(
                Alert.AlertType.INFORMATION,
                "Success",
                "Customer added successfully!"
            );

            clearFields();
            refreshCustomers();

        } else {

            showAlert(
                Alert.AlertType.ERROR,
                "Error",
                "Could not add customer. Check the database."
            );
        }
    }

    // REFRESH TABLE
    private void refreshCustomers() {

        customerList.setAll(
            controller.getAllCustomers()
        );
    }

    // CLEAR FORM
    private void clearFields() {

        firstNameField.clear();
        lastNameField.clear();
        phoneField.clear();
        emailField.clear();
    }

    // ALERT MESSAGE
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
