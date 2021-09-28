package managerpro.Model;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import java.util.List;
/**
 * Data Access Object for performing CRUD on Portions table and returning PortionsEntity -objects when applicable.
 * {@link PortionsEntity}
 * @author OTP7
 */
public class PortionsDao implements IPortionsDao {
    /**
     * Hibernate session factory
     */
    private SessionFactory sessionFactory;

    /**
     * PortionsDao Constructor, takes Hibernate session factory as parameter
     * @param sessionFactory Hibernate SessionFactory
     */
    public PortionsDao(SessionFactory sessionFactory){
        this.sessionFactory = sessionFactory;
    }

    /**
     * Returns all Portions in database
     * @return List of PortionsEntities
     */
    public List<PortionsEntity> getAllPortions() {
        Session session = null;
        Transaction transaction = null;

        try {
            session = this.sessionFactory.openSession();
            transaction = session.beginTransaction();
            @SuppressWarnings("unchecked")
            List<PortionsEntity> results = session.createQuery("from PortionsEntity ").getResultList();

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
     * Retuns a single PortionsEntity by its id
     * @param portionId Id of PortionsEntity
     * @return PortionsEntity
     */
    public PortionsEntity getPortion(int portionId) {
        Session session = null;
        PortionsEntity portion;
        Transaction transaction = null;
        try {
            session = this.sessionFactory.openSession();
            transaction = session.beginTransaction();
            portion = session.get(PortionsEntity.class, portionId);
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

        return portion;
    }

    /**
     * Creates a new row in database based on PortionsEntity supplied
     * @param portion PortionsEntity to create to database
     */
    public void createPortion(PortionsEntity portion) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = this.sessionFactory.openSession();
            transaction = session.beginTransaction();
            int id = (Integer)session.save(portion);
            portion.setId(id);
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
     * Updates a row in database based on PortionsEntity supplied
     * @param portion PortionsEntity to update
     */
    public void updatePortion(PortionsEntity portion) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = this.sessionFactory.openSession();
            transaction = session.beginTransaction();
            session.update(portion);
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
     * Deletes a row in database based on the PortionsEntity supplied
     * @param portion PortionsEntity to delete
     */
    public void deletePortion(PortionsEntity portion) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = this.sessionFactory.openSession();
            transaction = session.beginTransaction();
            session.remove(portion);
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
