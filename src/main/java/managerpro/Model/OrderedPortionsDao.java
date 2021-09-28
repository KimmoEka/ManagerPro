package managerpro.Model;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

/**
 * Data Access Object for performing CRUD on OrderedPortions table and returning OrderedPortionsEntity -objects when applicable.
 * {@link OrderedPortionsEntity}
 * @author OTP7
 */
public class OrderedPortionsDao implements IOrderedPortionsDao{
    /**
     * Hibernate Session factory
     */
    private SessionFactory sessionFactory;

    /**
     * OrderedPortionsDao constructor
     * @param sessionFactory Hibernate sessionFactory
     */
    public OrderedPortionsDao(SessionFactory sessionFactory){this.sessionFactory = sessionFactory;}

    /**
     * Return all OrderedPortions in database
     * @return List of OrderedPortionsEntities
     */
    public List<OrderedPortionsEntity> getAllOrderedPortions(){
        Session session  = null;
        Transaction transaction = null;
        try{
            session = this.sessionFactory.openSession();
            transaction = session.beginTransaction();
            @SuppressWarnings("unchecked")
            List<OrderedPortionsEntity> results = session.createQuery("FROM OrderedPortionsEntity").getResultList();
            transaction.commit();
            return results;
        }
        catch (Exception e){
            if (transaction !=null)
                transaction.rollback();
            throw e;
        }finally {
            if (session !=null)
                session.close();
        }
    }

    /**
     * Return list orderedPortionEntities which have order_id of set parameter
     * @param orderId Integer: OrderID of orderedPortion
     * @return List of OrderedPortions
     */
    public List<OrderedPortionsEntity> getPortionsOfOrder(int orderId) {
        Session session = null;
        Transaction transaction =null;
        try{
            session = this.sessionFactory.openSession();
            transaction = session.beginTransaction();
            @SuppressWarnings("unchecked")
            List<OrderedPortionsEntity> results = session.createQuery("FROM OrderedPortionsEntity E WHERE E.orderId = "+orderId).getResultList();
            return results;
        }catch (Exception e){
            if (transaction!=null)
                transaction.rollback();
            throw e;
        }finally {
            if(session !=null)
                session.close();
        }
    }

    /**
     * Return single orderedPortions with junction_id of set parameter
     * @param orderedPortionId junction_id of orderedPortion
     * @return returns OrderedPortionsEnityt
     */
    public OrderedPortionsEntity getOrderedPortion(int orderedPortionId) {
        Session session = null;
        Transaction transaction = null;
        OrderedPortionsEntity orderedPortion;
        try {
            session = this.sessionFactory.openSession();
            transaction = session.beginTransaction();
            orderedPortion = session.get(OrderedPortionsEntity.class, orderedPortionId);
            transaction.commit();
            return orderedPortion;
        } catch (Exception e) {
            if (transaction != null)
                transaction.rollback();
            throw e;
        } finally {
            if (session != null)
                session.close();
        }
    }

    /**
     * Creates a row in database for ordered portion based on the orderedPortionsEntity supplied.
     * @param orderedPortion OrderedPortionsEntity to be added to DB
     */
    public void createOrderedPortion(OrderedPortionsEntity orderedPortion) {
        Session session = null;
        Transaction transaction = null;
        try{
            session = this.sessionFactory.openSession();
            transaction = session.beginTransaction();
            int id = (Integer)session.save(orderedPortion);
            orderedPortion.setJunctionId(id);
            transaction.commit();
        }catch (Exception e){
            if (transaction != null)
                transaction.rollback();
            throw e;
        }finally {
            if(session !=null)
                session.close();
        }

    }

    /**
     * Updates a row in database, where ID == orderedPortions.getJunctionID with data of the orderedPortions supplied
     * @param orderedPortion OrderedPortionsEntity to be updated
     */
    public void updateOrderedPortion(OrderedPortionsEntity orderedPortion) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = this.sessionFactory.openSession();
            transaction = session.beginTransaction();
            session.update(orderedPortion);
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
     * Deletes an orderedPortionsEntity form DB
     * @param orderedPortion OrderedPortionEntity to delete
     */
    public void deleteOrderedPortion(OrderedPortionsEntity orderedPortion) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = this.sessionFactory.openSession();
            transaction = session.beginTransaction();
            session.remove(orderedPortion);
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
