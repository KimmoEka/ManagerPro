package managerpro.Model;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.junit.jupiter.api.*;


import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@DisplayName("Testataan PortionsDao luokan metodit")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PortionsDaoTest {
    private static PortionsDao portionsDao;
    private static SessionFactory sesFac;
    private static List<PortionsEntity> portions;
    private static PortionsEntity portion1;
    private static PortionsEntity portion2;
    private static PortionsEntity portion3;
    @BeforeAll
    static void setUp()
    {
        Configuration configuration = new Configuration();
        configuration.configure();
        sesFac = configuration.buildSessionFactory();
        portionsDao = new PortionsDao(sesFac);

        portion1 = new PortionsEntity("Makkaraperunat", 4.95);
        portion2 = new PortionsEntity("Kaalilaatikko", 9.95);
        portion3 = new PortionsEntity("Pizza Americana", 10.90);
    }
    @AfterAll
    static void cleanUp(){
        // Empty the Users table before testing
        Session session = null;
        Transaction transaction = null;
        try {
            session = sesFac.openSession();
            transaction = session.beginTransaction();
            Query query = session.createQuery("DELETE from PortionsEntity ");
            Query query2 = session.createSQLQuery("ALTER TABLE Portions AUTO_INCREMENT = 1  ");
            query.executeUpdate();
            query2.executeUpdate();
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
    void createPortion() {
        //Varmista että tietokanta on tyhjä
        Session session = sesFac.openSession();
        Transaction transaction = session.beginTransaction();
        portions = session.createQuery("from PortionsEntity ").getResultList();
        transaction.commit();
        assertEquals(0, portions.size());


        portionsDao.createPortion(portion1);
        portionsDao.createPortion(portion2);
        portionsDao.createPortion(portion3);

        //Tarkista että annokset menivät tietokantaan
        transaction = session.beginTransaction();
        portions = session.createQuery("from PortionsEntity ").getResultList();
        assertEquals(3,portions.size());
        transaction.commit();
        session.close();
    }
    @Test
    @Order(2)
    void getPortion() {
        assertEquals(portion1, portionsDao.getPortion(portion1.getId()));
    }
    @Test
    @Order(3)
    void updatePortion() {
        portion1.setPrice(5.95);
        assertNotEquals(portion1, portionsDao.getPortion(portion1.getId()));
        portionsDao.updatePortion(portion1);
        assertEquals(portion1, portionsDao.getPortion(portion1.getId()));
    }

    @Test
    @Order(4)
    void deletePortion() {
        portionsDao.deletePortion(portion3);
        assertNull(portionsDao.getPortion(portion3.getId()));
    }
    @Test
    @Order(5)
    void getAllPortions() {
        ArrayList<PortionsEntity> list = new ArrayList<>();
        list.add(portion1);
        list.add(portion2);

        List<PortionsEntity> temp = portionsDao.getAllPortions();
        ArrayList<PortionsEntity> dbList = new ArrayList<>(temp);
        assertEquals(list,dbList);
    }
    @Test
    @Order(6)
    void getPortionInvalidIndex() {
        assertNull(portionsDao.getPortion(-1));
    }
}