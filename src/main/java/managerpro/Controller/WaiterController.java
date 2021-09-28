package managerpro.Controller;

import managerpro.Model.*;
import managerpro.Utils.LanguageUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller class for the Waiter view. In waiter view, all the current orders are visible. The orders are moved around on swimlanes based on their status
 * The GUI is updated similarly to Kitchen controller; when the propertyChangeListener is fired, the intermediary data stores are updated and the updateGui method called.
 * @author OTP7
 */
public class WaiterController {
    //Waiter view FXML nodes
    /**
     * FXML Node for waiterButtonsVBox. Holds the controls for opening 'new order' view and advancing a order on the 'swimlane'
     */
    @FXML VBox waiterButtonsVBox;
    /**
     * FXML Node for waiterOrdersVBox. Holds the orders that are currently waiting to be made.
     */
    @FXML VBox waiterOrdersVBox;
    /**
     * FXML Node for waiterReadyVBox. Holds the orders that are currently ready to be served
     */
    @FXML VBox waiterReadyVBox;
    /**
     * FXML Node for waiterServedVBox. Holds the orders that are currently served.
     */
    @FXML VBox waiterServedVBox;
    /**
     * FXML Node for waiterPaidVBox. Holds the orders that are paid.
     */
    @FXML VBox waiterPaidVBox;
    /**
     * FXML Node for newOrderTableLabel. Label that shows the table the order is being made to
     */
    @FXML Label newOrderTableLabel;
    //New Order view FXML nodes
    /**
     * FXML Node for newOrderTablesVBox. VBox that holds all the tables.
     */
    @FXML VBox newOrderTablesVBox;
    /**
     * FXML Node for newOrderOpVBox. VBox that hold the portions that are selected for the order.
     */
    @FXML VBox newOrderOpVBox;
    /**
     * FXML Node for newOrderPortionsVBox. VBox that holds a list of the available portions.
     */
    @FXML VBox newOrderPortionsVBox;

    //More waiter view nodes
    /**
     * FXML Node for newOrderBtn. Button that opens the new order -view
     */
    @FXML Button newOrderBtn;
    /**
     * FXML Node for advanceOrderBtn. Button that moves the selected order to the next swimlane.
     */
    @FXML Button advanceOrderBtn;
    /**
     * FXML Node for confirmOrder. Button that creates the order and it's ordered portions to database
     */
    @FXML Button confirmOrderBtn;
    /**
     * FXML Node for waiterGridPane. GridPane that is used to create the swimlane ui.
     */
    @FXML GridPane waiterGridPane;
    /**
     * FXML Node for waiterAnchorPane. AnchorPane that is used to hold all the elements of the Waiter view.
     */
    @FXML AnchorPane waiterAnchorPane;
    /**
     * FXML Node for newOrderAnchorPane. AnchorPane that is hidden by default, but made visible when the new order -button is pressed.
     */
    @FXML AnchorPane newOrderAnchorPane;

    /**
     * An OrdersEntity private field, that holds the selected ordersEntity -object
     */
    private OrdersEntity selectedOrder;

    /**
     * An OrderedPortionsEntity private field, that holds the selected orderedPortionsEntity -object
     */
    private OrderedPortionsEntity selectedOp;
    /**
     * An ArrayList of OrderedPortionsEntities, that is used to populate the orderedPortionsFlowPane
     */
    private ArrayList<OrdersEntity> orders;
    /**
     * Reference to a instance of a singleton Model -class.
     */
    private Model model;

    /**
     * An integer, that holds the ID of the selected Table. Is used at creation of new order.
     */
    private int selectedTableId;
    /**
     * A list of integers, that are the IDs of the portions that are selected for order. Is used at creation of new order and it's orderedPortions.
     */
    private ArrayList<Integer> selectedPortionIds = new ArrayList<>();
    private  Boolean dirty_hack = false;

    /**
     * Constructor for waiter controller. Updates the data stores
     */
    public WaiterController(){
        try {
            model = Model.getInstance();
            orders = model.getOrders();
        }catch (Exception e){
            System.out.println("WaiterController: Exception thrown when trying to get instance of Model");
            e.printStackTrace();
        }
    }

