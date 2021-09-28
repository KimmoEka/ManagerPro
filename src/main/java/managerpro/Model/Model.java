package managerpro.Model;

import org.hibernate.SessionFactory;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
/**
 * This is a class that is used as a intermediary data storage,
 * to provide the controllers access to database and to start a timer
 * for ModelHelper. Model class holds collections of entities and provides CRUD methods for each entity.
 * Internally model uses DAOs to provide the database access.
 *
 * This class is in the larger side, but a necessary step between the database and the controllers, because the
 * project is lacking a backend at this point.
 * @author OTP7
 *
 */
public class Model {
    /**
     * ArrayList holding all the reservations
     */
    private ArrayList<ReservationsEntity> reservations;
    /**
     * ArrayList holding all the orders
     */
    private ArrayList<OrdersEntity> orders;
    /**
     * ArrayList holding all the portions
     */
    private ArrayList<PortionsEntity> portions;
    /**
     * ArrayList holding all the orderedPortions
     */
    private ArrayList<OrderedPortionsEntity> orderedPortions;
    /**
     * ArrayList holding all the tables
     */
    private ArrayList<TablesEntity> tables;

    private Map<Integer,List<OrderedPortionsEntity>> orderedPortionsByOrderId = new HashMap<>();
    /**
     * Database access object
     */
    private ITablesDao tablesDao;
    /**
     * Database access object
     */
    private IOrdersDao ordersDao;
    /**
     * Database access object
     */
    private IPortionsDao portionsDao;
    /**
     * Database access object
     */
    private IReservationsDao reservationsDao;
    /**
     * Database access object
     */
    private IOrderedPortionsDao orderedPortionsDao;

    /**
     * PropertyChangeSupport. PropertyChangeSupport is used to inform Controllers of any changes
     * that may have happened when modelHelper updates the fields
     */
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    /**
     * An ArrayBlockingQueue that is used to prevent concurrent reads and writes of the Entity collections.
     * Prevents race conditions when modelHelper is invoked after INSERT, UPDATE or DELETE and the timed modelHelper
     * task is also updating the fields at same time.
     */
    private final BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(1,true);
    private static SessionFactory sessionFactory = null;
    /**
     * Timer for modelHelper scheduling.
     */
    private Timer timer;
    /**
     * A modelHelper instance, that is used to pull data periodically from the DB.
     */
    private ModelHelper modelHelper;

    private static volatile Model instance = null;

    /**
     * A Method to return an instance of model class. Model is a singleton, so its constructor is private and only way to get instances is to use this method.
     * @return A shared instance of Model class.
     * @throws Exception An exception is thrown, if Model class has no sessionFactory
     */
    public static Model getInstance() throws Exception{
        if(instance==null){
            synchronized (Model.class){
                if(Model.sessionFactory == null){
                    throw new Exception("Model: getInstance: SessionFactory is null!");
                }
                if(instance==null){
                    instance= new Model();
                }
            }
        }
        return instance;
    }

    /**
     * A Method for setting a sessionFactory for Model class
     * @param sessionFactory a Hibernate session factory, that will be used by DAOs to get database sessions.
     */
    public static void setSessionFactory(SessionFactory sessionFactory){
        Model.sessionFactory = sessionFactory;
    }
    /**
     * <p>
     * Model class constructor.
     * Instantiates Daos, ArrayLists, modelHelper and Timer.
     * Adds PropertyChangeListener to modelHelper
     * Schedules modelHelper to be run every 2 seconds.
     * </p>
     */
    private Model()
    {
        //Daos
        this.tablesDao = new TablesDao(sessionFactory);
        this.ordersDao = new OrdersDao(sessionFactory);
        this.portionsDao = new PortionsDao(sessionFactory);
        this.reservationsDao = new ReservationsDao(sessionFactory);
        this.orderedPortionsDao = new OrderedPortionsDao(sessionFactory);


        //Empty Lists
        this.reservations = new ArrayList<ReservationsEntity>();
        this.orders = new ArrayList<OrdersEntity>();
        this.portions = new ArrayList<PortionsEntity>();
        this.orderedPortions = new ArrayList<OrderedPortionsEntity>();
        this.tables = new ArrayList<TablesEntity>();


        //Background TimerTask to fetch data form the Database

        modelHelper = new ModelHelper(sessionFactory);

        //Add Listener. If the property changes on modelHelper, update the property on Model and fire a PropertyChange for the controller
        modelHelper.addPropertyChangeListener(evt -> {
            //System.out.println("PropertyChange fired: " +evt.getPropertyName());
                updateFields((ArrayList)evt.getNewValue());
        });


        //Schedule the modelHelper to run every second

        timer = new Timer(true);
        timer.schedule(modelHelper,0,2000);

        //Run the initial population of the fields
        modelHelper.run();

    }

