package managerpro.Model;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Time;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testataan ReservationsDao olion metodit")
class ReservationsDaoTest {
    private static ReservationsDao reservationsDao;
    private static SessionFactory sessionFactory;
    private static TablesDao tablesDao;

    @BeforeAll
    static void setUp() throws Exception {
        Configuration configuration = new Configuration();

        // Change configuration to use testdb
        configuration.setProperty("hibernate.connection.url", "jdbc:mariadb://10.114.32.44:3306/testdb");
        configuration.configure();

        sessionFactory = configuration.buildSessionFactory();
        reservationsDao = new ReservationsDao(sessionFactory);
        tablesDao = new TablesDao(sessionFactory);

        emptyTable();
    }

    static void emptyTable () {
        // Empty the Reservations table before testing
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            Query query = session.createQuery("DELETE from ReservationsEntity ");
            query.executeUpdate();
            Query query2 = session.createQuery("DELETE from OrdersEntity ");
            query2.executeUpdate();

            Query query3 = session.createQuery("DELETE from TablesEntity ");
            query3.executeUpdate();
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

    @AfterEach
    void emptyTableAfterEach () {
        emptyTable();
    }

    @Test
    @DisplayName("Testataan kaikkien tilausten haku")
    void getAllReservations() {
        // Test that returns empty array list
        assertEquals(0, reservationsDao.getAllReservations().size());

        TablesEntity table1 = new TablesEntity();
        table1.setSeats(2);
        tablesDao.createTable(table1);
        // Test that returns the same number as created reservations
        ReservationsEntity firstReservation = new ReservationsEntity("amal","46111222", new Date(System.currentTimeMillis()), table1.getId(),new Time(1000),new Time(2000));
        reservationsDao.createReservation(firstReservation);

        TablesEntity table2 = new TablesEntity();
        table2.setSeats(2);
        tablesDao.createTable(table2);
        reservationsDao.createReservation(new ReservationsEntity("juho", "46555444", new Date(System.currentTimeMillis()), table2.getId(), new Time(1000),new Time(2000)));

        List<ReservationsEntity> reservations = reservationsDao.getAllReservations();
        assertEquals(2, reservations.size());
        assertEquals(firstReservation, reservations.get(0));
    }

    @Test
    @DisplayName("Testataan tilausten luonti")
    void createReservation() {

        // Test reservation creation
        TablesEntity table1 = new TablesEntity();
        table1.setSeats(2);
        tablesDao.createTable(table1);
        ReservationsEntity reservation = new ReservationsEntity("amal","46111222", new Date(System.currentTimeMillis()), table1.getId(),new Time(1000),new Time(2000));
        reservationsDao.createReservation(reservation);
        assertNotEquals(0, reservation.getId());

        // Test that fetching the created reservation from database works
        ReservationsEntity reservationFromDb = reservationsDao.getReservation(reservation.getId());
        assertEquals(reservation, reservationFromDb);
    }

    @Test
    @DisplayName("Testataan tilausten päivitys")
    void updateReservation() {

        // Create reservation
        TablesEntity table1 = new TablesEntity();
        table1.setSeats(2);
        tablesDao.createTable(table1);

        ReservationsEntity reservation = new ReservationsEntity("amal","46111222", new Date(System.currentTimeMillis()), table1.getId(),new Time(1000),new Time(2000));
        reservationsDao.createReservation(reservation);

        ReservationsEntity reservationFromDb = reservationsDao.getReservation(reservation.getId());

        assertEquals(reservation, reservationFromDb);

        // Update reservation
        reservation.setClientName("Amal");
        reservation.setClientPhone("46555333");
        reservation.setReservationDate(new Date(System.currentTimeMillis()));

        reservationsDao.updateReservation(reservation);

        ReservationsEntity updatedReservationFromDb = reservationsDao.getReservation(reservation.getId());

        assertEquals(reservation, updatedReservationFromDb);
    }

    @Test
    @DisplayName("Testataan tilausten poisto tietokannasta")
    void deleteReservation() {

        // Create a reservation in DB
        TablesEntity table = new TablesEntity();
        table.setSeats(2);
        tablesDao.createTable(table);

        ReservationsEntity reservation = new ReservationsEntity("Antero", "10073819", new Date(System.currentTimeMillis()), table.getId(), new Time(1000), new Time(2000));
        reservationsDao.createReservation(reservation);

        // Confirm that the reservation is in DB
        ReservationsEntity reservationFromDb = reservationsDao.getReservation(reservation.getId());
        assertEquals(reservation, reservationFromDb);

        // Delete reservation and confirm that it's not in the DB
        reservationsDao.deleteReservation(reservation);

        reservationFromDb = reservationsDao.getReservation(reservation.getId());
        assertEquals(null, reservationFromDb);

    }
    @Test
    @DisplayName("Tilauksen haku pöydän numerolla")
    void getReservationByTableId()
    {
        TablesEntity table = new TablesEntity(2);
        TablesDao asd = new TablesDao(sessionFactory);
        asd.createTable(table);
        ReservationsEntity resent1 = new ReservationsEntity("mikki","123",new Date(System.currentTimeMillis()),table.getId(),new Time(1000),new Time(2000));
        ReservationsEntity resent2 = new ReservationsEntity("mikki","123",new Date(System.currentTimeMillis()),table.getId(),new Time(1000),new Time(2000));
        ReservationsEntity resent3 = new ReservationsEntity("mikki","123",new Date(System.currentTimeMillis()),table.getId(),new Time(1000),new Time(2000));
        reservationsDao.createReservation(resent1);
        reservationsDao.createReservation(resent2);
        reservationsDao.createReservation(resent3);
        ArrayList<ReservationsEntity> listResEnt = new ArrayList<>();
        listResEnt.add(resent1);
        listResEnt.add(resent2);
        listResEnt.add(resent3);
        ArrayList<ReservationsEntity> newResEnt = new ArrayList(reservationsDao.getReservationsByTableId(table.getId()));
        reservationsDao.deleteReservation(resent1);
        reservationsDao.deleteReservation(resent2);
        reservationsDao.deleteReservation(resent3);
        asd.deleteTable(table);
        assertEquals(listResEnt,newResEnt);
    }
    @Test
    @DisplayName("Tilauksen haku olemattomalla pöydän numerolla")
    void getReservationByTableIdNonTableId()
    {

        ArrayList<ReservationsEntity> newResEnt = new ArrayList(reservationsDao.getReservationsByTableId(-5));
        assertEquals(0,newResEnt.size());
    }
}