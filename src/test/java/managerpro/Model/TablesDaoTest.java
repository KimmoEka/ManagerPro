package managerpro.Model;
import org.hibernate.*;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
@DisplayName("Testataan Tables Data access olion metodit")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TablesDaoTest {
    private static TablesDao tablesDao;
    private static  ArrayList<TablesEntity> tables;
    private static SessionFactory sesFac;
    private static Random rand;

    @BeforeAll
    static void setUp() throws Exception{
        Configuration configuration = new Configuration();
        configuration.configure();
        sesFac = configuration.buildSessionFactory();
        tablesDao = new TablesDao(sesFac);
        tables = new ArrayList<>();
        rand = new Random();
        for (int i =0;i<4;i++)
        {
            TablesEntity table = new TablesEntity();
            table.setSeats(4);
            tables.add(table);
        }
    }
    @Test
    @DisplayName("Testataan pöydän luonti ja vienti tietokantaan")
    @Order(1)
    void createTable() {
        //Varmista että tietokanta on tyhjä
        Session session = sesFac.openSession();
        Transaction transaction = session.beginTransaction();
        @SuppressWarnings("unchecked")
        List<TablesEntity> emptyTables = session.createQuery("from TablesEntity ").getResultList();
        transaction.commit();
        session.close();
        assertEquals(0, emptyTables.size());

        //Luo neljä pöytää
        for (TablesEntity table : tables) {
            tablesDao.createTable(table);
        }

        //Tarkista että neljä pöytää meni sinne
        session = sesFac.openSession();
        transaction = session.beginTransaction();
        @SuppressWarnings("unchecked")
        List<TablesEntity> tablesList = session.createQuery("from TablesEntity ").getResultList();
        tables = new ArrayList<>(tablesList);
        assertEquals(4,tables.size());
        transaction.commit();
        session.close();
    }


    @Test
    @Order(2)
    @DisplayName("Haetaan pöytä ID:llä")
    void getTable() {

        //Pöydät haettu tietokannasta, otetaan niistä ID
        //Valitaan haetuista pöydistä yksi ja käytetään sitä
        int i = rand.nextInt(tables.size()-1);
        TablesEntity testTable = tablesDao.getTable(tables.get(i).getId());
        assertEquals(testTable, tables.get(i));

    }
    @Test
    @Order(3)
    @DisplayName("Haetaan pöytää olemattomalla ID:llä")
    void getNonTable() {
        assertThrows(HibernateException.class, ()->tablesDao.getTable(-1));
    }
    @Test
    @Order(4)
    @DisplayName("Testataan kaikkien tietokannan pöytien haku")
    void getAllTables() {
        ArrayList<TablesEntity> lista  = tablesDao.getAllTables();
        assertEquals(tables,lista);
    }
    @Test
    @Order(5)
    @DisplayName("Testataan pöydän päivitys, muutetaan tuolien määrä")
    void updateTable() {

        TablesEntity table = tables.get(0);
        table.setSeats(2);
        tablesDao.updateTable(table);
        Session session = sesFac.openSession();
        Transaction transaction = session.beginTransaction();
        TablesEntity updatedTable = new TablesEntity();
        session.load(updatedTable, table.getId());
        transaction.commit();
        session.close();
        assertEquals(table.getSeats(), updatedTable.getSeats());
    }

    @Test
    @Order(6)
    @DisplayName("Testataan pöydän poisto tietokannasta")
    void deleteTable() {
        try{
            tablesDao.deleteTable(tables.get(tables.size()-1));
            tables.remove(tables.get(tables.size()-1));
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    @AfterAll
    static void rollback(){
        Session session = sesFac.openSession();
        Transaction transaction = session.beginTransaction();
        @SuppressWarnings("unchecked")
        List<TablesEntity> results = session.createQuery("from TablesEntity ").getResultList();
        tables = new ArrayList<>(results);

        for(TablesEntity table : tables)
        {
            session.delete(table);
        }
        transaction.commit();
        session.close();
    }

}