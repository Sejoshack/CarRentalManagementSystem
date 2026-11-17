
package view;

import controllers.CarController;
import models.Car;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

public class CarView {

    private final CarController controller = new CarController();

    private final ObservableList<Car> carList =
            FXCollections.observableArrayList();

    private final TableView<Car> carTable = new TableView<>();

    private TextField brandField;
    private TextField modelField;
    private TextField plateField;
    private TextField rateField;
    private ComboBox<String> statusBox;

    public Parent getView() {

        BorderPane root = new BorderPane();

        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: #f4f6f9;");

        // PAGE TITLE
        Label title = new Label("Cars Management");

        title.setStyle(
            "-fx-font-size: 25px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #172554;"
        );

        // REGISTRATION FORM
        VBox form = createCarForm();

        // TABLE
        VBox tableSection = createCarTable();

        VBox content = new VBox(20, title, form, tableSection);

        root.setCenter(content);

        // LOAD EXISTING CARS
        refreshCars();

        return root;
    }

    // CREATE CAR FORM
    private VBox createCarForm() {

        Label formTitle = new Label("Register New Car");

        formTitle.setStyle(
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;"
        );

        brandField = new TextField();
        brandField.setPromptText("Enter brand");

        modelField = new TextField();
        modelField.setPromptText("Enter model");

        plateField = new TextField();
        plateField.setPromptText("Enter plate number");

        rateField = new TextField();
        rateField.setPromptText("Enter daily rate");

        statusBox = new ComboBox<>();

        statusBox.getItems().addAll(
            "Available",
            "Rented",
            "Maintenance"
        );

        statusBox.setValue("Available");
        statusBox.setMaxWidth(Double.MAX_VALUE);

        GridPane grid = new GridPane();

        grid.setHgap(15);
        grid.setVgap(12);

        grid.add(new Label("Brand"), 0, 0);
        grid.add(brandField, 1, 0);

        grid.add(new Label("Model"), 2, 0);
        grid.add(modelField, 3, 0);

        grid.add(new Label("Plate Number"), 0, 1);
        grid.add(plateField, 1, 1);

        grid.add(new Label("Daily Rate"), 2, 1);
        grid.add(rateField, 3, 1);

        grid.add(new Label("Status"), 0, 2);
        grid.add(statusBox, 1, 2);

        ColumnConstraints column1 = new ColumnConstraints();
        ColumnConstraints column2 = new ColumnConstraints();

        column1.setPercentWidth(15);
        column2.setPercentWidth(35);

        ColumnConstraints column3 = new ColumnConstraints();
        ColumnConstraints column4 = new ColumnConstraints();

        column3.setPercentWidth(15);
        column4.setPercentWidth(35);

        grid.getColumnConstraints().addAll(
            column1, column2, column3, column4
        );

        Button addButton = new Button("Add Car");

        addButton.setStyle(
            "-fx-background-color: #2563eb;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 10 25;"
        );

        addButton.setOnAction(e -> addCar());

        VBox formBox = new VBox(
            15,
            formTitle,
            grid,
            addButton
        );

        formBox.setPadding(new Insets(20));
        formBox.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10;"
        );

        return formBox;
    }

    // CREATE CAR TABLE
    private VBox createCarTable() {

        Label tableTitle = new Label("Registered Cars");

        tableTitle.setStyle(
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;"
        );

        TableColumn<Car, Integer> idColumn =
                new TableColumn<>("Car ID");

        idColumn.setCellValueFactory(
            new PropertyValueFactory<>("carId")
        );

        TableColumn<Car, String> brandColumn =
                new TableColumn<>("Brand");

        brandColumn.setCellValueFactory(
            new PropertyValueFactory<>("brand")
        );

        TableColumn<Car, String> modelColumn =
                new TableColumn<>("Model");

        modelColumn.setCellValueFactory(
            new PropertyValueFactory<>("model")
        );

        TableColumn<Car, String> plateColumn =
                new TableColumn<>("Plate Number");

        plateColumn.setCellValueFactory(
            new PropertyValueFactory<>("plateNumber")
        );

        TableColumn<Car, Double> rateColumn =
                new TableColumn<>("Daily Rate");

        rateColumn.setCellValueFactory(
            new PropertyValueFactory<>("dailyRate")
        );

        TableColumn<Car, String> statusColumn =
                new TableColumn<>("Status");

        statusColumn.setCellValueFactory(
            new PropertyValueFactory<>("status")
        );

        carTable.getColumns().addAll(
            idColumn,
            brandColumn,
            modelColumn,
            plateColumn,
            rateColumn,
            statusColumn
        );

        carTable.setItems(carList);

        carTable.setColumnResizePolicy(
            TableView.CONSTRAINED_RESIZE_POLICY
        );

        Button refreshButton = new Button("Refresh");

        refreshButton.setOnAction(e -> refreshCars());

        HBox tableHeader = new HBox(
            15,
            tableTitle,
            refreshButton
        );

        tableHeader.setAlignment(Pos.CENTER_LEFT);

        VBox tableBox = new VBox(
            15,
            tableHeader,
            carTable
        );

        tableBox.setPadding(new Insets(20));
        tableBox.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10;"
        );

        return tableBox;
    }

    // ADD CAR TO DATABASE
    private void addCar() {

        String brand = brandField.getText().trim();
        String model = modelField.getText().trim();
        String plate = plateField.getText().trim();
        String rateText = rateField.getText().trim();
        String status = statusBox.getValue();

        if (brand.isEmpty() ||
            model.isEmpty() ||
            plate.isEmpty() ||
            rateText.isEmpty()) {

            showAlert(
                Alert.AlertType.WARNING,
                "Validation Error",
                "Please fill in all car details."
            );

            return;
        }

        try {

            double rate = Double.parseDouble(rateText);

            if (!Double.isFinite(rate) || rate <= 0) {
                showAlert(
                    Alert.AlertType.WARNING,
                    "Invalid Rate",
                    "Daily rate must be a positive number."
                );
                return;
            }

            Car car = new Car(
                0,
                brand,
                model,
                plate,
                rate,
                status
            );

            boolean success = controller.addCar(car);

            if (success) {

                showAlert(
                    Alert.AlertType.INFORMATION,
                    "Success",
                    "Car registered successfully!"
                );

                clearForm();
                refreshCars();

            } else {

                showAlert(
                    Alert.AlertType.ERROR,
                    "Error",
                    "Car could not be registered."
                );
            }

        } catch (NumberFormatException e) {

            showAlert(
                Alert.AlertType.WARNING,
                "Invalid Input",
                "Enter a valid daily rate."
            );
        }
    }

    // LOAD CARS FROM DATABASE
    private void refreshCars() {

        carList.setAll(controller.getAllCars());
    }

    // CLEAR FORM
    private void clearForm() {

        brandField.clear();
        modelField.clear();
        plateField.clear();
        rateField.clear();

        statusBox.setValue("Available");
    }

    // DISPLAY ALERT
    private void showAlert(
            Alert.AlertType type,
            String title,
            String message) {

        Alert alert = new Alert(type);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }
}