    /**
     * A simple method that refreshes the fields. Not usually required, the PropertyChangeListeners and individual modelHelpers should do the syncing.
     */
    public void refresh(){
        modelHelper.run();
    }
    /**
     * A method to disable the modelHelper scheduling. Not recommended.
     */
    public void disableScheduling(){
        timer.cancel();
        timer.purge();
        System.out.println("Model: Scheduling disabled");
    }
    /**
     * A method for re-enabling the scheduling
     */
    public void enableScheduling(){
        timer = new Timer(true);
        modelHelper = new ModelHelper(sessionFactory);
        timer.schedule(modelHelper,0,2000);
        System.out.println("Model: Scheduling enabled");
    }
    /**
     * <p>
     *     A method for updating the Entity collections. This method is called in the PropertyChangeListener, and
     *     gets a ArrayList of ArrayList as parameter. The ArrayLists are then compared to the fields of the model and
     *     an PropertyChange is fired if the list the modelHelper has loaded from database is different from the Models field
     *
     *     Database is assumed to have more up-to-date knowledge at any given time.
     * </p>
     * @param lists ArrayList of ArrayLists coming from the modelHelper. Order of ArrayList in lists must be
     *              Tables, Orders,Reservations,Portions,OrderedPortions
     */
    private void updateFields(ArrayList lists) {
        //Order of lists in lists is
        // Tables
        // Orders
        // Reservations
        // Portions
        // Ordered Portions

        //Wait until Queue is empty
        try {
            queue.put(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //For each of the lists in the 'lists' variable, we check if the values have changed
        //If they have, fire the PropertyChange and update the private fields

        if (lists.get(0) != null && !this.tables.equals(lists.get(0))) {
            //System.out.println("PropertyChange fired: tables");
            this.tables = (ArrayList<TablesEntity>) lists.remove(0);
            support.firePropertyChange("tables", null, this.tables);
        }
        else
            lists.remove(0);
        if (lists.get(0) != null && !this.orders.equals(lists.get(0))) {
            //System.out.println("PropertyChange fired: orders");
            this.orders = (ArrayList<OrdersEntity>) lists.remove(0);
            support.firePropertyChange("orders", null, this.orders);
        }
        else
            lists.remove(0);
        if (lists.get(0) != null && !this.reservations.equals(lists.get(0))) {
            //System.out.println("PropertyChange fired: reservations");
            this.reservations = (ArrayList<ReservationsEntity>) lists.remove(0);
            support.firePropertyChange("reservations", null, this.reservations);
        }
        else
            lists.remove(0);
        if (lists.get(0) != null && !this.portions.equals(lists.get(0))) {
            //System.out.println("PropertyChange fired: portions");
            this.portions = (ArrayList<PortionsEntity>) lists.remove(0);
            support.firePropertyChange("portions", null, this.portions);
        }
        else
            lists.remove(0);
        if (lists.get(0) != null && !this.orderedPortions.equals(lists.get(0))) {
            this.orderedPortions = (ArrayList<OrderedPortionsEntity>) lists.remove(0);
            //System.out.println("PropertyChange fired: orderedPortions");
            rebuildOpMap();
            support.firePropertyChange("orderedPortions", null, this.orderedPortions);
        }
        else
            lists.remove(0);

        //When done, take the token Integer from the Queue and let the next thread enter

        try {
            queue.take();
        }catch (InterruptedException e){
            System.out.println("Thread Interrupted");
            e.printStackTrace();
        }
    }

    /**
     * <p>
     * A method for starting a new thread to run the modelHelper
     * This updates the fields when user calls the CRUD methods of the Model object
     * ie. No waiting for the scheduled update
     * </p>
     * @param s A string that is used to tell the ModelHelper to fetch data of a certain table
     */
    private void startHelper(String s){
            ModelHelper tempHelper=  new ModelHelper(sessionFactory,s);
            tempHelper.addPropertyChangeListener(evt -> {
                updateFields((ArrayList)evt.getNewValue());
            });
            Thread t = new Thread(tempHelper);
            t.start();
    }
    private void rebuildOpMap(){
        //System.out.println("Rebuilding orderedPortions Map");
        //Clear everything
        orderedPortionsByOrderId.clear();

        for(OrderedPortionsEntity op : this.orderedPortions){
            //If order ID is in the map, we add the ordered portion to the existing list
            if (!orderedPortionsByOrderId.containsKey(op.getOrderId())) {
                orderedPortionsByOrderId.put(op.getOrderId(), new ArrayList<>());
            }
            orderedPortionsByOrderId.get(op.getOrderId()).add(op);
        }
        //System.out.println(orderedPortionsByOrderId);
    }

    /**
     * <p>
     *     Method for adding a PropertyChangeListener
     * </p>
     * @param propertyChangeListener PropertyChangeListener to add to the PropertyChangeSupport
     */
    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener){
        this.support.addPropertyChangeListener(propertyChangeListener);
    }
    /**
     * <p>
     *     Method for removing a PropertyChangeListener
     * </p>
     * @param propertyChangeListener PropertyChangeListener to remove to the PropertyChangeSupport
     */
    public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener){
        this.support.removePropertyChangeListener(propertyChangeListener);
    }
    /**
     * <p>
     *     Method for returning all attached PropertyChangeListeners
     * </p>
     * @return Array of attached PropertyChangeListeners
     */
    public PropertyChangeListener[] getPropertyChangeListeners(){
        return this.support.getPropertyChangeListeners();
    }

