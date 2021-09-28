package managerpro.Model;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import java.util.ArrayList;
import java.util.List;
/**
 * Data Access Object for performing CRUD on Orders table and returning OrdersEntity -objects when applicable.
 * {@link OrdersEntity}
 * @author OTP7
 */
public class OrdersDao implements IOrdersDao {

    /**
     * Hibernate session factory
     */
    private SessionFactory sessionFactory;

    /**
     * OrderDao constructor. Takes Hibernate session factory as a parameter
     * @param sessionFactory Hibernate SessionFactory
     */
    public  OrdersDao(SessionFactory sessionFactory){
        this.sessionFactory = sessionFactory;
    }

    /**
     * Return all orders in database
     * @return List of OrdersEntities
     */
    public ArrayList<OrdersEntity> getAllOrders(){
        try (Session session  = this.sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            @SuppressWarnings("unchecked")
            List<OrdersEntity> results = session.createQuery("from OrdersEntity ").getResultList();
            ArrayList<OrdersEntity> orders = new ArrayList<>(results);
            transaction.commit();
            return orders;
        }
        catch (Exception e)
        {
            throw e;
        }
    }

    /**
     * Get single order from database with it's id
     * @param orderId Id of the order
     * @return OrdersEntity
     */
    public OrdersEntity getOrder(int orderId){
        try(Session session  = this.sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            OrdersEntity order = new OrdersEntity();
            session.load(order, orderId);
            transaction.commit();
            return order;
        }
        catch (Exception e)
        {
            throw e;
        }

    }

    /**
     * Creates a new order based on the OrdersEntity supplied
     * @param order OrdersEntity to create
     */
    public void createOrder(OrdersEntity order){
        Transaction transaction = null;
        try(Session session  = this.sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            int id = (Integer)session.save(order);
            order.setId(id);
            transaction.commit();
        }
        catch (Exception e)
        {
            if(transaction != null) transaction.rollback();
            throw e;
        }
    }

    /**
     * Updates the columns in the database based on the supplied OrdersEntity
     * @param order Order to update
     */
    public void updateOrder(OrdersEntity order){
        Session session = null;
        Transaction transaction = null;
        try {
            session = this.sessionFactory.openSession();
            transaction = session.beginTransaction();
            session.update(order);
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
     * Deletes an order from the database
     * @param order Order to delete
     */
    public void deleteOrder(OrdersEntity order){
        Transaction transaction = null;
        try(Session session  = this.sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.delete(order);
            transaction.commit();
        }
        catch (Exception e)
        {
            if(transaction != null) transaction.rollback();
            throw e;
        }
    }
}
