package managerpro.Model;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import java.util.ArrayList;
import java.util.List;
/**
 * Data Access Object for performing CRUD on Tables table and returning TablesEntity -objects when applicable.
 * {@link PortionsEntity}
 * @author OTP7
 */
public class TablesDao implements ITablesDao {
    /**
     * Hibernate SessionFactory
     */
    private SessionFactory sessionFactory;

    /**
     * Constructor for TablesDao, takes hibernate SessionFactory as parameter
     * @param sessionFactory Hibernate SessionFactory
     */
    public  TablesDao(SessionFactory sessionFactory){
        this.sessionFactory = sessionFactory;
    }

    /**
     * Return all tables from the databse
     * @return List of TablesEntity
     */
    public ArrayList<TablesEntity> getAllTables(){
        Transaction transaction = null;
        try (Session session  = this.sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            @SuppressWarnings("unchecked")
            List<TablesEntity> results = session.createQuery("from TablesEntity ").getResultList();
            ArrayList<TablesEntity> tables = new ArrayList<TablesEntity>(results);
            transaction.commit();
            return tables;
        }
        catch (Exception e)
        {
            throw e;
        }
    }

    /**
     * Return a single table from database with id supplied
     * @param tableId id to look for in database
     * @return TablesEntity
     */
    public TablesEntity getTable(int tableId){
        Transaction transaction = null;
        try(Session session  = this.sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            TablesEntity table = new TablesEntity();
            session.load(table, tableId);
            transaction.commit();
            return table;
        }
        catch (Exception e)
        {
            throw e;
        }

    }

    /**
     * Create a table like TablesEntity supplied to database
     * @param table TablesEntity to create
     */
    public void createTable(TablesEntity table){
        Transaction transaction = null;
        try(Session session  = this.sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            int id = (Integer)session.save(table);
            table.setId(id);
            transaction.commit();
        }
        catch (Exception e)
        {
            if(transaction != null) transaction.rollback();
            throw e;
        }
    }

    /**
     * Update a table in database that matched the supplied TablesEntity
     * @param table TablesEntity to update
     */
    public void updateTable(TablesEntity table){
        Session session = null;
        Transaction transaction = null;
        try {
            session = this.sessionFactory.openSession();
            transaction = session.beginTransaction();
            session.update(table);
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
     * Deletes a matching table from the db
     * @param table TablesEntity to delete
     */
    public void deleteTable(TablesEntity table){
        Transaction transaction = null;
        try(Session session  = this.sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.delete(table);
            transaction.commit();
        }
        catch (Exception e)
        {
            if(transaction != null) transaction.rollback();
            throw e;
        }
    }
}