    /**
     * initializer for waiter controller. Updates the gui and add PropertyChangeListener to Controller.Model
     */
    @FXML
    public void initialize(){

        updateGui();
        Controller.model.addPropertyChangeListener(evt -> {
            //NOTE! DO NOT CALL Model -getters here, the thread has a lock and calling a getter will make the thread enter a deadlock!
            if(evt.getPropertyName().equals("orders")) {
                orders = (ArrayList<OrdersEntity>)evt.getNewValue();
                updateGui();
            }
            else if(evt.getPropertyName().equals("orderedPortions")) {
                updateGui();
            }
        });
    }

    /**
     * If user clicks outside any buttons, the order is deselected
     */
    public void deSelect(){
        selectedOrder = null;
        updateGui();
    }

    /**
     * Moves the order to next swimlane
     */
    public void advanceOrder(){
        if(selectedOrder.getOrderDetails().equals("readyToServe"))
            selectedOrder.setOrderDetails("served");
        else if (selectedOrder.getOrderDetails().equals("served"))
            selectedOrder.setOrderDetails("paid");
        else if (selectedOrder.getOrderDetails().equals("paid"))
            selectedOrder.setOrderDetails("paid");
        else{
            selectedOrder.setOrderDetails("readyToServe");
        }
        Controller.model.updateOrder(selectedOrder);
        updateGui();
    }

    /**
     * Selects an order when user clicks it. If user clicks a selected order, a editor view is opened.
     * @param mouseEvent MouseEvent from the GUI
     */
    public void orderClicked(MouseEvent mouseEvent){
        Button button = (Button) mouseEvent.getSource();
        if(selectedOrder!= null && selectedOrder.getId() == Integer.parseInt(button.getId())) {
            openEditOrderView();
        }
        else {
            selectedOp = null;
            for (OrdersEntity o : orders) {
                if (o.getId() == Integer.parseInt(button.getId()))
                    selectedOrder = o;
            }
            updateGui();
        }
    }

