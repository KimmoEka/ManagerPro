package managerpro.Controller;

import managerpro.Model.Model;
import managerpro.Model.ReservationsEntity;
import managerpro.Utils.FormattingUtils;
import managerpro.Utils.LanguageUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * ReservationListController class handles the data and events related to the reservation list view.
 * An instance of this class is created when the ReservationPanel FXML is loaded.
 */

public class ReservationListController {

    /**
     * Label-node for displaying the table number.
     */
    @FXML private Label tableNumberLabel;
    /**
     * TableView- element that holds the columns and their data.
     */
    @FXML private TableView<ReservationsEntity> tableView;
    /**
     * TableColumn-node for reservation ID.
     */
    @FXML private TableColumn<ReservationsEntity,Integer> columnID;
    /**
     * TableColumn-node for reservation client name.
     */
    @FXML private TableColumn<ReservationsEntity,String> columnName;
    /**
     * TableColumn-node for reservation client phone number.
     */
    @FXML private TableColumn<ReservationsEntity,String> columnPhone;
    /**
     * TableColumn-node for reservation date.
     */
    @FXML private TableColumn<ReservationsEntity,Date> columnDate;
    /**
     * TableColumn-node for reservation starting time.
     */
    @FXML private TableColumn<ReservationsEntity,Time> columnFrom;
    /**
     * TableColumn-node for reservation ending time.
     */
    @FXML private TableColumn<ReservationsEntity,Time> columnTo;
    /**
     * TableColumn-node to act as a parent to all the other columns.
     */
    @FXML private TableColumn<String,Integer> columnTitle;
    /**
     * TextField-node used to display and edit the client's name.
     */
    @FXML private TextField nameField;
    /**
     * TextField-node used to display and edit the client's phone number.
     */
    @FXML private TextField phoneField;
    /**
     * ObservableList- variable to hold all the information to be represented in the tableview.
     */
    private ObservableList<ReservationsEntity> observableList = FXCollections.observableArrayList();
    /**
     * Holds the information of the currently selected row (reservation) inside the table view.
     */
    private ReservationsEntity selectedReservation;
    /**
     * Model is used to access the database.
     */
    private Model model;
    /**
     * Controller- object is used to call methods of the main controller.
     */
    private Controller controller;
    /**
     * Holds the information of the object that has called this class.
     */
    private Object source;
    /**
     * Holds the table ID of the currently selected table.
     */
    private int tableID;


    /**
     * Initialize-method executes the needed setup functions.
     * Sets the cellvaluefactories for each table column.
     * Sets the tableview selection model to SINGLE- selection.
     * Adds a listener to the table rows.
     */
    public void initialize(){

        columnID.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnName.setCellValueFactory(new PropertyValueFactory<>("clientName"));
        columnPhone.setCellValueFactory(new PropertyValueFactory<>("clientPhone"));
        columnDate.setCellValueFactory(new PropertyValueFactory<>("reservationDate"));
        columnFrom.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        columnTo.setCellValueFactory(new PropertyValueFactory<>("endTime"));



        columnDate.setCellFactory(column -> {
            TableCell<ReservationsEntity, Date> cell = new TableCell<ReservationsEntity, Date>() {
                private DateTimeFormatter format = FormattingUtils.getDateFormatter();

                @Override
                protected void updateItem(Date item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        TableRow<ReservationsEntity> row = getTableRow();
                        this.setText(item.toLocalDate().format(format));
                        if(item.toLocalDate().isBefore(LocalDate.now())){
                            row.setStyle("-fx-background-color: grey");
                        }
                    }
                }
            };

            return cell;
        });


