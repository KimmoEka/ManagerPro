package managerpro.Controller;

import managerpro.Model.*;
import managerpro.Utils.LanguageUtils;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import org.hibernate.SessionFactory;
import managerpro.View.MainApp;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;


/**
 * The Controller class is the main controller of the main user interface. An instance of this class
 * is first loaded when the user logged in and the main screen of the application loads. It is responsible for updating the tablescreen as well as loading the
 * information panels which are inserted on the right side of the window.
 *
 * The same controller instance is passed to the panels that are loaded during the execution of the program.
 *
 * @author OTP7
 */
public class Controller {

	/**
	 * AnchorPane into which the other information panels ( table information, main information and reservation listing) are loaded.
	 */
	@FXML
	private AnchorPane informationPanel;
	/**
	 * Tab-node for user creation.
	 */
	@FXML
	private Tab createUserTab;
	/**
	 * The controller of the user creation tab.
	 */
	@FXML
	private CreateUserController createUserController;
	/**
	 * Button-node for table 1.
	 */
	@FXML
	private Button table1;
	/**
	 * Button-node for table 2.
	 */
	@FXML
	private Button table2;
	/**
	 * Button-node for table 3.
	 */
	@FXML
	private Button table3;
	/**
	 * Button-node for table 4.
	 */
	@FXML
	private Button table4;
	/**
	 * Button-node for table 5.
	 */
	@FXML
	private Button table5;
	/**
	 * Button-node for table 6.
	 */
	@FXML
	private Button table6;
	/**
	 * Holds the information of the currently selected table.
	 */
	private static int currentTable = 0;
	/**
	 * ArrayList into which all the button-nodes are added. Used for dynamic accessing.
	 */
	private ArrayList<Button> tableElements = new ArrayList<>();
	/**
	 * ArrayLis of Strings pointing to FXML-resources for the FXML-files that are used as resources.
	 */
	private ArrayList<String> FXMLArray = new ArrayList<>();
	/**
	 * Model holds the functionality to access the database and fetch data.
	 */
	public static Model model;


	/**
	 * Constructor of the Controller.
	 * Automatically gets the sessionfactory from the Login Controller and passes it into model constructor as a parameter.
	 */
	public Controller() {
		Model.setSessionFactory(LoginController.getSessionFactory());
		try {
			model = Model.getInstance();
		}catch (Exception e){
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Initialize- method is called when the FXML-file is loaded. Executes the required setup functions.
	 */
	public void initialize() {
		tableElements.add(table1);
		tableElements.add(table2);
		tableElements.add(table3);
		tableElements.add(table4);
		tableElements.add(table5);
		tableElements.add(table6);
		FXMLArray.add("/mainInformation.fxml");
		FXMLArray.add("/tableInformation.fxml");
		FXMLArray.add("/reservationsPanel.fxml");
		updateTableView();
	}

	/**
	 * Handles the event when table element is clicked.
	 * If the main information panel is active, loads table information panel.
	 * If the table information panel is active, loads main information panel instead.
	 * Sets the style of the clicked table, as well as the tables that were not clicked.
	 *
	 * @param event - An event that is fired when a table is clicked.
	 */
	@FXML
	public void tableClick(Event event) {
		Object source = event.getSource();
		Button clickedButton = (Button) source;
		if(currentTable != Integer.parseInt(clickedButton.getText())){
			for(Button button : tableElements){
				if(button.equals(clickedButton)){
					button.setStyle("-fx-background-color: lightgreen");
					currentTable = Integer.parseInt(button.getText());
					openTableInfoPanel(currentTable);
				}else{
					button.setStyle("-fx-background-color: grey");
				}
			}
		}else{
			currentTable = 0;
			openMainInfoPanel();
			updateTableView();
		}
	}

	/**
	 * Method that handles the event of selecting user creation tab.
	 */
	@FXML
	public void onTabSelectionChanged() {
		if (createUserTab.isSelected()) {
			Platform.runLater(() -> createUserController.resetForm());
		}
	}

	/**
	 * Loads the table information panel into the side panel.
	 *
	 * @param tableID - Id of the table that has been selected. The information inside this panel
	 *                is updated based on this parameter.
	 */
	@FXML
	public void openTableInfoPanel(int tableID) {
		try {
			FXMLLoader loader = new FXMLLoader(Controller.class.getResource(FXMLArray.get(1)), LanguageUtils.getResourceBundle());
			GridPane gridPane = loader.load();
			informationPanel.getChildren().setAll(gridPane);
			TableInformationController tableInformationController = loader.getController();
			tableInformationController.setModel(model);
			tableInformationController.updateTableInformationPanel(tableID);
			tableInformationController.setController(this);
			tableInformationController.setTableID(tableID);
		} catch (Exception e) {
			System.out.println("Error while loading table information panel");
			e.printStackTrace();
		}
	}

	/**
	 * Loads the main information panel into the side panel.
	 */
	public void openMainInfoPanel() {
		try {
			FXMLLoader loader = new FXMLLoader(Controller.class.getResource(FXMLArray.get(0)), LanguageUtils.getResourceBundle());
			GridPane gridPane = loader.load();
			informationPanel.getChildren().setAll(gridPane);
			MainInformationController mainInformationController = loader.getController();
			mainInformationController.setModel(model);
			mainInformationController.setController(this);
			mainInformationController.updateActiveReservationsLabel();
		} catch (Exception e) {
			System.out.println("Error while loading main information panel");
			e.printStackTrace();
		}
	}

	/**
	 * Method for showing a confirmation window for logout. Returns user to loginwindow on successful logout.
	 *
	 * @throws IOException Throws IOException if resource not found
	 */
	@FXML
	public void logOut() throws IOException {
		ResourceBundle bundle = LanguageUtils.getResourceBundle();
		ButtonType yes = new ButtonType(bundle.getString("okbutton"), ButtonBar.ButtonData.OK_DONE);
		ButtonType no = new ButtonType(bundle.getString("nobutton"), ButtonBar.ButtonData.CANCEL_CLOSE);
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION, bundle.getString("logout_text"), yes, no);
		alert.setTitle(bundle.getString("logout_title"));
		alert.setHeaderText(bundle.getString("logout_header"));

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == yes) {
			MainApp.switchLoginView();
		}
	}


