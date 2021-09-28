package managerpro.Utils;

import managerpro.Model.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.junit.jupiter.api.*;
import java.sql.Date;
import java.sql.Time;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testataan LanguageUtils luokan i18n ja l10n työkaluja")

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LanguageUtilsTest {

    private static TablesEntity table;
    private static PortionsEntity portion;
    private static OrdersEntity order;
    private static ReservationsEntity reservation;
    private static OrderedPortionsEntity orderedPortion;

    private static SessionFactory sessionFactory;

    private static OrdersDao ordersDao;
    private static PortionsDao portionsDao;
    private static TablesDao tablesDao;
    private static ReservationsDao reservationsDao;
    private static OrderedPortionsDao orderedPortionsDao;
    @BeforeAll
    static void setUp(){
        Configuration configuration = new Configuration();
        configuration.configure();
        sessionFactory = configuration.buildSessionFactory();

        tablesDao = new TablesDao(sessionFactory);
        ordersDao = new OrdersDao(sessionFactory);
        reservationsDao = new ReservationsDao(sessionFactory);
        portionsDao = new PortionsDao(sessionFactory);
        orderedPortionsDao = new OrderedPortionsDao(sessionFactory);

        table = new TablesEntity(4);
        tablesDao.createTable(table);
        portion  = new PortionsEntity("ASDF",5.5);
        portionsDao.createPortion(portion);
        order = new OrdersEntity("waiting",table.getId());
        ordersDao.createOrder(order);
        orderedPortion = new OrderedPortionsEntity(order.getId(),portion.getId(),"Ei juustoa",0);
        reservation = new ReservationsEntity("Mikki Hiiri","0440123456", new Date(System.currentTimeMillis()),table.getId(),new Time(1000), new Time(2000));
        reservationsDao.createReservation(reservation);



    }
    @Test
    @Order(1)
    void getResourceBundleTest(){
        LanguageUtils.setLanguage("English");
        ResourceBundle rb = ResourceBundle.getBundle("messages", Locale.forLanguageTag("en-US"));
        System.out.println(rb);
        System.out.println(LanguageUtils.getResourceBundle());
        assertEquals(rb, LanguageUtils.getResourceBundle());
    }
    @Test
    @Order(2)
    void translateToStringOrder() {
        System.out.println(LanguageUtils.translateToString(order));
        assertTrue(order.toString().equalsIgnoreCase(LanguageUtils.translateToString(order)));
    }
    @Test
    @Order(3)
    void translateToStringTable() {
        assertTrue(table.toString().equalsIgnoreCase(LanguageUtils.translateToString(table)));
    }
    @Test
    @Order(4)
    void translateToStringPortion() {
        assertTrue(portion.toString().equalsIgnoreCase(LanguageUtils.translateToString(portion)));
    }
    @Test
    @Order(5)
    void translateToStringReservation() {
        assertTrue(reservation.toString().equalsIgnoreCase(LanguageUtils.translateToString(reservation)));
    }
    @Test
    @Order(6)
    void translateToStringOrderedPortion() {
        assertTrue(orderedPortion.toString().equalsIgnoreCase(LanguageUtils.translateToString(orderedPortion)));
    }
    @Test
    @Order(7)
    void getCurrentLocale(){
        LanguageUtils.setLanguage("");
        assertEquals(Locale.forLanguageTag("en-US"),LanguageUtils.getLocale());
        LanguageUtils.setLanguage("English");
        assertEquals(Locale.forLanguageTag("en-US"),LanguageUtils.getLocale());
        LanguageUtils.setLanguage("Suomi");
        assertEquals(Locale.forLanguageTag("fi-FI"),LanguageUtils.getLocale());
        LanguageUtils.setLanguage("Non-Language");
        assertEquals(Locale.forLanguageTag("en-US"),LanguageUtils.getLocale());
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
}