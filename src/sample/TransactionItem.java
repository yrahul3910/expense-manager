package sample;

import java.io.Serializable;

public class TransactionItem implements Serializable {
    private String type;
    private String notes;
    private String category;
    private double amount;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public TransactionItem(String type, String category, double amount, String notes) {
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.notes = notes;
    }
}