	/**
	 * Loads the reservation listing into the side panel.
	 * This same method (and FXML-file) is accessed from two different classes:
	 * 1. From the table information controller, to open the reservation list for that specific table.
	 * 2. From the main information controller, to open the resevation list to see all reservations.
	 *
	 * @param object - the controller object is passed as a parameter so that the reservation list knows which panel is the source of the request
	 *               and what data should be presented.
	 */
	public void openReservationsPanel(Object object) {
		try {
			FXMLLoader loader = new FXMLLoader(Controller.class.getResource(FXMLArray.get(2)), LanguageUtils.getResourceBundle());
			GridPane gridPane = loader.load();
			informationPanel.getChildren().setAll(gridPane);
			System.out.println("Attempting to load controller for reservation list");
			ReservationListController reservationController = loader.getController();
			System.out.println("Attempting to set model");
			reservationController.setModel(model);
			reservationController.setController(this);
			reservationController.setSource(object);
			reservationController.setTableID(currentTable);
			if (object.getClass().equals(TableInformationController.class)) {
				reservationController.updateTable();
			} else if (object.getClass().equals(MainInformationController.class)) {
				reservationController.updateTable();
			}
		} catch (Exception e) {
			System.out.println("Controller: openReservationsPanel: Error while loading reservation list panel");
			e.printStackTrace();
		}
	}


	/**
	 * A method for updating the 'table' buttons shown on the Tables -tab. If model had current reservations (reservations that have not passed), this method gets a list
	 * of them from the model and sets the table colour to reflect the status of the table (red=taken, green=free).
	 */
	public void updateTableView(){
		if(model.getCurrentReservations() != null){
			ArrayList<ReservationsEntity> currentReservations = model.getCurrentReservations();
			for(ReservationsEntity reservation : currentReservations){
				for(Button table : this.tableElements){
					if(reservation.getTableNumber().equals(Integer.valueOf(table.getText()))){
						table.setStyle("-fx-background-color: red");
					}else{
						table.setStyle("-fx-background-color: green");
					}
				}
			}
		}else{
			for(Button table : this.tableElements){
				table.setStyle("-fx-background-color: green");
			}
		}
	}
}
