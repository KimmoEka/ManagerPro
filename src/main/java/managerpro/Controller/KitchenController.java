package managerpro.Controller;

import managerpro.Model.Model;
import managerpro.Model.OrderedPortionsEntity;
import managerpro.Model.OrdersEntity;
import managerpro.Utils.LanguageUtils;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * A Controller class to control the Kitchen -tab in the JavaFX User interface.
 * Has an intermediary datastorage of Orders and OrderedPortions, based on which the user interface is updated
 * @author OTP7
 */
public class KitchenController {
    /**
     * FXML Node ordersFlowPane, FlowPane that holds the buttons that are created based on the orders in the database.
     */
    @FXML public FlowPane ordersFlowPane;
    /**
     * FXML Node portionReadyBtn, a button that sets the status of an order to Ready
     */
    @FXML Button portionReadyBtn;
    /**
     * FXML Node orderedPortionsFlowPane, FlowPane that holds buttons that are created in the code, based on portions attached to selected order.
     */
    @FXML FlowPane orderedPortionsFlowPane;
    /**
     * FXML Node singlePortionVBox VBox, that holds details of the currently selected ordered portion.
     */
    @FXML VBox singlePortionVBox;

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
    private ArrayList<OrderedPortionsEntity> orderedPortions;
    /**
     * An ArrayList of OrdersEntities, that is used to populate the ordersFlowPane
     */
    private ArrayList<OrdersEntity> orders;
    /**
     * Reference to a instance of a singleton Model -class.
     */
    private Model model;
    /**
     * Constructor of Kitchen controller. Takes no parameters. It inits the intermediary data stores.
     */
    public KitchenController(){
        try {
            model = Model.getInstance();
            orderedPortions = model.getOrderedPortions();
            orders = model.getOrders();
            if(orders.size()>0) {
                for(int i=0;i<orders.size();i++){
                    if(!orders.get(i).getOrderDetails().equals("readyToServe") && !orders.get(i).getOrderDetails().equals("served") && !orders.get(i).getOrderDetails().equals("paid")) {
                        selectedOrder = orders.get(i);
                    }
                }
            }
        }catch (Exception e){
            System.out.println("KitchenController: Exception thrown when trying to get instance of Model");
            e.printStackTrace();
        }

    }

    /**
     * Initializer for Kitchen controller. Updates the GUI and adds PropertyChangeListener to Controller.Model
     * to update the Gui whenever changes are made ot database.
     */
    @FXML
    public void initialize(){
        updateGui();
        model.addPropertyChangeListener(evt -> {
            if(evt.getPropertyName().equals("orders")) {
                orders = (ArrayList<OrdersEntity>)evt.getNewValue();
                if (orders.isEmpty()){ selectedOrder=null; selectedOp=null;}
                updateGui();
            }
            else if(evt.getPropertyName().equals("orderedPortions")) {
                orderedPortions = (ArrayList<OrderedPortionsEntity>) evt.getNewValue();
                if(orderedPortions.isEmpty()){ selectedOrder=null; selectedOp=null;}
                updateGui();
            }
        });
    }

    /**
     * Event Handler for the "portion ready" Button. Sets the selected ordered portions's status to 1 and if all portions are done, sets the
     * orders status to "readyToServe"
     * @param mouseEvent MouseEvent from the button click. Will be changed to ActionEvent at some point
     */
    public void setPortionDone(MouseEvent mouseEvent) {
        if(selectedOp != null) {
            if (selectedOp.getStatus() == 0)
                selectedOp.setStatus(1);
            else
                selectedOp.setStatus(0);
            model.updateOrderedPortion(selectedOp);
            int i=0;
            for(OrderedPortionsEntity op : model.getOrderedPortionsByOrderId(selectedOrder.getId()))
            {
                if(op.getStatus()==1) {
                    i++;
                }
            }
            if (i==model.getOrderedPortionsByOrderId(selectedOrder.getId()).size()) {
                selectedOrder.setOrderDetails("readyToServe");
                model.updateOrder(selectedOrder);
                selectedOrder=null;
            }
            else if (i==0){
                selectedOrder.setOrderDetails("waiting");
                model.updateOrder(selectedOrder);
            }
            else {
                model.updateOrder(selectedOrder);
            }
            updateGui();
        }
    }

    /**
     * Event Handler for when the user clicks an order on the screen. Sets the selected order to selectedOrder field and finds the first
     * orderedPortion the of selected order and sets it as selectedOrderedPortion
     * @param mouseEvent Mouse event from the GUI
     */
    public void orderClicked(MouseEvent mouseEvent) {
        Button button = (Button) mouseEvent.getSource();
        selectedOp =null;
        for (OrdersEntity o : orders) {
            if (o.getId() == Integer.parseInt(button.getId().substring(3)))
                selectedOrder = o;
        }
        updateGui();
    }