        columnFrom.setCellFactory(column -> {
            TableCell<ReservationsEntity, Time> cell = new TableCell<ReservationsEntity, Time>() {
                private DateTimeFormatter format = FormattingUtils.getTimeFormatter();

                @Override
                protected void updateItem(Time item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        this.setText(item.toLocalTime().format(format));

                    }
                }
            };

            return cell;
        });

        columnTo.setCellFactory(column -> {
            TableCell<ReservationsEntity, Time> cell = new TableCell<ReservationsEntity, Time>() {
                private DateTimeFormatter format = FormattingUtils.getTimeFormatter();


                @Override
                protected void updateItem(Time item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        this.setText(item.toLocalTime().format(format));
                    }
                }
            };

            return cell;
        });


       TableView.TableViewSelectionModel<ReservationsEntity> selectionModel = tableView.getSelectionModel();

       selectionModel.setSelectionMode(SelectionMode.SINGLE);

       selectionModel.selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
           onRowSelection(newValue);
       });

    }

    /**
     * Sets the model for this class.
     * @param model Model created at main controller
     */
    public void setModel(Model model) {
        this.model = model;
    }

    /**
     * Sets the source from which this class was called
     * @param object - Either a tableInformationController or a mainInformationController
     */
    public void setSource(Object object){
        this.source = object;
    }

    /**
     * Sets the Controller for this class.
     * @param controller controller for this class
     */
    public void setController(Controller controller) {
        this.controller = controller;
    }

    /**
     * Sets the table ID of the currently selected table.
     * @param tableID table id of selected table
     */
    public void setTableID(int tableID) {
        this.tableID = tableID;
    }

    /**
     * Updates the list in table view by fetching the correct information through the model.
     */
    public void updateTable(){
        System.out.println("ReservationListController: updateTable: Updating reservation list.");
        try {
            if (source.getClass().equals(TableInformationController.class) && model.getReservationsByTableID(this.tableID) != null) {
                tableView.getItems().clear();
                observableList.addAll(model.getReservationsByTableID(this.tableID));
                tableView.setItems(observableList);
                columnTitle.setText(LanguageUtils.getResourceBundle().getString("reservations_for_table") + this.tableID);
            } else if (source.getClass().equals(MainInformationController.class) && model.getReservations() != null) {
                tableView.getItems().clear();
                observableList.addAll(model.getReservations());
                tableView.setItems(observableList);
                columnTitle.setText(LanguageUtils.getResourceBundle().getString("all_reservations"));
            }
        }catch(Exception e){
            System.out.println("ReservationListController: updateTable: Updating reservation list has failed.");
        }
    }

    /**
     * Handles the event of clicking a row in the tableview.
     * @param reservation reservation entity
     */
    public void onRowSelection(ReservationsEntity reservation){
        if(reservation != null) {
            this.selectedReservation = reservation;
            nameField.setText(reservation.getClientName());
            phoneField.setText(reservation.getClientPhone());
            tableNumberLabel.setText(LanguageUtils.getResourceBundle().getString("tables.table_number") + reservation.getTableNumber());
        }
    }

    /**
     * Handles the event of saving changes to the currently selected item.
     */
    public void saveChanges(){
        if (selectedReservation != null) {
            selectedReservation.setClientName(nameField.getText());
            selectedReservation.setClientPhone(FormattingUtils.formatPhoneNumber(phoneField.getText()));
            model.updateReservation(selectedReservation);
            clearSelections();
            updateTable();
        }
    }

    /**
     * Handles the event of closing the reservation panel.
     */
    public void closePanel(){
        if (source.getClass().equals(MainInformationController.class)){
            controller.openMainInfoPanel();
        } else if (source.getClass().equals(TableInformationController.class)){
            controller.openTableInfoPanel(this.tableID);
        }
    }

    /**
     * Clears selections and textfields.
     */
    public void clearSelections(){
        this.selectedReservation = null;
        nameField.setText("");
        phoneField.setText("");
        tableNumberLabel.setText(LanguageUtils.getResourceBundle().getString("tables.table_number"));
    }

    /**
     * Deletes the selected reservation from the database.
     */
    public void deleteReservation(){
        model.deleteReservation(selectedReservation);
        clearSelections();
        updateTable();
    }

    /**
     * Displays a confirmation dialog before deleting a reservation.
     */
    public void confirmDeletion() {
        if (selectedReservation != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(LanguageUtils.getResourceBundle().getString("confirmation"));
            alert.setHeaderText(LanguageUtils.getResourceBundle().getString("delete_this_reservation"));
            alert.setContentText(LanguageUtils.getResourceBundle().getString("selected_reservation_will_be_deleted"));

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                deleteReservation();
            }
        }
    }
}
