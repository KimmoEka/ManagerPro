package managerpro.Model;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.junit.jupiter.api.*;

import java.sql.Date;
import java.sql.Time;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Model luokan testit")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

class ModelTest {
    //The usual setup for our tests
    private static Model model;
    private static SessionFactory sessionFactory;

    private static TablesEntity table;
    private static PortionsEntity portion;
    private static OrdersEntity order;
    private static ReservationsEntity reservation;
    private static OrderedPortionsEntity orderedPortion;

    @BeforeAll
    static void setUp(){
        //Configure hibernate and disable Model -class scheduled updating
        Configuration configuration = new Configuration();
        configuration.configure();
        sessionFactory = configuration.buildSessionFactory();
        Model.setSessionFactory(sessionFactory);
        try {
            model = Model.getInstance();
        }catch (Exception e){
            e.printStackTrace();
        }


        table = new TablesEntity();
        table.setSeats(2);
        portion  = new PortionsEntity();
        order = new OrdersEntity();
        orderedPortion = new OrderedPortionsEntity();
        model.disableScheduling();
        //Just to run the code and see if it works
        model.enableScheduling();
        //Disable the scheduling again
        model.disableScheduling();
    }
    //The usual cleanup, make sure the database is clean for the next user
    @AfterAll
    static void cleanUp(){
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            Query queryDel1 = session.createQuery("DELETE from OrdersEntity ");
            Query queryDel2 = session.createQuery("DELETE from PortionsEntity ");
            Query queryDel3 = session.createQuery("DELETE from OrderedPortionsEntity ");
            Query queryDel4 = session.createQuery("DELETE from TablesEntity ");
            Query queryDel5 = session.createQuery("DELETE from ReservationsEntity ");
            //Query queryAlter1 = session.createSQLQuery("ALTER TABLE Portions AUTO_INCREMENT = 1  ");
            //Query queryAlter2 = session.createSQLQuery("ALTER TABLE Orders AUTO_INCREMENT = 1  ");
            //Query queryAlter3 = session.createSQLQuery("ALTER TABLE OrderedPortions AUTO_INCREMENT = 1  ");
            //Query queryAlter4 = session.createSQLQuery("ALTER TABLE Tables AUTO_INCREMENT = 1  ");
            //queryAlter1.executeUpdate();
            //queryAlter2.executeUpdate();
            //queryAlter3.executeUpdate();
            //queryAlter4.executeUpdate();

            //OrderedPortions first!
            queryDel3.executeUpdate();
            //Then the rest...
            queryDel1.executeUpdate();
            queryDel2.executeUpdate();

            //Reservations before Tables!
            queryDel5.executeUpdate();
            queryDel4.executeUpdate();

            transaction.commit();
        }
        catch (Exception e)
        {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Test
    @Order(1)
    @DisplayName("Test getting propertyChangeListeners")
    void getPropertyChangeListeners(){
        assertEquals(0,model.getPropertyChangeListeners().length);
    }

    @Test
    @Order(2)
    @DisplayName("Test adding a propertyChangeListeners")
    void addPropertyChangeListener() {
        model.addPropertyChangeListener(evt -> {
            System.out.println("I'm here");
        });
        assertEquals(1,model.getPropertyChangeListeners().length);
    }

    @Test
    @Order(3)
    @DisplayName("Test removing a propertyChangeListeners")
    void removePropertyChangeListener() {
        model.removePropertyChangeListener(model.getPropertyChangeListeners()[0]);
        assertEquals(0,model.getPropertyChangeListeners().length);
    }

    @Test
    @Order(4)
    @DisplayName("Test adding a table to Database")
    void addTable() {
        //Make sure db is Empty
        TablesDao tablesDao = new TablesDao(sessionFactory);
        assertEquals(0,tablesDao.getAllTables().size());

        //Add table via Model
        model.addTable(table);

        //Check if it went to db
        assertEquals(1,tablesDao.getAllTables().size());

        //Keep table in DB, we will need it later

    }

    @Test
    @Order(5)
    @DisplayName("Test adding a portion to Database")
    void addPortion() {
        //Make sure db is Empty
        PortionsDao portionsDao = new PortionsDao(sessionFactory);
        assertEquals(0,portionsDao.getAllPortions().size());
        portion.setName("Juusto");
        portion.setPrice(5.5);
        //Add table via Model
        model.addPortion(portion);

        //Check if it went to db
        assertEquals(1,portionsDao.getAllPortions().size());

        //Keep portion in DB, we will need it later
    }

    @Test
    @Order(6)
    @DisplayName("Test adding an order to Database")
    void addOrder() {
        //Make sure db is Empty
        OrdersDao ordersDao = new OrdersDao(sessionFactory);

        assertEquals(0,ordersDao.getAllOrders().size());
        order.setTableNumber(table.getId());
        //Add table via Model
        model.addOrder(order);

        //Check if it went to db
        assertEquals(1,ordersDao.getAllOrders().size());

        //Also keep order in db, needed later

    }

    @Test
    @Order(7)
    @DisplayName("Test adding a reservation to Database")
    void addReservation() {
        ReservationsDao reservationsDao = new ReservationsDao(sessionFactory);

        assertEquals(0,reservationsDao.getAllReservations().size());
        reservation = new ReservationsEntity("asd","123", new Date(System.currentTimeMillis()), table.getId(),new Time(1000),new Time(2000));
        model.addReservation(reservation);

        assertEquals(1,reservationsDao.getAllReservations().size());


    }

    @Test
    @Order(8)
    @DisplayName("Test adding an orderedPortion to Database")
    void addOrderedPortion() {
        OrderedPortionsDao orderedPortionsDao = new OrderedPortionsDao(sessionFactory);
        assertEquals(0,orderedPortionsDao.getAllOrderedPortions().size());

        orderedPortion.setPortionDetails("asd");
        orderedPortion.setOrderId(order.getId());
        orderedPortion.setPortionId(portion.getId());

        model.addOrderedPortion(orderedPortion);

        assertEquals(1,orderedPortionsDao.getAllOrderedPortions().size());
    }

    @Test
    @Order(9)
    @DisplayName("Test updating a table in Database")
    void updateTable() {
        TablesDao tablesDao = new TablesDao(sessionFactory);

        table.setSeats(6);

        assertNotEquals(table,tablesDao.getTable(table.getId()));
        model.updateTable(table);

        assertEquals(table, tablesDao.getTable(table.getId()));


    }

    @Test
    @Order(10)
    @DisplayName("Test updating an order in Database")
    void updateOrder() {
        OrdersDao ordersDao = new OrdersDao(sessionFactory);

        order.setOrderDetails("asfasdfasdf");

        assertNotEquals(order,ordersDao.getOrder(order.getId()));
        model.updateOrder(order);

        assertEquals(order, ordersDao.getOrder(order.getId()));


    }

    @Test
    @Order(11)
    @DisplayName("Test updating a portion in Database")
    void updatePortion() {
        PortionsDao portionsDao= new PortionsDao(sessionFactory);

        portion.setPrice(6.9);

        assertNotEquals(portion, portionsDao.getPortion(portion.getId()));

        model.updatePortion(portion);

        assertEquals(portion, portionsDao.getPortion(portion.getId()));
    }

    @Test
    @Order(12)
    @DisplayName("Test updating an orderedPortion in Database")
    void updateOrderedPortion() {
        OrderedPortionsDao orderedPortionsDao = new OrderedPortionsDao(sessionFactory);

        orderedPortion.setPortionDetails("asldkfjlaksdjf");

        assertNotEquals(orderedPortion, orderedPortionsDao.getOrderedPortion(orderedPortion.getJunctionId()));

        model.updateOrderedPortion(orderedPortion);

        assertEquals(orderedPortion, orderedPortionsDao.getOrderedPortion(orderedPortion.getJunctionId()));
    }

    @Test
    @Order(13)
    @DisplayName("Test updating a reservation in Database")
    void updateReservation() {
        ReservationsDao reservationsDao = new ReservationsDao(sessionFactory);

        reservation.setClientName("Mikki Hiiri");

        assertNotEquals(reservation, reservationsDao.getReservation(reservation.getId()));

        model.updateReservation(reservation);


        assertEquals(reservation, reservationsDao.getReservation(reservation.getId()));

    }

    @Test
    @Order(14)
    @DisplayName("Test getting orders from Database")
    void getOrders() {
        assertEquals(order,model.getOrders().get(0));
    }

    @Test
    @Order(15)
    @DisplayName("Test getting tables from Database")
    void getTables() {
        assertEquals(table,model.getTables().get(0));
    }

    @Test
    @Order(16)
    @DisplayName("Test getting portions from Database")
    void getPortions() {
        assertEquals(portion,model.getPortions().get(0));
    }

    @Test
    @Order(17)
    @DisplayName("Test getting reservations from Database")
    void getReservations() {
        assertEquals(reservation,model.getReservations().get(0));
    }

    @Test
    @Order(18)
    @DisplayName("Test getting orderedPortions from Database")
    void getOrderedPortions() {
        assertEquals(orderedPortion,model.getOrderedPortions().get(0));
    }

    @Test
    @Order(19)
    @DisplayName("Test deleting an orderedPortion from Database")
    void deleteOrderedPortion() {
        OrderedPortionsDao orderedPortionsDao = new OrderedPortionsDao(sessionFactory);
        assertEquals(1,orderedPortionsDao.getAllOrderedPortions().size());

        model.deleteOrderedPortion(orderedPortion);

        assertEquals(0,orderedPortionsDao.getAllOrderedPortions().size());
    }

    @Test
    @Order(20)
    @DisplayName("Test deleting an order from Database")
    void deleteOrder() {
        OrdersDao ordersDao = new OrdersDao(sessionFactory);
        assertEquals(1,ordersDao.getAllOrders().size());

        model.deleteOrder(order);

        assertEquals(0,ordersDao.getAllOrders().size());
    }

    @Test
    @Order(21)
    @DisplayName("Test deleting a reservation from Database")
    void deleteReservation() {
        ReservationsDao reservationsDao= new ReservationsDao(sessionFactory);
        assertEquals(1,reservationsDao.getAllReservations().size());

        model.deleteReservation(reservation);

        assertEquals(0,reservationsDao.getAllReservations().size());
    }

    @Test
    @Order(22)
    @DisplayName("Test deleting a table (entity) from Database")
    void deleteTable() {
        TablesDao tablesDao = new TablesDao(sessionFactory);
        assertEquals(1,tablesDao.getAllTables().size());

        model.deleteTable(table);

        assertEquals(0,tablesDao.getAllTables().size());

    }

    @Test
    @Order(23)
    @DisplayName("Test deleting a portion from Database")
    void deletePortion() {
        PortionsDao portionsDao = new PortionsDao(sessionFactory);
        assertEquals(1, portionsDao.getAllPortions().size());

        model.deletePortion(portion);

        assertEquals(0,portionsDao.getAllPortions().size());
    }
}