    /**
     * Event Handler for when the user clicks an ordered portion. Prints out the ordered portions details to a VBox.
     * @param mouseEvent MouseEvent from the gui
     */
    public void oPClicked(MouseEvent mouseEvent){
        Button button = (Button) mouseEvent.getSource();
        for(OrderedPortionsEntity op : orderedPortions)
        {
            if(op.getJunctionId() == Integer.parseInt(button.getId().substring(5)))
                selectedOp = op;
        }
        updateGui();
    }

    /**
     * A Method for updating the JavaFX UI.
     * Clears the children of the HBoxes and VBox, creates a new button for each order,
     * creates button for each orderedPortion of the selected order and prints out the information of the selectedOrderedPortion to
     * the VBox. Wrapped in Platform.runLater() to allow the PropertyChangeListener to call this method on database updates. Neat.
     */
    public void updateGui(){
        Platform.runLater(()->{
            //Empty the Boxes
            ordersFlowPane.getChildren().clear();
            orderedPortionsFlowPane.getChildren().clear();
            singlePortionVBox.getChildren().clear();
            //Make buttons for orders
            List<Button> orderButtons = new ArrayList<>();
            int i =0;
            for(OrdersEntity ordersEntity : orders){
                if(!ordersEntity.getOrderDetails().equals("readyToServe") && !ordersEntity.getOrderDetails().equals("served") && !ordersEntity.getOrderDetails().equals("paid")) {
                    orderButtons.add(new Button(LanguageUtils.getResourceBundle().getString("order")+ "# " + ordersEntity.getId() + "\n"+LanguageUtils.getResourceBundle().getString("table")+ "# " + ordersEntity.getTableNumber()));
                    orderButtons.get(i).addEventHandler(MouseEvent.MOUSE_CLICKED, this::orderClicked);
                    orderButtons.get(i).setId("kth"+ordersEntity.getId());
                    orderButtons.get(i).getStyleClass().add("btn");
                    if (ordersEntity.equals(selectedOrder)) {
                        orderButtons.get(i).getStyleClass().add("kitchenOrderSelected");
                    }
                    i++;
                }
            }
            i=0;
            ordersFlowPane.getChildren().addAll(orderButtons);

            //If an order is selected (default first order, can be changed by clicking), make buttons for ordered portions
            if(selectedOrder != null){

                List<Button> oPButtons = new ArrayList<>();
                List<OrderedPortionsEntity> opList = model.getOrderedPortionsByOrderId(selectedOrder.getId());
                if(opList != null)
                    for(OrderedPortionsEntity op : opList){
                        oPButtons.add(new Button(LanguageUtils.getResourceBundle().getString("portion")+": " +model.getPortion(op.getPortionId()).getName()+ "\n"+LanguageUtils.getResourceBundle().getString("details")+": "+op.getPortionDetails()));
                        oPButtons.get(i).setId("opKth"+ op.getJunctionId());

                        oPButtons.get(i).addEventHandler(MouseEvent.MOUSE_CLICKED,this::oPClicked);
                        oPButtons.get(i).getStyleClass().add("btn");
                        if(op.getStatus() == 0)
                            oPButtons.get(i).getStyleClass().add("kitchenNotReady");
                        if(op.equals(selectedOp))
                            oPButtons.get(i).getStyleClass().add("bold");
                        if(op.getStatus()==1) {
                            oPButtons.get(i).getStyleClass().add("kitchenReady");
                        }
                        i++;
                    }
                orderedPortionsFlowPane.getChildren().addAll(oPButtons);
            }

            //Print details of the selected OrderedPortion to singlePortion VBox
            if(selectedOp != null)
            {
                Text text = new Text(
                        LanguageUtils.getResourceBundle().getString("portion_name")+": " + model.getPortion(selectedOp.getPortionId()).getName()
                                + "\n"+LanguageUtils.getResourceBundle().getString("portion_details")+": "+ selectedOp.getPortionDetails()
                );
                text.getStyleClass().add("kitchenTextBox");
                text.setId("opText");
                singlePortionVBox.getChildren().add(text);
            }
        });
    }


    /**
     * Handles the event of opening the kitchen-tab.
     * @param event - The event that is fired when the tab is selected.
     */
    public void tabOpened(Event event) {
        orderedPortions = model.getOrderedPortions();
        orders = model.getOrders();
        if(orders.size()>0) {
            for(int i=0;i<orders.size();i++){
                if(!orders.get(i).getOrderDetails().equals("readyToServe") && !orders.get(i).getOrderDetails().equals("served") && !orders.get(i).getOrderDetails().equals("paid")) {
                    selectedOrder = orders.get(i);
                }
            }
        }else selectedOrder=null;
        updateGui();
    }
}
