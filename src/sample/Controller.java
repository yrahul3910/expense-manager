package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;

import java.net.URL;
import java.util.*;

public class Controller implements Initializable {
    // UI elements
    public ChoiceBox<String> cmbType;
    public ComboBox<String> cmbCategory;
    public TextField txtAmount;
    public Button btnAddEntry;
    public RadioButton rdbBudget, rdbTransactions;
    public TextArea txtNotes;

    // Right-anchored TableView and its columns
    public TableView<BudgetItem> budgetView;
    public TableColumn<BudgetItem, String> budgetCategoryColumn;
    public TableColumn<BudgetItem, Double> budgetAmountColumn;

    // Left-anchored TableView and its columns
    public TableView<TransactionItem> transactionsView;
    public TableColumn<TransactionItem, String> transactionsCategoryColumn;
    public TableColumn<TransactionItem, Double> transactionsAmountColumn;

    // Data representation of budget and transactions
    // We use a Map for budget, because you can only have one of each category. But
    // for the transactions, you can have several of each.
    private Map<String, BudgetItem> budget = new HashMap<>();
    private List<TransactionItem> transactions = new ArrayList<>();
    private Map<String, Double> transactionsMap = new HashMap<>();

    /*
    TODO:
        * View --> Consolidated Transactions / Separate
        * File --> Save, Open, Separator, Exit
        * Help --> About
     */

    private ObservableList<BudgetItem> getBudgetItems() {
        ObservableList<BudgetItem> items = FXCollections.observableArrayList(budget.values());
        return items;
    }

    private ObservableList<TransactionItem> getTransactionItems() {
        ObservableList<TransactionItem> items = FXCollections.observableArrayList(transactions);
        return items;
    }

    private void onTypeChanged(ActionEvent e) {
        if (cmbType.getValue().equals("Income"))
            cmbCategory.setDisable(true);
        else
            cmbCategory.setDisable(false);
    }

    public void onBudgetButtonSelected(ActionEvent e) {
        cmbType.setDisable(true);
        txtNotes.setDisable(true);
    }

    public void onTransactionsButtonSelected(ActionEvent e) {
        cmbType.setDisable(false);
        txtNotes.setDisable(false);
    }

    public void onAddEntryClicked(ActionEvent e) {
        String key = cmbCategory.getValue();

        try {
            double amount = Double.parseDouble(txtAmount.getText());

            if (rdbBudget.isSelected()) {
                // Sanity check
                if (amount < 0) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Amount cannot be negative in budget",
                            ButtonType.OK);
                    alert.setTitle("Error");
                    alert.setHeaderText("Data error");
                    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                    alert.showAndWait();
                    return;
                }

                if (budget.containsKey(key)) {
                    // Update the values
                    BudgetItem currentValue = budget.get(key);
                    BudgetItem newValue = currentValue.add(amount);

                    budget.put(key, newValue);
                    budgetView.setItems(getBudgetItems());  // and also in the view
                } else {
                    // Create a new entry
                    BudgetItem budgetItem = new BudgetItem(amount, key);
                    budget.put(key, budgetItem);
                    budgetView.getItems().add(budgetItem);
                }
            } else {
                // Add a new entry
                String type = cmbType.getValue();

                if (type.equals("Income"))
                    key = "Income";

                // Sanity checks
                if (type.equals("Expense") && amount > 0)
                    amount = -amount;
                else if (type.equals("Income") && amount < 0) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Income amount must be positive.",
                            ButtonType.OK);
                    alert.setTitle("Error");
                    alert.setHeaderText("Data error");
                    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                    alert.showAndWait();
                    return;
                }
                if (!budget.containsKey(key) && !type.equals("Income")) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Transaction category must be in budget.",
                            ButtonType.OK);
                    alert.setTitle("Error");
                    alert.setHeaderText("Missing budget entry");
                    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                    alert.showAndWait();
                    return;
                }

                TransactionItem item = new TransactionItem(type, key, amount, txtNotes.getText());
                transactions.add(item);
                transactionsView.getItems().add(item);

                // Add to the map as well
                if (transactionsMap.containsKey(key))
                    transactionsMap.put(key, transactionsMap.get(key) + amount);
                else
                    transactionsMap.put(key, amount);

                // Check if transaction total is near or above budget
                if (!type.equals("Income")) {
                    double categoryBudget = budget.get(key).getAmount();

                    // We need to use a - because the transactions are in negative,
                    // while the budget is a positive amount.
                    if (-transactionsMap.get(key) / categoryBudget > 1.0) {
                        Alert alert = new Alert(Alert.AlertType.WARNING,
                                "Expenses for this category have exceeded its budget.",
                                ButtonType.OK);
                        alert.setTitle("Warning");
                        alert.setHeaderText("Budget exceeded");
                        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                        alert.showAndWait();
                    } else if (-transactionsMap.get(key) / categoryBudget > 0.9) {
                        Alert alert = new Alert(Alert.AlertType.WARNING,
                                "Expenses for this category are over 90% of its budget.",
                                ButtonType.OK);
                        alert.setTitle("Warning");
                        alert.setHeaderText("Budget almost exceeded");
                        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                        alert.showAndWait();
                    }
                }
            }

            // Clear the inputs
            cmbCategory.setValue("");
            cmbType.setValue("Expense");
            txtAmount.clear();
            txtNotes.clear();
        } catch (NumberFormatException err) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid amount.", ButtonType.OK);
            alert.setTitle("Error");
            alert.setHeaderText("Data error");
            alert.showAndWait();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize items in the ComboBox and ChoiceBox
        cmbType.getItems().addAll("Expense", "Income");
        cmbType.setValue("Expense");

        cmbCategory.getItems().addAll("Travel", "Restaurant", "Groceries", "Utilities", "Rent", "Shopping",
                "Medical", "Education", "Miscellaneous");

        // If the Type is Income, then we can't have a Category.
        cmbType.setOnAction(this::onTypeChanged);

        // Set the table views
        budgetView.setItems(getBudgetItems());
        budgetCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        budgetAmountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));

        transactionsView.setItems(getTransactionItems());
        transactionsCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        transactionsAmountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
    }
}
