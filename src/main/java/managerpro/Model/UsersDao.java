package managerpro.Model;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.List;
import java.util.Set;
/**
 * Data Access Object for performing CRUD on Users table and returning UsersEntity -objects when applicable.
 * {@link TablesEntity}
 * @author OTP7
 */
public class UsersDao implements IUsersDao {
    /**
     * Hibernate SessionFactory
     */
    private SessionFactory sessionFactory;
    /**
     * Javax ValidatorFactory
     */
    private ValidatorFactory validatorFactory;

    /**
     * UsersDao constructor, takes Hibernate SessionFactory and a Javax ValidatorFactory
     * @param sessionFactory Hibernate SessionFactory
     * @param validatorFactory Javax ValidatorFactory
     */
    public  UsersDao(SessionFactory sessionFactory, ValidatorFactory validatorFactory){
        this.sessionFactory = sessionFactory;
        this.validatorFactory = validatorFactory;
    }

    /**
     * Returns all users in database
     * @return List of UsersEntity
     */
    public List<UsersEntity> getAllUsers() {
        Session session = null;
        Transaction transaction = null;

        try {
            session = this.sessionFactory.openSession();
            transaction = session.beginTransaction();
            @SuppressWarnings("unchecked")
            List<UsersEntity> results = session.createQuery("from UsersEntity ").getResultList();

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
     * Returns UserEntity which has the id supplied
     * @param userId Id to look for in Database
     * @return UsersEntity to return
     */
    public UsersEntity getUser(int userId) {
        Session session = null;
        UsersEntity user;
        Transaction transaction = null;
        try {
            session = this.sessionFactory.openSession();
            transaction = session.beginTransaction();
            user = session.get(UsersEntity.class, userId);
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

        return user;
    }

    /**
     * Returns a UsersEntity which has username of string supplied
     * @param username String to look fir in database
     * @return UsersEntity. If not found, returns null
     */
    public UsersEntity getUserByUsername(String username) {
        Session session = null;
        UsersEntity user;
        Transaction transaction = null;

        try {
            session = this.sessionFactory.openSession();
            transaction = session.beginTransaction();

            Query userQuery = session.createQuery("FROM UsersEntity E Where E.username LIKE ?1");
            @SuppressWarnings("unchecked")
            List<UsersEntity> results = userQuery.setParameter(1,username).list();
            if (results.size() != 0)
            user = results.get(0);
            else
                user =null;
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


        return user;
    }


    /**
     * Creates user like UsersEntity supplied to database. Validates user input with validator.
     * @param user UserEntity to create
     */
    public void createUser(UsersEntity user) {
        Session session = null;
        Transaction transaction = null;

        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<UsersEntity>> constraintViolations = validator.validate(user);

        if (constraintViolations.size() > 0) {
            String errorString = "";
            for (ConstraintViolation<UsersEntity> violation : constraintViolations) {
                errorString += violation.getMessage() + ", ";
            }

            throw new IllegalArgumentException(errorString);
        } else {
            System.out.println("Valid Object");
        }

        try {
            session = this.sessionFactory.openSession();
            transaction = session.beginTransaction();
            int id = (Integer)session.save(user);
            user.setId(id);
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
     * Updates user like UsersEntity supplied in database
     * @param user UsersEntity to update
     */
    public void updateUser(UsersEntity user) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = this.sessionFactory.openSession();
            transaction = session.beginTransaction();
            session.update(user);
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
     * Deletes user like UsersEntity supplied from database
     * @param user UserEntity to delete
     */
    public void deleteUser(UsersEntity user) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = this.sessionFactory.openSession();
            transaction = session.beginTransaction();
            session.remove(user);
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
