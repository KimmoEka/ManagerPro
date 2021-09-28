package managerpro.Model;

import org.junit.jupiter.api.*;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Test the setters, getters, hashcode and toString methods of TablesEntity")
class OrdersEntityTest {

    private static OrdersEntity order;
    private static TablesEntity table;
    private static Random rand;

    @BeforeAll
    static void firstSetUp() {
        rand = new Random();
        table = new TablesEntity();
        table.setId(1);
    }
    @BeforeEach
    void setUp(){
        order = new OrdersEntity();
    }

    @Test
    @Order(1)
    @DisplayName("Setting and getting ID ")
    void setAndGetId() {
        int i = rand.nextInt();
        order.setId(i);
        assertEquals(i,order.getId());
    }
    @Test
    @Order(2)
    @DisplayName("Setting and getting Order details")
    void setAndGetOrderDetails() {
        order.setOrderDetails("Test Details");
        assertEquals("Test Details", order.getOrderDetails());
    }
    @Test
    @Order(3)
    @DisplayName("Setting and getting Table Number")
    void setAndGetSeats() {
        order.setTableNumber(table.getId());
        assertEquals(table.getId(),order.getTableNumber());
    }

    @Test
    @Order(4)
    @DisplayName("Setting and Getting TablesByTableNumber")
    void setAndGetReservationId() {
        order.setTablesByTableNumber(table);
        assertEquals(table,order.getTablesByTableNumber());
    }

    @Test
    @Order(5)
    @DisplayName("Test equal method with equal table")
    void testEqualsWithEqual() {
        OrdersEntity equalOrder = order;
        assertTrue(order.equals(equalOrder));

    }
    @Test
    @Order(6)
    @DisplayName("Testing equal method table with non equal table")
    void testEqualsWithNotEqual() {
        OrdersEntity newOrder = new OrdersEntity();
        newOrder.setId(61);
        assertFalse(order.equals(newOrder));
    }
    @Test
    @Order(7)
    @DisplayName("Testing equal method with wrong object type")
    void testEqualsWithDifferentObject() {
        assertFalse(order.equals(new ReservationsEntity()));
    }
    @Test
    @Order(8)
    @DisplayName("Test object hashing with equal object")
    void testHashcode(){
        OrdersEntity equalOrder = order;
        assertEquals(equalOrder.hashCode(),order.hashCode());
    }
    @Test
    @Order(9)
    @DisplayName("Test object hashing with different object")
    void testHashCodeDifferentObject() {
        order.setId(6);
        assertNotEquals(new OrdersEntity().hashCode(), order.hashCode());
    }
    @Test
    @Order(10)
    void testToString() {
        order.setId(1);
        order.setOrderDetails("Makkaraperunat");
        order.setTableNumber(1);
        assertEquals("ID: 1, Order Details: Makkaraperunat, Table Number: 1", order.toString());
    }
    @Test
    @Order(11)
    void testConstructors()
    {
        OrdersEntity asdf = new OrdersEntity("Ei kinuskia", 1);
        assertEquals("Ei kinuskia", asdf.getOrderDetails());
        assertEquals(1,asdf.getTableNumber());
        asdf = new OrdersEntity("Ei juustoa", new TablesEntity());
        assertEquals("Ei juustoa", asdf.getOrderDetails());
        assertEquals(new TablesEntity(), asdf.getTablesByTableNumber());

    }
}