    /**
     * Method for updating the gui, wrapped in Platform.runLater(). Similar to kitchen controller {@link managerpro.Controller.KitchenController}
     */
    public void updateGui(){
        //Calling model getters is fine inside the Platform.runLater() -block, this block is run by JavaFX thread
        Platform.runLater(()->{

            //If the "new order" -panel is visible, don't update the gridPane, and vice versa

            if(waiterGridPane.isVisible()) {
                if (selectedOrder != null) {
                    advanceOrderBtn.setDisable(false);
                } else {
                    advanceOrderBtn.setDisable(true);
                }
                int a, b, c, d;
                a = b = c = d = 0;
                //Clear the boxes
                waiterReadyVBox.getChildren().clear();
                waiterOrdersVBox.getChildren().clear();
                waiterServedVBox.getChildren().clear();
                waiterPaidVBox.getChildren().clear();

                //Create Button Lists
                List<Button> waiting = new ArrayList<>();
                List<Button> readyToServe = new ArrayList<>();
                List<Button> served = new ArrayList<>();
                List<Button> paid = new ArrayList<>();

                //Loop through orders:
                for (OrdersEntity o : orders) {
                    if (o.getOrderDetails().equalsIgnoreCase("paid")) {
                        paid.add(new Button(LanguageUtils.translateToString(o)));
                        paid.get(a).addEventHandler(MouseEvent.MOUSE_CLICKED, this::orderClicked);
                        paid.get(a).setId((Integer.toString(o.getId())));

                        if (o.equals(selectedOrder))
                            paid.get(a).setStyle("-fx-base: #b6e7c9; -fx-font-weight: bold");

                        a++;
                    } else if (o.getOrderDetails().equalsIgnoreCase("served")) {
                        served.add(new Button(LanguageUtils.translateToString(o)));
                        served.get(b).addEventHandler(MouseEvent.MOUSE_CLICKED, this::orderClicked);
                        served.get(b).setId((Integer.toString(o.getId())));

                        if (o.equals(selectedOrder))
                            served.get(b).setStyle("-fx-base: #b6e7c9; -fx-font-weight: bold");

                        b++;
                    } else if (o.getOrderDetails().equalsIgnoreCase("readyToServe")) {
                        readyToServe.add(new Button(LanguageUtils.translateToString(o)));
                        readyToServe.get(c).addEventHandler(MouseEvent.MOUSE_CLICKED, this::orderClicked);
                        readyToServe.get(c).setId((Integer.toString(o.getId())));
                        if (o.equals(selectedOrder))
                            readyToServe.get(c).setStyle("-fx-base: #b6e7c9; -fx-font-weight: bold");

                        c++;
                    } else {
                        waiting.add(new Button(LanguageUtils.translateToString(o)));
                        waiting.get(d).addEventHandler(MouseEvent.MOUSE_CLICKED, this::orderClicked);
                        waiting.get(d).setId((Integer.toString(o.getId())));
                        if (o.equals(selectedOrder))
                            waiting.get(d).setStyle("-fx-base: #b6e7c9; -fx-font-weight: bold");

                        d++;
                    }
                }
                //Add Buttons
                waiterReadyVBox.getChildren().addAll(readyToServe);
                waiterOrdersVBox.getChildren().addAll(waiting);
                waiterServedVBox.getChildren().addAll(served);
                waiterPaidVBox.getChildren().addAll(paid);


                //
            }
            //If the gridPane isn't visible, update the new order pane
            else {
                //newOrderOpVBox.getChildren().clear();
                newOrderPortionsVBox.getChildren().clear();
                newOrderTablesVBox.getChildren().clear();

                ArrayList<Button> btns = new ArrayList<>();
                int i=0;
                for(TablesEntity tb : Controller.model.getTables()){
                 btns.add(new Button(LanguageUtils.translateToString(tb)));
                 btns.get(i).setId(Integer.toString(tb.getId()));
                 btns.get(i).addEventHandler(ActionEvent.ACTION,this::selectTable);
                 i++;
                }
                newOrderTablesVBox.getChildren().addAll(btns);

                btns.clear();
                i=0;
                for(PortionsEntity portion : Controller.model.getPortions()){
                    btns.add(new Button(LanguageUtils.translateToString(portion)));
                    btns.get(i).setId(Integer.toString(portion.getId()));
                    btns.get(i).addEventHandler(ActionEvent.ACTION,this::addPortion);
                    i++;
                }
                newOrderPortionsVBox.getChildren().addAll(btns);
            }
        });
    }

    /**
     * Method for handling table selecting in Editor/New order View
     * @param actionEvent Action event from gui
     */
    public void selectTable(ActionEvent actionEvent){
        confirmOrderBtn.setDisable(false);
        Button src = (Button)actionEvent.getSource();
        selectedTableId = Integer.parseInt(src.getId());
        newOrderTableLabel.setText( LanguageUtils.getResourceBundle().getString("table")+" #"+src.getId());
    }

    /**
     * Method for handling adding a portion to an order in editor/New order view
     * @param actionEvent Action event from gui
     */
    public void addPortion(ActionEvent actionEvent){
        Button btn = (Button) actionEvent.getSource();
        Button newBtn = new Button();
        newBtn.setId("op"+btn.getId());
        newBtn.setText(btn.getText());

        newBtn.addEventHandler(ActionEvent.ACTION,this::removePortion);

        selectedPortionIds.add(Integer.parseInt(btn.getId()));

        newOrderOpVBox.getChildren().add(newBtn);
        TextField textField = new TextField();
        textField.setPadding(new Insets(0,0,25,0));
        int i = Integer.parseInt(btn.getId());
        textField.setId("tf"+ i);
        newOrderOpVBox.getChildren().add(textField);
        //System.out.println(LanguageUtils.getResourceBundle().getString("portion_ids")+":");

    }

    /**
     * Method for removing a portion from an order in Editor/New order view
     * @param actionEvent Action Event from gui
     */
    public void removePortion(ActionEvent actionEvent){
        Button btn = (Button) actionEvent.getSource();

        //Get buttons index
        int i =newOrderOpVBox.getChildren().indexOf(btn);
        //Remove button and it's TextField
        newOrderOpVBox.getChildren().remove(i);
        newOrderOpVBox.getChildren().remove(i);


        selectedPortionIds.remove(Integer.valueOf(Integer.parseInt(btn.getId().substring(2))));

    }