    // From Here on there are the CRUD methods of the model
    // For each Entity type there is a method for the CRUD operations
    // Getters also reside here, with a queue.peek() as a guard
    // -> If an update is in the queue, wait for it to finish
    //    before getting the list.


    /**
     * <p>
     *     Add table to database
     * </p>
     * @param table TablesEntity to add
     */
    public void addTable(TablesEntity table){
            try{
                this.tablesDao.createTable(table);
                startHelper("tables");
            }catch (Exception e){
                System.out.println("Model: addTables: Exception when adding table #" + table.getId());
                throw e;
            }

    }
    /**
     * <p>
     *     Add portion to database
     * </p>
     * @param portion PortionsEntity to add
     */
    public void addPortion(PortionsEntity portion){
            try{
                this.portionsDao.createPortion(portion);
                startHelper("portions");
            }catch (Exception e){
                System.out.println("Model: addPortion: Exception when adding portion: " + portion.getName());
                throw e;
            }

    }
    /**
     * <p>
     *     Add order to database
     * </p>
     * @param order OrdersEntity to add
     */
    public void addOrder(OrdersEntity order){
            try{
                this.ordersDao.createOrder(order);
                startHelper("orders");
            }catch (Exception e){
                System.out.println("Model: addPortion: Exception when adding order #"+order.getId());
                throw e;
            }
    }
    /**
     * <p>
     *     Add reservation to database
     * </p>
     * @param reservation ReservationsEntity to add
     */
    public void addReservation(ReservationsEntity reservation){
            try{
                this.reservationsDao.createReservation(reservation);
                startHelper("reservations");
            }catch (Exception e){
                System.out.println("Model: addReservation: Exception when adding reservation for: " + reservation.getClientName());
                throw e;
            }
    }
    /**
     * <p>
     *     Add orderedPortion to database
     * </p>
     * @param orderedPortion OrderedPortionsEntity to add
     */
    public void addOrderedPortion(OrderedPortionsEntity orderedPortion){
            try{
                this.orderedPortionsDao.createOrderedPortion(orderedPortion);
                startHelper("orderedPortions");
            }catch (Exception e){
                System.out.println("Model: addOrderedPortion: Exception when adding OrderedPortion for order #"+ orderedPortion.getOrderId() + ", portion: " +orderedPortion.getPortionId());
                throw e;
            }
    }
    /**
     * <p>
     *     Delete table from database
     * </p>
     * @param table TablesEntity to delete
     */
    public void deleteTable(TablesEntity table){
            int i = tables.indexOf(table);
            if (i !=-1)
            try{
                this.tablesDao.deleteTable(table);
                startHelper("tables");
            }catch (Exception e){
                System.out.println("Model: deleteTable: Exception when deleting table#" + table.getId());
                throw e;
            }
    }

