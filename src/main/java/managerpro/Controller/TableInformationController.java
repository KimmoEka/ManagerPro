package managerpro.Controller;

import managerpro.Model.Model;
import managerpro.Utils.LanguageUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import managerpro.Model.ReservationsEntity;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Optional;

/**
 * TableInformationController handles all the data and events related to table information panel.
 * An instance of this class is created when table information panel FXML is loaded.
 */

public class TableInformationController {


    /**
     * Holds the model of this class.
     */
    private Model model;
    /**
     * Label-node to display table title.
     */
    @FXML private Label tableTitleLabel;
    /**
     * label-node to display the date and time of the next reservation.
     */
    @FXML private Label nextReservationLabel;
    /**
     * Label-node to display the status of the table ("Free"/"Taken").
     */
    @FXML private Label statusLabel;
    /**
     * Label-node to display the client name of the current reservation.
     */
    @FXML private Label nameLabel;
    /**
     * Label-node to display the client phone number of the current reservation.
     */
    @FXML private Label phoneLabel;
    /**
     * Label-node to display the date of the current reservation.
     */
    @FXML private Label dateLabel;
    /**
     * Label-node to display the starting and ending time of the current reservation.
     */
    @FXML private Label timeLabel;
    /**
     * Controller- variable of this class.
     */
    private Controller controller;
    /**
     * Holds the ReservationEntity-object of the current reservation
     */
    private ReservationsEntity currentReservation;
    /**
     * Holds the information of the table ID.
     */
    private int tableID;


    /**
     * Sets the controller for this class.
     * @param controller Controller to set
     */
    public void setController(Controller controller){
        this.controller = controller;
    }

    /**
     * Sets the model for this class.
     * @param model Model -class created at Main controller
     */
    public void setModel(Model model){
        this.model = model;
    }

    /**
     * Sets the tableID of the currently selected table
     * @param tableID Table id of current table
     */
    public void setTableID(int tableID){
        this.tableID = tableID;
    }

    /**
     * Updates the table panel based on the table ID parameter. Uses model-object to fetch reservations for the provided table.
     * Sorts out the active reservations and displays the current reservation if there is any. Also displays the next reservation date and time if there is any.
     * @param tableID table id to update
     */
    public void updateTableInformationPanel(int tableID){
        ArrayList<ReservationsEntity> reservations = model.getReservationsByTableID(tableID);
        ArrayList<ReservationsEntity> activeReservations = new ArrayList<>();
        ArrayList<ReservationsEntity> pastReservations = new ArrayList<>();
        tableTitleLabel.setText(LanguageUtils.getResourceBundle().getString("tables.table") + tableID);
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        if (reservations != null) {
            for (ReservationsEntity reservation : reservations) {
                if (reservation.getReservationDate().toLocalDate().isBefore(today) || (reservation.getReservationDate().toLocalDate().equals(today) && reservation.getEndTime().toLocalTime().isBefore(now))) {
                    pastReservations.add(reservation);
                } else {
                    activeReservations.add(reservation);
                }
            }
        }
        if(!activeReservations.isEmpty()){
            ReservationsEntity firstReservation = activeReservations.get(0);
            if(firstReservation.getStartTime().toLocalTime().isBefore(now) && firstReservation.getReservationDate().toLocalDate().equals(today)){
                this.currentReservation = firstReservation;
                statusLabel.setText(LanguageUtils.getResourceBundle().getString("taken_until") + firstReservation.getEndTime().toLocalTime().toString());
                nameLabel.setText(firstReservation.getClientName());
                phoneLabel.setText(firstReservation.getClientPhone());
                dateLabel.setText(firstReservation.getReservationDate().toLocalDate().toString());
                timeLabel.setText(LanguageUtils.getResourceBundle().getString("form_ws") + firstReservation.getStartTime().toLocalTime().toString() + " " +LanguageUtils.getResourceBundle().getString("to_lowercase") +" "+ firstReservation.getEndTime().toLocalTime().toString());
                if(activeReservations.size() >= 2){
                    String nextReservationInfo = activeReservations.get(1).getReservationDate().toLocalDate().toString() +" "+LanguageUtils.getResourceBundle().getString("at_lowercase")+" "+ activeReservations.get(1).getStartTime().toLocalTime().toString();
                    nextReservationLabel.setText(nextReservationInfo);
                }
            }else{
                statusLabel.setText(LanguageUtils.getResourceBundle().getString("free"));
                String nextReservationInfo = activeReservations.get(0).getReservationDate().toLocalDate().toString() +" "+LanguageUtils.getResourceBundle().getString("at_lowercase")+" "+ activeReservations.get(0).getStartTime().toLocalTime().toString();
                nextReservationLabel.setText(nextReservationInfo);
                nameLabel.setText(LanguageUtils.getResourceBundle().getString("double_dash"));
                phoneLabel.setText(LanguageUtils.getResourceBundle().getString("double_dash"));
                dateLabel.setText(LanguageUtils.getResourceBundle().getString("double_dash"));
                timeLabel.setText(LanguageUtils.getResourceBundle().getString("double_dash"));
            }
        } else{
            statusLabel.setText(LanguageUtils.getResourceBundle().getString("free"));
            nextReservationLabel.setText(LanguageUtils.getResourceBundle().getString("double_dash"));
            nameLabel.setText(LanguageUtils.getResourceBundle().getString("double_dash"));
            phoneLabel.setText(LanguageUtils.getResourceBundle().getString("double_dash"));
            dateLabel.setText(LanguageUtils.getResourceBundle().getString("double_dash"));
            timeLabel.setText(LanguageUtils.getResourceBundle().getString("double_dash"));
        }
    }

    /**
     * Deletes the current reservation.
     */
    public void freeTable(){
        model.deleteReservation(this.currentReservation);
        updateTableInformationPanel(this.tableID);
    }

    /**
     * Displays a confirmation dialog before deleting a reservation.
     */
    public void confirmReset() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(LanguageUtils.getResourceBundle().getString("confirmation"));
        alert.setHeaderText(LanguageUtils.getResourceBundle().getString("free_up_question"));
        alert.setContentText(LanguageUtils.getResourceBundle().getString("current_reservation_will_be_deleted"));

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            freeTable();
        }
    }

    /**
     * Loads the reservations panel with reservations for the currently selected table.
     */
    public void openAllReservationsPanel(){
        controller.openReservationsPanel(this);
    }

}
