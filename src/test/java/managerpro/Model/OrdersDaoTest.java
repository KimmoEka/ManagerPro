package managerpro.Model;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
@DisplayName("OrdersDao luokan CRUD metodien testaus")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrdersDaoTest {
    private static OrdersDao ordersDao;
    private static ArrayList<TablesEntity> tables;
    private static ArrayList<OrdersEntity> orders;
    private static SessionFactory sesFac;
    private static Random rand;
    @BeforeAll
    static void testSetUp()
    {
        Configuration configuration = new Configuration();
        configuration.configure();
        sesFac = configuration.buildSessionFactory();
        ordersDao = new OrdersDao(sesFac);
        orders = new ArrayList<>();
        rand = new Random();

        TablesEntity table1 = new TablesEntity();
        TablesEntity table2 = new TablesEntity();

        table1.setSeats(4);

        table2.setSeats(4);

        Session session = sesFac.openSession();
        Transaction transaction = session.beginTransaction();
        session.saveOrUpdate(table1);
        session.saveOrUpdate(table2);
        @SuppressWarnings("unchecked")
        List<TablesEntity> results = session.createQuery("from TablesEntity ").getResultList();
        tables = new ArrayList<>(results);
        transaction.commit();
        session.close();

        for (int i =0;i<4;i++)
        {
            OrdersEntity order = new OrdersEntity();
            order.setOrderDetails("Test String");
            if(i%2==0)
                {
                    order.setTableNumber(tables.get(0).getId());
                }
            else
                {
                    order.setTableNumber(tables.get(1).getId());
                }
            orders.add(order);
        }


    }
    @Test
    @DisplayName("Testataan tilauksen luonti ja vienti tietokantaan")
    @Order(1)
    void createOrder() {
        //Varmista että tietokanta on tyhjä
        Session session = sesFac.openSession();
        Transaction transaction = session.beginTransaction();
        @SuppressWarnings("unchecked")
        List<OrdersEntity> emptyOrders = session.createQuery("from OrdersEntity ").getResultList();
        transaction.commit();
        session.close();
        assertEquals(0, emptyOrders.size());

        //Luo 4 tilausta tietokantaan
        for (OrdersEntity order : orders) {
            ordersDao.createOrder(order);
        }

        //Tarkista, että neljä tilausta on mennyt sinne, joista kaksi pöytään X ja kaksi pöytään Y
        session = sesFac.openSession();
        transaction = session.beginTransaction();
        @SuppressWarnings("unchecked")
        List<OrdersEntity> ordersList = session.createQuery("from OrdersEntity ").getResultList();
        orders = new ArrayList<>(ordersList);
        assertEquals(4,orders.size());
        int i=0;
        int j=0;
        for (OrdersEntity order : orders)
        {
            if(order.getTableNumber() == tables.get(0).getId())
                i++;
            else
                j++;
        }
        assertEquals(2,i);
        assertEquals(2,j);

    }
    @Test
    @Order(2)
    @DisplayName("Luodaan uusi tilaus, jonka jäsenmuuttujia ei ole alustettu, odotetaan IllegalStateException jos tablenumber ei asetettu")
    void createEmptyOrder() {
        assertThrows(IllegalStateException.class,() ->ordersDao.createOrder(new OrdersEntity()));
    }
    @Test
    @Order(3)
    @DisplayName("Haetaan tilaus ID:llä")
    void getOrder() {
        //Tietokannasta tilaukset haettu jo kerran, poimitaan sieltä ID
        //Hae tietokannasta tilaus sillä ID:llä
        int i =  rand.nextInt(orders.size());
        OrdersEntity testOrder = ordersDao.getOrder(orders.get(i).getId());

        assertEquals(testOrder, orders.get(i));
    }
    @Test
    @Order(4)
    @DisplayName("Haetaan tilausta olemattomalla ID:llä")
    void getNonOrder() {
        assertThrows(HibernateException.class, ()->ordersDao.getOrder(-1));
    }
    @Test
    @Order(5)
    @DisplayName("Testataan kaikkien tietokannan tilausten haku")
    void getAllOrders() {
        //Kaikki tilaukset haettu jo aikaisemmin orders jäsenmuuttujaan, käytetään sitä
        ArrayList<OrdersEntity> lista  = ordersDao.getAllOrders();
        assertEquals(orders,lista);
    }
    @Test
    @Order(6)
    @DisplayName("Testataan tilauksen päivitys, vaihdetaan tilauksen orderDetails")
    void updateOrder() {

        OrdersEntity order = orders.get(rand.nextInt(4));
        order.setOrderDetails("Changed Test String");
        ordersDao.updateOrder(order);
        Session session = sesFac.openSession();
        Transaction transaction = session.beginTransaction();
        OrdersEntity updatedOrder = new OrdersEntity();
        session.load(updatedOrder, order.getId());
        transaction.commit();
        session.close();
        assertEquals(order.getOrderDetails(), updatedOrder.getOrderDetails());

    }

    @Test
    @Order(7)
    @DisplayName("Testataan tilauksen poisto tietokannasta")
    void deleteOrder() {
        try{
            ordersDao.deleteOrder(orders.get(orders.size()-1));
            orders.remove(orders.get(orders.size()-1));
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
        for(OrdersEntity order : orders)
        {
            session.delete(order);
        }
        @SuppressWarnings("unchecked")
        List<TablesEntity> results = session.createQuery("from TablesEntity ").getResultList();
        ArrayList<TablesEntity> tables = new ArrayList<>(results);
        for (TablesEntity table : tables)
        session.delete(table);

        transaction.commit();
        session.close();
    }
}