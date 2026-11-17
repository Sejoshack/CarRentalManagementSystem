
package models;

import java.time.LocalDate;

public class Rental {

    private int rentalId;
    private int customerId;
    private int carId;
    private int employeeId;

    private LocalDate rentalDate;
    private LocalDate returnDate;

    private double totalAmount;
    private String status;

    public Rental(
        int rentalId,
        int customerId,
        int carId,
        int employeeId,
        LocalDate rentalDate,
        LocalDate returnDate,
        double totalAmount,
        String status
    ) {

        this.rentalId = rentalId;
        this.customerId = customerId;
        this.carId = carId;
        this.employeeId = employeeId;
        this.rentalDate = rentalDate;
        this.returnDate = returnDate;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    // GETTERS

    public int getRentalId() {
        return rentalId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public int getCarId() {
        return carId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public LocalDate getRentalDate() {
        return rentalDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getStatus() {
        return status;
    }

    // SETTERS

    public void setRentalId(int rentalId) {
        this.rentalId = rentalId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public void setCarId(int carId) {
        this.carId = carId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public void setRentalDate(LocalDate rentalDate) {
        this.rentalDate = rentalDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
