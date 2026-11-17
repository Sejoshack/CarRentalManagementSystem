
package models;

public class Car {

    // Attributes
    private int carId;
    private String brand;
    private String model;
    private String plateNumber;
    private double dailyRate;
    private String status;

    // Constructor
    public Car(int carId, String brand, String model,
               String plateNumber, double dailyRate,
               String status) {

        this.carId = carId;
        this.brand = brand;
        this.model = model;
        this.plateNumber = plateNumber;
        this.dailyRate = dailyRate;
        this.status = status;
    }

    // Getters
    public int getCarId() {
        return carId;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public double getDailyRate() {
        return dailyRate;
    }

    public String getStatus() {
        return status;
    }

    // Setters
    public void setCarId(int carId) {
        this.carId = carId;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public void setDailyRate(double dailyRate) {
        this.dailyRate = dailyRate;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