    /**
     * <p>
     *     Delete portion from database
     * </p>
     * @param portion PortionsEntity to delete
     */
    public void deletePortion(PortionsEntity portion){
            int i = portions.indexOf(portion);
            if (i !=-1)
            try{
                this.portionsDao.deletePortion(portion);
                startHelper("portions");
            }catch (Exception e){
                System.out.println("Model: deletePortion: Exception when deleting portion: " + portion.getName());
                throw e;
            }
    }

    /**
     * <p>
     *     Delete order from database
     * </p>
     * @param order OrdersEntity to delete
     */
    public void deleteOrder(OrdersEntity order){
            int i = orders.indexOf(order);
            if (i !=-1)
            try{
                this.ordersDao.deleteOrder(order);
                startHelper("orders");
            }catch (Exception e){
                System.out.println("Model: deleteOrder: Exception when deleting order #" + order.getId());
                throw e;
            }
    }

    /**
     * <p>
     *     Delete reservation from database
     * </p>
     * @param reservation ReservationsEntity to delete
     */
    public void deleteReservation(ReservationsEntity reservation){
            int i = reservations.indexOf(reservation);
            if (i !=-1)
            try{
                this.reservations.remove(reservation);
                this.reservationsDao.deleteReservation(reservation);
                startHelper("reservations");
            }catch (Exception e){
                System.out.println("Model: deleteReservation: Exception when deleting reservation for: " + reservation.getClientName());
                throw e;
            }
    }

    /**
     * <p>
     *     Delete orderedPortion from database
     * </p>
     * @param orderedPortion OrderedPortionsEntity to delete
     */
    public void deleteOrderedPortion(OrderedPortionsEntity orderedPortion){
            int i = orderedPortions.indexOf(orderedPortion);
            if (i !=-1)
            try{
                this.orderedPortionsDao.deleteOrderedPortion(orderedPortion);
                startHelper("orderedPortions");
            }catch (Exception e){
                System.out.println("Model: deleteOrderedPortion: Exception when deleting orderedPortion, order #" + orderedPortion.getOrderId() + ", portion #" + orderedPortion.getPortionId());
                throw e;
            }
    }

