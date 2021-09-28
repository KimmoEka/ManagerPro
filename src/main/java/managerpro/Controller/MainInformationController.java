package managerpro.Controller;

import managerpro.Model.Model;
import managerpro.Utils.FormattingUtils;
import managerpro.Utils.LanguageUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.Time;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import managerpro.Model.ReservationsEntity;
import managerpro.Model.TablesEntity;

/**
 * MainInformationController class is handles the events and data related to the main information panel.
 * An instance of this class is created when the main information panel is loaded.
 */

public class MainInformationController {

    /**
     * ComboBox of the type String to hold the starting times of the reservation.
     */
    @FXML private ComboBox<String> reservationTimeFrom;
    /**
     * ComboBox of the type String to hold the ending times of the reservation.
     */
    @FXML private ComboBox<String> reservationTimeTo;
    /**
     * Label-node to display the amount of active reservations.
     */
    @FXML private Label activeReservationsAmountLabel;
    /**
     * Textfield-node for inserting the clients name.
     */
    @FXML private TextField clientNameField;
    /**
     * Textfield-node for inserting the clients phone number.
     */
    @FXML private TextField clientPhoneField;
    /**
     * Datepicker-node for inserting the date of the reservation.
     */
    @FXML private DatePicker reservationDatePicker;
    /**
     * Label-node for displaying the currently selected table.
     */
    @FXML private Label selectedTableLabel;
    /**
     * Button-node for selecting the table size.
     */
    @FXML private Button tableSize2Button;
    /**
     * Button-node for selecting the table size.
     */
    @FXML private Button tableSize4Button;
    /**
     * Button-node for selecting the table size.
     */
    @FXML private Button tableSize6Button;
    /**
     * ArrayList to hold all the table size buttons. USed for dynamic accessing.
     */
    private ArrayList<Button>tableSizeButtons = new ArrayList<>();
    /**
     * ObservableList to hold all the items displayed by ComboBox- nodes.
     */
    private ObservableList<String> hourItems;
    /**
     * The Model which is used to fetch data from the database.
     */
    private Model model;
    /**
     * TablesEntity-type variable into which the currently selected table is stored.
     */
    private TablesEntity selectedTable;
    /**
     * Integer-type variable that holds the currently selected table size.
     */
    private int selectedTableSize;
    /**
     * Controller-variable that is passed in to Main Information Controller for later use.
     */
    private Controller controller;