    /**
     * Method for creating a new order and it's orderedPortions based on the information on the Gui. Uses {@link managerpro.Model.Model} to add rows to database.
     * @param actionEvent Action event from the gui
     */
    public void createNewOrder(ActionEvent actionEvent) {
        if(!dirty_hack) {
            //Create new order and add to database
            OrdersEntity order = new OrdersEntity("waiting", selectedTableId);
            ArrayList<String> stringArrayList = new ArrayList<>();
            Controller.model.addOrder(order);
            for (Node n : newOrderOpVBox.getChildren()) {
                if (n.getClass().equals(TextField.class))
                    stringArrayList.add(((TextField) n).getText());
            }
            //Create orderedPortions
            for (int i = 0; i < selectedPortionIds.size(); i++) {
                Controller.model.addOrderedPortion(new OrderedPortionsEntity(order.getId(), selectedPortionIds.get(i), stringArrayList.get(i), 0));
            }
            closeNewOrderDialog(null);
        }
        else
            updateOrder(actionEvent);
    }

    /**
     * Method for gracefully closing the New order / edit order dialog
     * @param actionEvent Action Event from the gui
     */
    public void closeNewOrderDialog(ActionEvent actionEvent) {
        setDirtyHack(false);
        selectedPortionIds.clear();
        newOrderOpVBox.getChildren().clear();
        selectedTableId = -1;
        newOrderTableLabel.setText("Select Table");
        confirmOrderBtn.setDisable(true);
        waiterAnchorPane.setVisible(false);
        waiterGridPane.setVisible(true);

        //Remove 'remove' button
        Button btn;
        for(Node n : newOrderAnchorPane.getChildren()) {
            btn = (Button) n;
            if (btn.getId().equals("deleteBtn")) {
                newOrderAnchorPane.getChildren().remove(btn);
                break;
            }
        }

        updateGui();
    }

    /**
     * Method for placing a Anchor pane on top of the Waiter view for creating a new order
     * @param actionEvent Action event from the gui
     */
    public void newOrderClicked(ActionEvent actionEvent){
        waiterAnchorPane.setVisible(true);
        waiterGridPane.setVisible(false);
        updateGui();
    }
    /**
     * Calls newOrderClicked with null parameter and alter the new order view to suit the Edit order intention of the user.
     */
    public void openEditOrderView(){
        newOrderClicked(null);
        //Change the Confirm order button to "Update Order"
        confirmOrderBtn.setDisable(false);
        confirmOrderBtn.setText(LanguageUtils.getResourceBundle().getString("update_order"));
        //dirty_hack
        setDirtyHack(true);

        //Prepare the View
        //set selected table to selectedOrder.getTableNumber
        selectedTableId = selectedOrder.getTableNumber();



        newOrderTableLabel.setText("Table #"+selectedOrder.getTableNumber());
        for(OrderedPortionsEntity op : Controller.model.getOrderedPortions()){
            if(op.getOrderId() == selectedOrder.getId()){
                //Also add the portion ID of the orderedPortion to selectedPortionsId -list
                selectedPortionIds.add(op.getPortionId());
                //Create button for each orderedPortion
                Button btn = new Button();
                btn.setId("op"+(op.getPortionId()));
                btn.setText(LanguageUtils.translateToString(Controller.model.getPortion(op.getPortionId())));
                btn.addEventHandler(ActionEvent.ACTION,this::removePortion);
                newOrderOpVBox.getChildren().add(btn);

                //Create text field for each orderedPortion
                TextField textField = new TextField();
                textField.setId("tf"+(op.getPortionId()));
                textField.setText(op.getPortionDetails());
                newOrderOpVBox.getChildren().add(textField);
            }
        }
        Button removeBtn = new Button();
        removeBtn.setText(LanguageUtils.getResourceBundle().getString("delete_order"));
        removeBtn.setId("deleteBtn");
        removeBtn.setStyle("-fx-font-scale: 2");
        removeBtn.addEventHandler(ActionEvent.ACTION,this::deleteOrder);
        newOrderAnchorPane.getChildren().add(removeBtn);
        AnchorPane.setBottomAnchor(removeBtn,0.0);
        AnchorPane.setTopAnchor(removeBtn,0.0);
        AnchorPane.setLeftAnchor(removeBtn,200.0);

    }

