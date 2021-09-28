package managerpro.Model;

import org.hibernate.SessionFactory;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.TimerTask;

/**
 * Helper class for the model. Extends TimerTask to allow for scheduling and running the class in a different thread.
 * Running the SELECT operations in a different thread from the UI prevents locking up the UI
 * every time the database needs to be queried
 *
 * @author OTP7
 *
 */
public class ModelHelper extends TimerTask{
    /**
     * Orders Data access object
     */
    private OrdersDao ordersDao;
    /**
     * Reservations Data access object
     */
    private ReservationsDao reservationsDao;
    /**
     * OrderedPortions Data access object
     */
    private OrderedPortionsDao orderedPortionsDao;
    /**
     * Portions Data access object
     */
    private PortionsDao portionsDao;
    /**
     * Tables data access object
     */
    private TablesDao tablesDao;
    /**
     * PropertyChange support
     */
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    private String whatToGet;
    /**
     * <p>
     * ModelHelper constructor. Creates the DAOs. Takes hibernate session factory as a parameter.
     * </p>
     * @param sessionFactory Hibernate session factory
     */
    public ModelHelper(SessionFactory sessionFactory){
        this.ordersDao = new OrdersDao(sessionFactory);
        this.orderedPortionsDao = new OrderedPortionsDao(sessionFactory);
        this.reservationsDao = new ReservationsDao(sessionFactory);
        this.tablesDao= new TablesDao(sessionFactory);
        this.portionsDao= new PortionsDao(sessionFactory);
        this.whatToGet = "all";

    }
    /**
     * <p>
     * ModelHelper constructor. Creates the DAOs. Takes hibernate session factory and a string as parameters. String should have a value of:
     *
     *tables,
     *reservations,
     *orders,
     *portions,
     *OrderedPortions,
     *
     * to get the corresponding rows from the database.
     * </p>
     * @param sessionFactory Hibernate session factory
     * @param s A string that is used to control the data set that is fetched from DB.
     */
    public ModelHelper(SessionFactory sessionFactory, String s){
        this.ordersDao = new OrdersDao(sessionFactory);
        this.orderedPortionsDao = new OrderedPortionsDao(sessionFactory);
        this.reservationsDao = new ReservationsDao(sessionFactory);
        this.tablesDao= new TablesDao(sessionFactory);
        this.portionsDao= new PortionsDao(sessionFactory);
        this.whatToGet = s;
    }

    /**
     * <p>
     * Adds a PropertyChangeListener
     * </p>
     * @param propertyChangeListener PropertyChangeListener to add
     */
    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener){

        support.addPropertyChangeListener(propertyChangeListener);
    }
    /**
     * <p>
     * Removes a PropertyChangeListener
     * </p>
     * @param propertyChangeListener PropertyChangeListener to remove
     */
    public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        support.removePropertyChangeListener(propertyChangeListener);
    }

    /**
     * <p>
     *  The run method is run by Timer and when a new thread is spawned. It tries to put to the queue, and when
     *  that is done, lists of Entities are created from the Database data. After all the lists are fetched,
     *  a PropertyChangeListener is fired to take the data to Model.
     * </p>
     */
    public void run(){
        //System.out.println(whatToGet);
        long startTime = System.currentTimeMillis();
        ArrayList<OrdersEntity> orders = null;
        ArrayList<TablesEntity> tables = null;
        ArrayList<OrderedPortionsEntity> orderedPortions = null;
        ArrayList<ReservationsEntity> reservations = null;
        ArrayList<PortionsEntity> portions = null;
        //get data from database
        if(whatToGet.equals("all")) {
            orders = new ArrayList<>(ordersDao.getAllOrders());
            tables = new ArrayList<>(tablesDao.getAllTables());
            orderedPortions = new ArrayList<>(orderedPortionsDao.getAllOrderedPortions());
            reservations = new ArrayList<>(reservationsDao.getAllReservations());
            portions = new ArrayList<>(portionsDao.getAllPortions());
        }
        if(whatToGet.equals("tables")){tables = new ArrayList<>(tablesDao.getAllTables());}
        if(whatToGet.equals("reservations")){reservations = new ArrayList<>(reservationsDao.getAllReservations());}
        if(whatToGet.equals("portions")){portions = new ArrayList<>(portionsDao.getAllPortions());}
        if(whatToGet.equals("orders")){orders = new ArrayList<>(ordersDao.getAllOrders());}
        if(whatToGet.equals("orderedPortions")){orderedPortions = new ArrayList<>(orderedPortionsDao.getAllOrderedPortions());}
        long dbAccessTime = System.currentTimeMillis();
        //System.out.println("DB Access time for "+ whatToGet +": " + (dbAccessTime - startTime));

        //Send Lists to Model in order T-O-R-P-Op
        ArrayList<ArrayList> dbLists = new ArrayList<>();
        dbLists.add(tables);
        dbLists.add(orders);
        dbLists.add(reservations);
        dbLists.add(portions);
        dbLists.add(orderedPortions);
        //System.out.println(dbLists);
        support.firePropertyChange("dbLists", null, dbLists);
    }


}

