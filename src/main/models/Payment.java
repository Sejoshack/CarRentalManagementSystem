
package models;

import java.time.LocalDate;

public class Payment {

    private int paymentId;
    private int rentalId;
    private double amount;
    private LocalDate paymentDate;
    private String paymentMethod;

    public Payment(
        int paymentId,
        int rentalId,
        double amount,
        LocalDate paymentDate,
        String paymentMethod
    ) {

        this.paymentId = paymentId;
        this.rentalId = rentalId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
    }

    // GETTERS

    public int getPaymentId() {
        return paymentId;
    }

    public int getRentalId() {
        return rentalId;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    // SETTERS

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public void setRentalId(int rentalId) {
        this.rentalId = rentalId;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
