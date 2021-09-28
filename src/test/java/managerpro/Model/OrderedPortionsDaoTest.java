package managerpro.Model;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@DisplayName("OrderedPortionsDao luokan testit")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrderedPortionsDaoTest {
    private static OrderedPortionsDao orderedPortionsDao;
    private static SessionFactory sessionFactory;

    private static List<OrderedPortionsEntity> orderedPortions;
    private static OrderedPortionsEntity orderedPortion1;
    private static OrderedPortionsEntity orderedPortion2;
    private static OrderedPortionsEntity orderedPortion3;

    private static OrdersEntity order;

    @BeforeAll
    static void setUp() {
        //The usual setup..
        Configuration configuration = new Configuration();
        configuration.configure();
        sessionFactory = configuration.buildSessionFactory();
        orderedPortionsDao = new OrderedPortionsDao(sessionFactory);

        Session session  = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        //Create new Table and push to DB
        TablesEntity table = new TablesEntity(4);
        int tableId = (Integer)session.save(table);
        table.setId(tableId);

        //Create new order and push to DB
        order = new OrdersEntity("This will be deprecated soon!", tableId);
        int orderId = (Integer)session.save(order);
        order.setId(orderId);


        //Create some new portions and push to DB
        PortionsEntity portion1 = new PortionsEntity("Makkaraperunat", 4.95);
        PortionsEntity portion2 = new PortionsEntity("Kaalilaatikko", 9.95);

        int port1Id = (Integer)session.save(portion1);
        int port2Id = (Integer)session.save(portion2);
        portion1.setId(port1Id);
        portion2.setId(port2Id);
        transaction.commit();
        session.close();
        //Create orderedPortions
        orderedPortion1 = new OrderedPortionsEntity(order, portion1, "Ilman makkaraa",0);
        orderedPortion2 = new OrderedPortionsEntity(order, portion2, "",0);
        orderedPortion3 = new OrderedPortionsEntity(order, portion2, "Ilman laatikkoa",0);
    }
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
    void createOrderedPortion() {
        //Varmistetaan että tietokanta on tyhjä
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        assertEquals(0, session.createQuery("FROM OrderedPortionsEntity ").getResultList().size());
        //Luodaan tietokannan rivit
        orderedPortionsDao.createOrderedPortion(orderedPortion1);
        orderedPortionsDao.createOrderedPortion(orderedPortion2);
        orderedPortionsDao.createOrderedPortion(orderedPortion3);
        transaction.commit();

        transaction = session.beginTransaction();
        //Tarkistetaan että ne meni sinne
        assertEquals(3,session.createQuery("FROM OrderedPortionsEntity").getResultList().size());
        orderedPortions = session.createQuery("FROM OrderedPortionsEntity").getResultList();
        transaction.commit();
        session.close();
    }
    @Test
    @Order(2)
    void getAllOrderedPortions() {
        assertEquals(orderedPortions, orderedPortionsDao.getAllOrderedPortions());
    }

    @Test
    @Order(3)
    void getPortionsOfOrder() {
        assertEquals(orderedPortions, orderedPortionsDao.getPortionsOfOrder(order.getId()));
    }

    @Test
    @Order(4)
    void getOrderedPortion() {
        assertEquals(orderedPortion1,orderedPortionsDao.getOrderedPortion(orderedPortion1.getJunctionId()));
    }
    @Test
    @Order(5)
    void updateOrderedPortion() {
        orderedPortion1.setPortionDetails("Ilman perunaa");
        orderedPortionsDao.updateOrderedPortion(orderedPortion1);
        assertEquals(orderedPortion1,orderedPortionsDao.getOrderedPortion(orderedPortion1.getJunctionId()));
    }
    @Test
    @Order(6)
    void deleteOrderedPortion() {
        orderedPortionsDao.deleteOrderedPortion(orderedPortion1);
        assertNull(orderedPortionsDao.getOrderedPortion(orderedPortion1.getJunctionId()));
    }
}