    /**
     * Method for updating the users changes to an order to database
     * @param actionEvent Action event from the gui
     */
    public void updateOrder(ActionEvent actionEvent) {



        //Update the data of selected Order according to the selections made by user
        selectedOrder.setTableNumber(selectedTableId);
        selectedOrder.setOrderDetails("inTheMaking");
        Controller.model.updateOrder(selectedOrder);

        //Get the strings from the TextField

        ArrayList<String> stringArrayList = new ArrayList<>();
        for (Node n : newOrderOpVBox.getChildren()) {
            if (n.getClass().equals(TextField.class))
                stringArrayList.add(((TextField) n).getText());
        }
        //Create new orderedPortions
        ArrayList<OrderedPortionsEntity> newOps = new ArrayList<>();
        for(int i=0; i<selectedPortionIds.size();i++){
            newOps.add(new OrderedPortionsEntity(selectedOrder.getId(),selectedPortionIds.get(i),stringArrayList.get(i),0));
        }

        //Find unchanged portions and save their statuses!

        //For every original orderedPortion...
        for(int i=0;i<Controller.model.getOrderedPortionsByOrderId(selectedOrder.getId()).size();i++){
            //We go through the new orders...
            for(int j=0;j<newOps.size();j++){
                //And look for a match
                if(Controller.model.getOrderedPortionsByOrderId(selectedOrder.getId()).get(i).equalsIgnoreId(newOps.get(j))){
                    //All the newOps dont have a id, so if the match has an id, it has been
                    //taken already. If the id == 0, we can take it.
                    if(newOps.get(j).getJunctionId() == 0) {
                        //Match found! Save the status and the junction_id
                        newOps.get(j).setStatus(Controller.model.getOrderedPortionsByOrderId(selectedOrder.getId()).get(i).getStatus());
                        newOps.get(j).setJunctionId(Controller.model.getOrderedPortionsByOrderId(selectedOrder.getId()).get(i).getJunctionId());
                    }
                }
            }
        }
        //Now we have a newOps list we can push to database
        //It prevents loss of data when altering the orderedPortions of an order
        //It still does useless extra work (deleting and writing the same row), maybe make this smarter later
        

        //Loop over the old oPs and remove them
        for(OrderedPortionsEntity op : Controller.model.getOrderedPortionsByOrderId(selectedOrder.getId())){
            Controller.model.deleteOrderedPortion(op);
        }

        //Loop over the newOps and add them
        for(OrderedPortionsEntity op : newOps){
            Controller.model.addOrderedPortion(op);
        }
        //Set the button back to it's normal state
        confirmOrderBtn.setText(LanguageUtils.getResourceBundle().getString("confirm_order"));
        
        //Close the dialog
        closeNewOrderDialog(null);
    }
    /**
     * Method for deleting an order from the databqse
     * @param actionEvent Action event from the gui
     */
    public void deleteOrder(ActionEvent actionEvent){
        List<OrderedPortionsEntity> opList = Controller.model.getOrderedPortionsByOrderId(selectedOrder.getId());
        if(opList != null)
        for(OrderedPortionsEntity op :opList)
            Controller.model.deleteOrderedPortion(op);

        Controller.model.deleteOrder(selectedOrder);
        confirmOrderBtn.setText(LanguageUtils.getResourceBundle().getString("confirm_order"));
        confirmOrderBtn.removeEventHandler(ActionEvent.ACTION,this::updateOrder);
        closeNewOrderDialog(null);
    }

    /**
     * Handles the event of opening the waiter-tab.
     * @param event - The event that is fired when the tab is selected.
     */
    public void tabOpened(Event event) {
        orders = Controller.model.getOrders();
        selectedOrder= null;
        selectedOp=null;
        updateGui();
    }
    private void setDirtyHack(boolean b){
        dirty_hack = b;
    }
}
