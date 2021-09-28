package managerpro.Model;

import managerpro.Controller.LoginController;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.validation.ValidatorFactory;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testataan UsersDao olion metodit")
class UsersDaoTest {
    private static UsersDao usersDao;
    private static SessionFactory sessionFactory;
    private static ValidatorFactory validatorFactory;

    @BeforeAll
    static void setUp() throws Exception {
        Configuration configuration = new Configuration();

        // Change configuration to use testdb
        configuration.setProperty("hibernate.connection.url", "jdbc:mariadb://10.114.32.44:3306/testdb");
        configuration.configure();

        sessionFactory = configuration.buildSessionFactory();
        validatorFactory = LoginController.createValidatorFactory();
        usersDao = new UsersDao(sessionFactory, validatorFactory);

        emptyTable();
    }

    static void emptyTable () {
        // Empty the Users table before testing
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            Query query = session.createQuery("DELETE from UsersEntity");
            query.executeUpdate();
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
    @DisplayName("Testataan kaikkien käyttäjien haku")
    void getAllUsers() {
        // Test that returns empty array list
        assertEquals(0, usersDao.getAllUsers().size());

        // Test that returns the same number as created users
        UsersEntity firstUser = new UsersEntity("amal","12345","1");
        usersDao.createUser(firstUser);
        usersDao.createUser(new UsersEntity("juho","34567","1"));
        usersDao.createUser(new UsersEntity("kimmo","56788","1"));
        usersDao.createUser(new UsersEntity("petteri","78894","1"));

        List<UsersEntity> users = usersDao.getAllUsers();
        assertEquals(4, users.size());
        assertEquals(firstUser, users.get(0));
    }

    @Test
    @DisplayName("Testataan käyttäjän luonti")
    void createUser() {

        // Test user creation
        UsersEntity user = new UsersEntity("amal","12345","1");
        usersDao.createUser(user);
        assertNotEquals(0, user.getId());

        // Test that fetching the created user from database works
        UsersEntity userFromDb = usersDao.getUser(user.getId());
        assertEquals(user, userFromDb);
    }

    @Test
    @DisplayName("Testataan käyttäjän päivitys")
    void updateUser() {

        // Create user
        UsersEntity user = new UsersEntity("amal","12345","1");
        usersDao.createUser(user);

        UsersEntity userFromDb = usersDao.getUser(user.getId());

        assertEquals(user, userFromDb);

        // Update user
        user.setUsername("Pekka");
        user.setPassword("45678");
        user.setUserlevel("2");

        usersDao.updateUser(user);

        UsersEntity updatedUserFromDb = usersDao.getUser(user.getId());

        assertEquals(user, updatedUserFromDb);
    }

    @Test
    @DisplayName("Testataan käyttäjän poisto tietokannasta")
    void deleteUser() {

        // Create a user in DB
        UsersEntity user = new UsersEntity("Antero", "123455", "1");
        usersDao.createUser(user);

        // Confirm that the user is in DB
        UsersEntity userFromDb = usersDao.getUser(user.getId());
        assertEquals(user, userFromDb);

        // Delete user and confirm that it's not in the DB
        usersDao.deleteUser(user);

        userFromDb = usersDao.getUser(user.getId());
        assertEquals(null, userFromDb);

    }
    @Test
    @DisplayName("Hae käyttäjä käyttäjänimen perusteella")
    void getUserByUsername(){
        UsersEntity user = new UsersEntity("Antero", "12345", "1");
        usersDao.createUser(user);
        UsersEntity user2 = new UsersEntity("Taavi", "123245", "1");
        usersDao.createUser(user2);
        UsersEntity newUser = usersDao.getUserByUsername("Taavi");
        usersDao.deleteUser(user);
        usersDao.deleteUser(user2);
        assertEquals(user2,newUser);
    }
    @Test
    @DisplayName("Hae käyttäjää  olemattoman käyttäjänimen perusteella")
    void getUserByUsernameNoUser(){
        UsersEntity newUser = usersDao.getUserByUsername("Antero");
        assertNull(newUser);
    }

    @Test
    @DisplayName("Testaa validaattoria")
    void testValidator()
    {
        UsersEntity user = new UsersEntity(null, "123", "1");
        try {
            usersDao.createUser(user);
        } catch (IllegalArgumentException e) {

            System.out.println(e.getMessage());

            return;
        }

        assertEquals(true, false, "Should have thrown an IllegalArgumentException");
    }
}