    /**
     * <p>
     *     Update a table at database
     * </p>
     * @param table TablesEntity to update
     */
    public void updateTable(TablesEntity table){
        TablesEntity temp = new TablesEntity();
        int i =-1;
        try{
            //Find the updated table in list
            for(TablesEntity t: this.tables){
                if(t.getId() == table.getId()){
                    temp = t;
                    break;
                }
            }
            //Set the updated table over the old table if its in there
            i = this.tables.indexOf(temp);
            if(i != -1) {
                this.tablesDao.updateTable(table);
                startHelper("tables");
            }
        }catch (Exception e){
            System.out.println("Model: updateTable: Exception when updating table #"+table.getId());
            throw e;
        }
    }
    /**
     * <p>
     *     Update a order at database
     * </p>
     * @param order OrdersEntity to update
     */
    public void updateOrder(OrdersEntity order){
            OrdersEntity temp = new OrdersEntity();
            int i = -1;
            try{
                for(OrdersEntity t: this.orders){
                    if(t.getId() == order.getId()){
                        temp = t;
                        break;
                    }
                }
                i = this.orders.indexOf(temp);
                if (i!=-1) {
                    this.ordersDao.updateOrder(order);
                    startHelper("orders");
                }
            }catch (Exception e){
                System.out.println("Model: updateOrder: Exception when updating order#" + order.getId());
                throw e;
            }
    }
    /**
     * <p>
     *     Update a portion at database
     * </p>
     * @param portion PortionsEntity to update
     */
    public void updatePortion(PortionsEntity portion){
            PortionsEntity temp = new PortionsEntity();
            int i =-1;
            try{
                for(PortionsEntity t: this.portions){
                    if(t.getId() == portion.getId()){
                        temp = t;
                        break;
                    }
                }
                i = this.portions.indexOf(temp);
                if (i!=-1) {
                    this.portionsDao.updatePortion(portion);
                    startHelper("portions");
                }
            }catch (Exception e){
                System.out.println("Model: updatePortion: Exception when updating portion: "+ portion.getName());
                throw e;
            }
    }
    /**
     * <p>
     *     Update a orderedPortion at database
     * </p>
     * @param orderedPortion OrderedPortionsEntity to update
     */
    public void updateOrderedPortion(OrderedPortionsEntity orderedPortion){
            OrderedPortionsEntity temp = new OrderedPortionsEntity();
            int i = -1;
            try{
                //Find the updated table in list
                for(OrderedPortionsEntity t: this.orderedPortions){
                    if(t.getJunctionId() == orderedPortion.getJunctionId()){
                        temp = t;
                        break;
                    }
                }
                i = this.orderedPortions.indexOf(temp);
                if (i!=-1) {
                    this.orderedPortionsDao.updateOrderedPortion(orderedPortion);
                    startHelper("orderedPortions");
                }
            }catch (Exception e){
                System.out.println("Model: updateOrderedPortion: Exception when updating orderedPortion, order #"+orderedPortion.getOrderId()+", portion #"+orderedPortion.getPortionId());
                throw e;
            }
    }
    /**
     * <p>
     *     Update a reservation at database
     * </p>
     * @param reservation ReservationsEntity to update
     */
    public void updateReservation(ReservationsEntity reservation){
            ReservationsEntity temp = new ReservationsEntity();
            int i = -1;
            try{
                for(ReservationsEntity t: this.reservations){
                    if(t.getId() == reservation.getId()){
                        temp = t;
                        break;
                    }
                }
                i = this.reservations.indexOf(temp);
                if(i!=-1) {
                    this.reservations.set(i,reservation);
                    this.reservationsDao.updateReservation(reservation);
                    startHelper("reservations");
                }
            }catch (Exception e){
                System.out.println("Model: updateReservation: Exception when updating reservation for "+reservation.getClientName());
                throw e;
            }
    }
    /**
     * <p>
     *     Get all orders of the restaurant. Uses Queue to prevent concurrent read and write
     * </p>
     * @return Returns an ArrayList of orders
     */
    public ArrayList<OrdersEntity> getOrders(){
            try {
             queue.put(1);
            }catch (InterruptedException e){
                System.out.println("Get Orders Interrupted: " +e.getMessage());
            }
            ArrayList<OrdersEntity> list = this.orders;
            try {
                queue.take();
            }catch (InterruptedException e){
                System.out.println("Get Orders Interrupted: " +e.getMessage());
            }
            return list;
    }
    /**
     * <p>
     *     Get all tables of the restaurant. Uses Queue to prevent concurrent read and write
     * </p>
     * @return Returns an ArrayList of tables
     */
    public ArrayList<TablesEntity> getTables(){
        try {
            queue.put(1);
        }catch (InterruptedException e){
            System.out.println("Get Tables Interrupted: " +e.getMessage());
        }
        ArrayList<TablesEntity> list = this.tables;
        try {
            queue.take();
        }catch (InterruptedException e){
            System.out.println("Get Tables Interrupted: " +e.getMessage());
        }
        return list;
    }

    /**
     * <p>
     *     Get all tables of the size that matches the argument
     * </p>
     * @param tableSize - The size of the table
     * @return Returns an ArrayList of tables of the specified size
     */

    public ArrayList<TablesEntity> getTablesBySize(int tableSize){
        System.out.println("Getting tables by size " + tableSize);
        ArrayList<TablesEntity> tablesBySize = new ArrayList<TablesEntity>();
        for(TablesEntity table : this.tables){
            if(table.getSeats() == tableSize){
                tablesBySize.add(table);
            }
        }
        if(tablesBySize.isEmpty()){
            System.out.println("No tables of size " + tableSize + " were found");
            return null;
        } else {
            return tablesBySize;
        }
    }


