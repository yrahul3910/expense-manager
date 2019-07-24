package sample;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

class Accounts implements Serializable {
    private Map<String, BudgetItem> budget;
    private List<TransactionItem> transactions;

    public Map<String, BudgetItem> getBudget() {
        return budget;
    }

    public List<TransactionItem> getTransactions() {
        return transactions;
    }

    Accounts(Map<String, BudgetItem> budget, List<TransactionItem> transactions) {
        this.budget = budget;
        this.transactions = transactions;
    }
}
