package sample;

import java.io.Serializable;

public class BudgetItem implements Serializable {
    private double amount;
    private String category;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BudgetItem(double amount, String category) {
        this.amount = amount;
        this.category = category;
    }

    BudgetItem add(double increment) {
        return new BudgetItem(this.amount + increment, this.category);
    }
}