    /**
     * <p>
     *     Get all portions of the restaurant. Uses Queue to prevent concurrent read and write
     * </p>
     * @return Returns an ArrayList of portions
     */

    public ArrayList<PortionsEntity> getPortions(){
        try {
            queue.put(1);
        }catch (InterruptedException e){
            System.out.println("Get Portions Interrupted: " +e.getMessage());
        }
        ArrayList<PortionsEntity> list = this.portions;
        try {
            queue.take();
        }catch (InterruptedException e){
            System.out.println("Get Portions Interrupted: " +e.getMessage());
        }
        return list;
    }
    /**
     * <p>
     *     Get all reservations of the restaurant. Uses Queue to prevent concurrent read and write
     * </p>
     * @return Returns an ArrayList of reservations
     */
    public ArrayList<ReservationsEntity> getReservations(){
        try {
            queue.put(1);
        }catch (InterruptedException e){
            System.out.println("Get Reservations Interrupted: " +e.getMessage());
        }
        ArrayList<ReservationsEntity> list = this.reservations;
        try {
            queue.take();
        }catch (InterruptedException e){
            System.out.println("Get Reservations Interrupted: " +e.getMessage());
        }
        return list;
    }
    /**
     * <p>
     *     Get a reservation by Table id.
     * </p>
     * @param tableID An integer of the table the reservation is booked for.
     * @return Returns a ReservationsEntity or null
     */

    public ArrayList<ReservationsEntity> getReservationsByTableID(int tableID) {
        System.out.println("Model: getReservationsByTableID : Getting reservations for table " + tableID);
        ArrayList<ReservationsEntity> reservations = this.getReservations();
        ArrayList<ReservationsEntity> reservationsForTable = new ArrayList<>();
        try {
            for (ReservationsEntity reservation : reservations) {
                if (reservation.getTableNumber() == tableID) {
                    reservationsForTable.add(reservation);
                }
            }
            if (!reservationsForTable.isEmpty()) {
                sortReservationsByDate(reservationsForTable);
                return reservationsForTable;
            }
        } catch (Exception e) {
            System.out.println("Model: getReservationsByTableID : Error while getting reservations for table " + tableID);
        }
        return null;
    }


    /**
     *
     * Sorts a list of reservations by date (descending)
     *
     * @param reservations An arraylist of reservation entities
     * @return Returns a sorted list
     */
    public ArrayList<ReservationsEntity> sortReservationsByDate(ArrayList<ReservationsEntity>reservations){
        System.out.println("Model: sortReservationsByDate: Sorting a list of reservations...");
        reservations.sort(new Comparator<ReservationsEntity>() {
            @Override
            public int compare(ReservationsEntity o1, ReservationsEntity o2) {
                if (o1.getReservationDate().toLocalDate() == null || o2.getReservationDate().toLocalDate() == null) {
                    return 0;
                }
                return o1.getReservationDate().toLocalDate().compareTo(o2.getReservationDate().toLocalDate());
            }
        });
        reservations.forEach((o) -> System.out.println(o.toString()));
        System.out.println("Model: sortReservationsByDate: Returning sorted list...");
        return reservations;

    }


    /**
     * Gets all active reservations from the database.
     * @return Returns an ArrayList of all the active reservations or null.
     */
    //getAllActiveReservations returns all active reservations, including those that are on-going and those that are after this point in time.
    public ArrayList<ReservationsEntity> getActiveReservations(){
        System.out.println("Model: getActiveReservations: Getting all active reservations...");
        ArrayList<ReservationsEntity> reservations = this.getReservations();
        ArrayList<ReservationsEntity> allActiveReservations = new ArrayList<ReservationsEntity>();
        try{
            for(ReservationsEntity reservation : reservations){
                if(!reservation.getReservationDate().toLocalDate().isBefore(LocalDate.now()) && !reservation.getEndTime().toLocalTime().isBefore(LocalTime.now())){
                    allActiveReservations.add(reservation);
                }
            }
            if(!allActiveReservations.isEmpty()){
                System.out.println("Model: getActiveReservations: Found active reservations, returning list...");
                return allActiveReservations;
            }
        }catch(Exception e){
            System.out.println("Model: getActiveReservations: An error occurred while getting all active reservations.");
        }
        System.out.println("Model: getActiveReservations: No active reservations were found, returning null...");
        return null;
    }


