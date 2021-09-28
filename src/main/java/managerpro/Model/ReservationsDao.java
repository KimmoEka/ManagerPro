package managerpro.Model;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
/**
 * Data Access Object for performing CRUD on Reservations table and returning ReservationsEntity -objects when applicable.
 * {@link PortionsEntity}
 * @author OTP7
 */
public class ReservationsDao implements IReservationsDao {
    /**
     * Hibernate SessionFactory
     */
    private SessionFactory sessionFactory;

    /**
     * ReservationsDAO constructor, takes Hibernate SessionFactory as parameter
     * @param sessionFactory Hibernate SessionFactory
     */
    public  ReservationsDao(SessionFactory sessionFactory){
        this.sessionFactory = sessionFactory;
    }

    /**
     * Returns all reservations in database
     * @return List of ReservationEntities
     */
    public List<ReservationsEntity> getAllReservations() {

        Session session = null;
        Transaction transaction = null;

        try {
            session = this.sessionFactory.openSession();
            transaction = session.beginTransaction();
            @SuppressWarnings("unchecked")
            List<ReservationsEntity> results = session.createQuery("from ReservationsEntity ").getResultList();

            transaction.commit();

            return results;
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

    /**
     * Returns a list of Reservations which have the supplied tableId
     * @param tableId Id of the table where the reservation was booked
     * @return List of ReservationEntities
     */
    public List<ReservationsEntity> getReservationsByTableId(int tableId) {

        Session session = null;
        Transaction transaction = null;
        try {
            session = this.sessionFactory.openSession();
            transaction = session.beginTransaction();
            @SuppressWarnings("unchecked")
            List<ReservationsEntity> results = session.createQuery("from ReservationsEntity E WHERE E.tableNumber = "+tableId).getResultList();
            transaction.commit();
            return results;
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

    /**
     * Returns a single Reservation entity which has the id supplied
     * @param reservationId Id to look for in DB
     * @return ReservationsEntity
     */
    public ReservationsEntity getReservation(int reservationId) {
        Session session = null;
        ReservationsEntity reservation;
        Transaction transaction = null;
        try {
            session = this.sessionFactory.openSession();
            transaction = session.beginTransaction();
            reservation = session.get(ReservationsEntity.class, reservationId);
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

        return reservation;
    }

    /**
     * Create a new reservation in database based on the ReservationEntity supplied
     * @param reservation ReservationEntity to create
     */
    public void createReservation(ReservationsEntity reservation) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = this.sessionFactory.openSession();
            transaction = session.beginTransaction();
            int id = (Integer)session.save(reservation);
            reservation.setId(id);
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

    /**
     * Updates a reservation in database based on the ReservationsEntity supplied
     * @param reservation ReservationsEntity to update
     */
    public void updateReservation(ReservationsEntity reservation) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = this.sessionFactory.openSession();
            transaction = session.beginTransaction();
            session.update(reservation);
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

    /**
     * Deletes a reservation from the database based on the ReservationEntity supplied
     * @param reservation ReservationEntity to delete
     */
    public void deleteReservation(ReservationsEntity reservation) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = this.sessionFactory.openSession();
            transaction = session.beginTransaction();
            session.remove(reservation);
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
