package managerpro.Controller;

import managerpro.Model.*;
import managerpro.Utils.FormattingUtils;
import managerpro.Utils.GUIUtils;
import managerpro.Utils.LanguageUtils;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller class for the Portions tab of the user interface. It is used to add, remove and modify portions in the system, allowing the user to
 * create a Menu for the restaurant.
 *
 * @author OTP7
 */
public class PortionsController {
    /**
     * JavaFX TextField that holds the name of the portion
     */
    @FXML private TextField name;

    /**
     * JavaFX TextField that holds the price of the portion
     */
    @FXML private TextField price;

    /**
     * JavaFX TableView that holds a table of all the portions in the database
     */
    @FXML private TableView<PortionsEntity> table;

    /**
     * PortionsEntity to indicate a selected portion
     */
    private PortionsEntity selectedPortion;
    private IPortionsDao portionsDao;

    /**
     * FXML initializer method. Instantiates portionsDao, adds the columns to TableView, sets TableViews selection model to one row
     *  Adds a listener for clicks and calls {@link #refreshTable()}.
     */
    @FXML
    public void initialize () {
        if (portionsDao == null) {
            portionsDao = new PortionsDao(LoginController.getSessionFactory());
        }

        // Add columns to table
        TableColumn<PortionsEntity, String> column1 = new TableColumn<>(LanguageUtils.getString("name.Portions_Controller"));
        column1.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<PortionsEntity, String> column2 = new TableColumn<>(LanguageUtils.getString("price.Portions_Controller"));
        column2.setCellValueFactory(price -> {
            SimpleStringProperty property = new SimpleStringProperty();
            property.setValue(FormattingUtils.formatCurrency(price.getValue().getPrice()));
            return property;
        });

        table.getColumns().add(column1);
        table.getColumns().add(column2);

        TableView.TableViewSelectionModel<PortionsEntity> selectionModel = table.getSelectionModel();

        // set selection mode to only 1 row
        selectionModel.setSelectionMode(SelectionMode.SINGLE);

        // Listen to table row click
        selectionModel.selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
                handleRowSelection(newValue);
        });
        refreshTable();
    }

    /**
     * Method for refreshing a a TableView. Gets all the portions form the database and adds each of them to the tableView. Then calls
     * {@link managerpro.Utils.GUIUtils} to resize the columns to appropriate lengths.
     */
    public void refreshTable() {
        List<PortionsEntity> portions = portionsDao.getAllPortions();
            table.getItems().clear();

        for (PortionsEntity portion : portions) {
            table.getItems().add(portion);
        }

        GUIUtils.autoResizeColumns(table);

    }

    /**
     * When a row is clicked, this.selectedPortion gets set to parameter, TextField name gets set to the portions name and TextField Price gets set
     * to portions price.
     * @param selectedPortion selectedPortion
     */

    private void handleRowSelection(PortionsEntity selectedPortion) {
        this.selectedPortion = selectedPortion;
        name.setText(selectedPortion == null ? "" : selectedPortion.getName());
        price.setText(String.valueOf(selectedPortion == null ? "" : selectedPortion.getPrice()));
    }

    /**
     * Gets the name and the price from the UI and uses portionsDao to create te portion to database.
     * Then resets tha name and price fields.
     */
    @FXML
    public void createPortion() {
        portionsDao.createPortion(new PortionsEntity(name.getText(), Double.parseDouble(price.getText())));
        selectedPortion = null;
        name.setText("");
        price.setText("");
        refreshTable();
    }

    /**
     * Updates the selectedPortion with the values in the UI, updates the database with the dao and resets the fields.
     */
    @FXML
    public void updatePortion() {
        if (selectedPortion == null || selectedPortion.getId() == 0) return;
        selectedPortion.setName(name.getText());
        selectedPortion.setPrice(Double.parseDouble(price.getText()));
        portionsDao.updatePortion(selectedPortion);
        selectedPortion = null;
        name.setText("");
        price.setText("");
        refreshTable();
    }

    /**
     * Deletes the selected portion from the database and resets the UI fields.
     */
    @FXML
    public void deletePortion() {
        if (selectedPortion == null || selectedPortion.getId() == 0) return;
        portionsDao.deletePortion(selectedPortion);
        selectedPortion = null;
        name.setText("");
        price.setText("");
        refreshTable();
    }
    /* Used for testing */

    public void setName(TextField name) {
        this.name = name;
    }

    public void setPrice(TextField price) {
        this.price = price;
    }
    public void setTable(TableView table) {
        this.table = table;
    }
    public void setSelectedPortion(PortionsEntity selectedPortion) {
        this.selectedPortion = selectedPortion;
    }

    public void setPortionsDao(IPortionsDao portionsDao) {
        this.portionsDao = portionsDao;
    }

    public PortionsEntity getSelectedPortion() {
        return this.selectedPortion;
    }

}