    /**
     * Filters all currently on-going reservations into a list.
     * @return Returns an arraylist of reservations that are currently on-going (at this point in time)
     */
    public ArrayList<ReservationsEntity> getCurrentReservations(){
        ArrayList<ReservationsEntity> reservations = this.getReservations();
        ArrayList<ReservationsEntity> currentReservations = new ArrayList<>();
        try {
            if (reservations.isEmpty()) {
                System.out.println("No reservations in database.");
                return null;
            } else {
                System.out.println("Reservations found from database.");
                for (ReservationsEntity reservation : reservations) {
                    if (reservation.getReservationDate().toLocalDate().equals(LocalDate.now()) && reservation.getStartTime().toLocalTime().isBefore(LocalTime.now()) && reservation.getEndTime().toLocalTime().isAfter(LocalTime.now())) {
                        currentReservations.add(reservation);
                    }
                }
            }
        }catch(Exception e) {
            System.out.println("An error occured in method getCurrentReservations");
        }

        if(!currentReservations.isEmpty()){
            return currentReservations;
        } else{
            return null;
        }
    }


    /**
     * <p>
     *     Get all orderedPortions of the restaurant. Uses Queue to prevent concurrent read and write
     * </p>
     * @return Returns an ArrayList of orderedPortions
     */
    public ArrayList<OrderedPortionsEntity> getOrderedPortions(){
        try {
            queue.put(1);
        }catch (InterruptedException e){
            System.out.println("Get orderedPortions Interrupted: " +e.getMessage());
        }
        ArrayList<OrderedPortionsEntity> list = this.orderedPortions;
        try {
            queue.take();
        }catch (InterruptedException e){
            System.out.println("Get OrderedPortions Interrupted: " +e.getMessage());
        }
        return list;
    }
    /**
     * <p>
     *     Get a table by its id.
     * </p>
     * @param id Table id
     * @return Returns a TablesEntity or null
     */
    public TablesEntity getTable(int id){
        for(TablesEntity table : this.tables)
        {
            if(table.getId() == id)
                return table;
        }
        return null;
    }
    /**
     * <p>
     *     Get a order by its id.
     * </p>
     * @param id Order id
     * @return Returns a Ordersentity or null
     */
    public OrdersEntity getOrders(int id){
        for(OrdersEntity order : this.orders)
        {
            if(order.getId() == id)
                return order;
        }
        return null;
    }
    /**
     * <p>
     *     Get a reservation by its id.
     * </p>
     * @param id Reservation id
     * @return Returns a ReservationsEntity or null
     */
    public ReservationsEntity getReservation(int id){
        for(ReservationsEntity reservation : this.reservations)
        {
            if(reservation.getId() == id)
                return reservation;
        }
        return null;
    }
    /**
     * <p>
     *     Get a portion by its id.
     * </p>
     * @param id portion id
     * @return Returns a PortionsEntity or null
     */
    public PortionsEntity getPortion(int id){
        for(PortionsEntity portion : this.portions)
        {
            if(portion.getId() == id)
                return portion;
        }
        return null;
    }

    /**
     * <p>
     *     Get a orderedPortion by its id
     * </p>
     * @param id orderedPortion id
     * @return Returns a OrderedPortionsEntity or null
     */
    public OrderedPortionsEntity getOrderedPortion(int id){
        for(OrderedPortionsEntity orderedPortion : this.orderedPortions)
        {
            if(orderedPortion.getJunctionId() == id)
                return orderedPortion;
        }
        return null;
    }
    public List<OrderedPortionsEntity> getOrderedPortionsByOrderId(int orderId){
        try {
            queue.put(1);
        }catch (Exception e){e.printStackTrace();}
        List<OrderedPortionsEntity> ops = orderedPortionsByOrderId.getOrDefault(orderId, null);
        try {
            queue.take();
        }catch (Exception e){e.printStackTrace();}
        return ops;
    }
}