    /**
     * Initialize-method executes the needed setup functions when the FXML-file is loaded.
     * 1.Populating ComboBoxes.
     * 2.Populating tableSizeButtons-arraylist.
     * 3. Initiating the listeners.
     * 4. Disabling past dates from the date picker.
     */
    public void initialize(){
        Locale.setDefault(LanguageUtils.getLocale());
        //Moved the hourItems initialization to here, the format of the hour items will depend on locale
        hourItems = FXCollections.observableArrayList();
        //How long the restaurant will take revervations for?
        int reservationHours = 12;
        //When can the first reservation be made (Hours after midnight
        int firstReservation = 10;
        //Add the hours to the list, format them using a DateTimeFormatter from the LanguageUtils
        for(int i=0;i<reservationHours;i++){
            hourItems.add(LocalTime.of(firstReservation+i,0).format(FormattingUtils.getTimeFormatter()));
        }

        reservationTimeFrom.setItems(hourItems);
        reservationTimeTo.setItems(hourItems);
        tableSizeButtons.add(tableSize2Button);
        tableSizeButtons.add(tableSize4Button);
        tableSizeButtons.add(tableSize6Button);
        initListeners();

        //NOTE: This method of disabling past dates in date picker is from stack overflow: https://stackoverflow.com/questions/48238855/how-to-disable-past-dates-in-datepicker-of-javafx-scene-builder
        reservationDatePicker.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.compareTo(today) < 0 );
            }
        });


    }

    /**
     * Sets the model of this class.
     * @param model Model created at MainController
     */
    public void setModel(Model model){
        this.model = model;
    }

    /**
     * Sets the controller of this class.
     * @param controller controller for this class
     */
    public void setController(Controller controller){
        this.controller = controller;
    }


    /**
     * Handles the event of selecting a table size.
     * @param event JavaFX Event
     */
    public void selectTableSize(Event event){
        for (Button button : tableSizeButtons){
            if(event.getSource().equals(button)){
                button.setStyle("-fx-background-color: lightblue");
                int tableSize = Integer.parseInt(button.getText());
                if(this.selectedTableSize != tableSize){
                    this.selectedTable = null;
                    this.selectedTableSize = tableSize;
                    updateSelectedTableLabel();
                }
            } else {
                button.setStyle("-fx-background-color: grey");
            }
        }
    }

    /**
     * Handles the event of clicking the "Fetch table"- button in the interface.
     * Evaluates the user input, checking that there is no errors in date or time.
     * Gets the the tables from the model that match the criteria.
     * Sets one of the available tables as selected.
     */
    public void fetchTable(){
        if(this.selectedTableSize == 0 || reservationDatePicker.getValue() == null || reservationTimeFrom.getSelectionModel().getSelectedItem() == null || reservationTimeTo.getSelectionModel().getSelectedItem() == null){
            displayErrorDialog(LanguageUtils.getResourceBundle().getString("error_in_form"),LanguageUtils.getResourceBundle().getString("form_error_empty_fields"));
        } else if (reservationTimeFrom.getSelectionModel().getSelectedItem().equals(reservationTimeTo.getSelectionModel().getSelectedItem())){
            displayErrorDialog(LanguageUtils.getResourceBundle().getString("error_in_form"), LanguageUtils.getResourceBundle().getString("form_error_same_times"));
        } else if(LocalTime.parse(reservationTimeTo.getSelectionModel().getSelectedItem(),FormattingUtils.getTimeFormatter()).isBefore(LocalTime.parse(reservationTimeFrom.getSelectionModel().getSelectedItem(),FormattingUtils.getTimeFormatter()))){
            displayErrorDialog(LanguageUtils.getResourceBundle().getString("error_in_form"),LanguageUtils.getResourceBundle().getString("form_error_bad_times"));
        } else if(Date.valueOf(reservationDatePicker.getValue()).toLocalDate().equals(LocalDate.now()) && LocalTime.parse(reservationTimeFrom.getSelectionModel().getSelectedItem(),FormattingUtils.getTimeFormatter()).isBefore(LocalTime.now())){
            displayErrorDialog(LanguageUtils.getResourceBundle().getString("error_in_form"),LanguageUtils.getResourceBundle().getString("form_error_bad_date"));
        }else{
            ArrayList<TablesEntity> tablesBySize = new ArrayList<>(model.getTablesBySize(this.selectedTableSize));
            ArrayList<TablesEntity> unavailableTables = new ArrayList<>();
            ArrayList<TablesEntity> availableTables = new ArrayList<>();

            for (TablesEntity table : tablesBySize) {
                ArrayList<ReservationsEntity> reservationsForTable = model.getReservationsByTableID(table.getId());
                boolean available = true;
                if (reservationsForTable != null) {
                    for (ReservationsEntity reservation : reservationsForTable) {
                        /*
                        //The sTime and eTime are now parsed from the comboboxes with getTimeFormatter....
                        LocalTime sTime = LocalTime.parse(reservationTimeFrom.getSelectionModel().getSelectedItem(),LanguageUtils.getTimeFormatter());
                        LocalTime eTime = LocalTime.parse(reservationTimeTo.getSelectionModel().getSelectedItem(),LanguageUtils.getTimeFormatter());
                        //then turned to java.sql.Time.....
                        Time startTime = Time.valueOf(sTime);
                        Time endTime = Time.valueOf(eTime);
                        //and then back to local time...
                        LocalTime localStartTime = startTime.toLocalTime();
                        LocalTime localEndTime = endTime.toLocalTime();
                        */
                        LocalTime localStartTime = LocalTime.parse(reservationTimeFrom.getSelectionModel().getSelectedItem(),FormattingUtils.getTimeFormatter());
                        LocalTime localEndTime = LocalTime.parse(reservationTimeTo.getSelectionModel().getSelectedItem(),FormattingUtils.getTimeFormatter());

                        LocalTime reservationStartTime = reservation.getStartTime().toLocalTime();
                        LocalTime reservationEndTime = reservation.getEndTime().toLocalTime();

                        //TODO Make this code block more readable
                        if (reservation.getReservationDate().equals(Date.valueOf(reservationDatePicker.getValue())) && (((localStartTime.equals(reservationStartTime) || (localStartTime.isAfter(reservationStartTime) && localStartTime.isBefore(reservationEndTime)))) || (localEndTime.equals(reservationEndTime) || (localEndTime.isAfter(reservationStartTime) && localEndTime.isBefore(reservationEndTime))) || (localStartTime.isBefore(reservationStartTime) && localEndTime.isAfter(reservationEndTime)))) {
                            unavailableTables.add(table);
                            available = false;
                        }
                    }
                } else{
                    System.out.println(LanguageUtils.getResourceBundle().getString("no_reservations_found") + table.getId());
                }
                if (available){
                    availableTables.add(table);
                }
            }

            if(availableTables.isEmpty()){
                displayInformationDialog(LanguageUtils.getResourceBundle().getString("no_free_tables"),LanguageUtils.getResourceBundle().getString("no_free_tables_context_text"));
            } else{
                List<String> temp = new ArrayList<>();
                //Translate the TableEntity toString output!
                for(TablesEntity t : availableTables)
                    temp.add(LanguageUtils.translateToString(t));
                System.out.println(LanguageUtils.getResourceBundle().getString("table_options") + temp.toString());
                this.selectedTable = availableTables.get(0);
                updateSelectedTableLabel();
            }
        }
    }

    /**
     * Handles the event of saving a reservation. Evaluates user input and checks that there is no empty data fields.
     * Fires a confirmation dialog before saving the reservation.
     */
    public void saveReservation() {
        if (clientNameField.getText().equals("") || clientPhoneField.getText().equals("") || reservationDatePicker.getValue() == null || reservationTimeFrom.getSelectionModel().getSelectedItem() == null || selectedTable == null) {
            displayErrorDialog(LanguageUtils.getResourceBundle().getString("error_in_form"), LanguageUtils.getResourceBundle().getString("form_error_empty_fields_2"));
        }else if(reservationTimeFrom.getSelectionModel().getSelectedItem().equals(reservationTimeTo.getSelectionModel().getSelectedItem())){
            displayErrorDialog(LanguageUtils.getResourceBundle().getString("error_in_form"),LanguageUtils.getResourceBundle().getString("form_error_same_times_2"));
        }else {
            ReservationsEntity reservation = new ReservationsEntity();
            reservation.setClientName(clientNameField.getText());
            reservation.setClientPhone(FormattingUtils.formatPhoneNumber(clientPhoneField.getText()));

            LocalTime sTime = LocalTime.parse(reservationTimeFrom.getSelectionModel().getSelectedItem(),FormattingUtils.getTimeFormatter());
            LocalTime eTime = LocalTime.parse(reservationTimeTo.getSelectionModel().getSelectedItem(),FormattingUtils.getTimeFormatter());

            reservation.setTableNumber(selectedTable.getId());

            try {
                reservation.setReservationDate(Date.valueOf(reservationDatePicker.getValue()));
                reservation.setStartTime(Time.valueOf(sTime));
                reservation.setEndTime(Time.valueOf(eTime));
            } catch (Exception e) {
                System.out.println(LanguageUtils.getResourceBundle().getString("date_parse_error"));
            }

            System.out.println("About to save the following object:\n" + reservation.toString() +"\nPrompting confirmation...");

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(LanguageUtils.getResourceBundle().getString("confirmation"));
            alert.setHeaderText(LanguageUtils.getResourceBundle().getString("is_this_correct"));
            alert.setContentText(LanguageUtils.getResourceBundle().getString("please_review"));

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                model.addReservation(reservation);
                resetForm();
                displaySaveDialog();
                updateActiveReservationsLabel();
            }
        }
    }

    /**
     * Listener that checks if the value in date picker changes. If the date value has changed, resets the selected table for re-evaluation.
     */
    public void initListeners(){
        reservationDatePicker.valueProperty().addListener((observable, oldValue, newValue)->{
            System.out.println("Old value: " + oldValue + "\nNew value: " + newValue);
            if(oldValue != newValue){
                this.selectedTable = null;
                updateSelectedTableLabel();
            }
        });
        /**
         * Listener that checks if the value in ComboBox(start time) changes. If the time value has changed, resets the selected table for re-evaluation.
         */
        reservationTimeFrom.valueProperty().addListener((observable,oldValue,newValue) ->{
            System.out.println("Old value: " + oldValue + "\nNew value: " + newValue);
            try {
                if (!oldValue.equals(newValue)) {
                    this.selectedTable = null;
                    updateSelectedTableLabel();
                }
            }catch (NullPointerException e){
                System.out.println("The old value was null");
            }
        });

        /**
         * Listener that checks if the value in ComboBox(end time) changes. If the time value has changed, resets the selected table for re-evaluation.
         */
        reservationTimeTo.valueProperty().addListener((observable,oldValue,newValue) ->{
            System.out.println("Old value: " + oldValue + "\nNew value: " + newValue);
            try {
                if (!oldValue.equals(newValue)) {
                    this.selectedTable = null;
                    updateSelectedTableLabel();
                }
            }catch (NullPointerException e){
                System.out.println("The old value was null");
            }
        });
    }

    /**
     * Calls for the controller- object to open reservations panel for all reservations. Fired when user clicks on the "See All Reservations"-button.
     */
    public void openAllReservationsPanel(){
        controller.openReservationsPanel(this);
    }

    /**
     * Updates the selected table label.
     */
    public void updateSelectedTableLabel(){
        if(selectedTable == null){
            selectedTableLabel.setText(LanguageUtils.getResourceBundle().getString("no_selected_table"));
        } else {
            selectedTableLabel.setText(LanguageUtils.getResourceBundle().getString("tables.selected_table") + selectedTable.getId());
        }
    }

    /**
     * Displays a confirmation dialog before calling resetForm-method.
     */
    public void confirmReset() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(LanguageUtils.getResourceBundle().getString("confirmation"));
        alert.setHeaderText(LanguageUtils.getResourceBundle().getString("clear_all_question"));
        alert.setContentText(LanguageUtils.getResourceBundle().getString("no_undo_warning"));

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            resetForm();
        }
    }

    /**
     * Updates the active reservations amount label.
     */
    public void updateActiveReservationsLabel(){
        if(model.getActiveReservations() == null){
            activeReservationsAmountLabel.setText(LanguageUtils.getResourceBundle().getString("no_active_reservations"));
        }else{
            activeReservationsAmountLabel.setText(LanguageUtils.getResourceBundle().getString("active_reservations") + model.getActiveReservations().size());
        }
    }

    /**
     * Resets all selected values and clears the values in the form.
     */
    public void resetForm(){
        clientNameField.setText("");
        clientPhoneField.setText("");
        selectedTableLabel.setText("");
        reservationDatePicker.setValue(null);
        reservationTimeFrom.getSelectionModel().clearSelection();
        reservationTimeTo.getSelectionModel().clearSelection();
        selectedTable = null;
        //Reset table size button color
        selectedTableSize = 0;
    }

    /**
     * Displays an information dialog.
     */
    public void displaySaveDialog(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(LanguageUtils.getResourceBundle().getString("information"));
        alert.setHeaderText(LanguageUtils.getResourceBundle().getString("success_exclamation"));
        alert.setContentText(LanguageUtils.getResourceBundle().getString("new_reservation_added_to_database"));
        alert.showAndWait();
    }

    /**
     * Displays a custom error dialog
     * @param headerText - String parameter to be displayed as header.
     * @param contentText - String parameter to be displayed as content.
     */
    public void displayErrorDialog(String headerText,String contentText){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(LanguageUtils.getResourceBundle().getString("error"));
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    /**
     * Displays a custom information dialog.
     * @param headerText - String parameter to be displayed as header.
     * @param contentText - String parameter to be displayed as content.
     */
    public void displayInformationDialog(String headerText, String contentText){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(LanguageUtils.getResourceBundle().getString("information"));
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

}
