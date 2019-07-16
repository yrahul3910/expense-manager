package sample;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    public ChoiceBox<String> cmbType;
    public ComboBox<String> cmbCategory;
    public TextField txtAmount;
    public Button btnAddEntry;

    public void onAddEntryClicked() {
        System.out.println("Lorem ipsum");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize items in the ComboBox and ChoiceBox
        cmbType.getItems().addAll("Expense", "Income");
        cmbType.setValue("Expense");

        cmbCategory.getItems().addAll("Travel", "Restaurant", "Groceries", "Utilities", "Rent", "Shopping",
                "Medical", "Education", "Miscellaneous");

        // If the Type is Income, then we can't have a Category.
        cmbType.setOnAction(e -> {
            if (cmbType.getValue().equals("Income"))
                cmbCategory.setDisable(true);
            else
                cmbCategory.setDisable(false);
        });
    }
}
