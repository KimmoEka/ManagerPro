package managerpro.Model;

import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Test the setters, getters, hashcode and toString methods of TablesEntity")
class TablesEntityTest {

    private static TablesEntity table;
    private static ArrayList<OrdersEntity> orders;
    private static ArrayList<ReservationsEntity> reservations;
    private static Random rand;

    @BeforeAll
    static void firstSetUp() {
        rand = new Random();
        table = new TablesEntity();
        reservations= new ArrayList<>();
        reservations.add(new ReservationsEntity());
        reservations.add(new ReservationsEntity());
        orders= new ArrayList<>();
        orders.add(new OrdersEntity());
        orders.add(new OrdersEntity());
    }
    @BeforeEach
    void setUp(){
        table = new TablesEntity();
    }

    @Test
    @Order(1)
    @DisplayName("Setting and getting ID ")
    void setAndGetId() {
        int i = rand.nextInt();
        table.setId(i);
        assertEquals(i,table.getId());
    }
    @Test
    @Order(5)
    @DisplayName("Setting and getting amount of seats")
    void setAndGetSeats() {
        int i = rand.nextInt(20);
        table.setSeats(i);
        assertEquals(i,table.getSeats());
    }
    @Test
    @Order(6)
    @DisplayName("Setting negative amount of chairs")
    void setAndGetNegativeSeats() {
        table.setSeats(-50);
        assertEquals(0,table.getSeats());
    }

    @Test
    @Order(7)
    @DisplayName("Setting and getting reservationID")
    void setAndGetReservationId() {
        int i = rand.nextInt(20);
        table.setReservationId(i);
        assertEquals(i,table.getReservationId());
    }

    @Test
    @Order(8)
    @DisplayName("Test equal method with equal table")
    void testEqualsWithEqual() {
        TablesEntity equalTable = table;
        assertTrue(table.equals(equalTable));

    }
    @Test
    @Order(9)
    @DisplayName("Testing equal method table with non equal table")
    void testEqualsWithNotEqual() {
        table.setId(5);
        assertFalse(table.equals(new TablesEntity()));
    }
    @Test
    @Order(10)
    @DisplayName("Testing equal method with wrong object type")
    void testEqualsWithDifferentObject() {
        assertFalse(table.equals(new ReservationsEntity()));
    }
    @Test
    @Order(11)
    @DisplayName("Test object hashing with equal object")
    void testHashcode(){
        TablesEntity equalTable = table;
        assertEquals(equalTable.hashCode(),table.hashCode());
    }
    @Test
    @Order(12)
    @DisplayName("Test object hashing with different object")
    void testHashCodeDifferentObject() {
        table.setId(6);
        assertNotEquals(new TablesEntity().hashCode(), table.hashCode());
    }
    @Test
    @Order(13)
    @DisplayName("Setting and getting ordersById")
    void setAndGetOrdersById() {
        table.setOrdersById(orders);
        assertEquals(orders,table.getOrdersById());
    }
    @Test
    @Order(14)
    void setAndGetReservationsById() {
        table.setReservationsById(reservations);
        assertEquals(reservations, table.getReservationsById());
    }
    @Test
    @Order(15)
    void setAndGetReservationsByReservationId() {
        ReservationsEntity reservation = new ReservationsEntity();
        table.setReservationsByReservationId(reservation);
        assertEquals(reservation, table.getReservationsByReservationId());
    }
    @Test
    @Order(16)
    void testToString() {
        table.setId(1);
        table.setSeats(4);
        assertEquals("ID: 1, Seats: 4", table.toString());
    }
    @Test
    @Order(17)
    void testConstructors(){
        TablesEntity asdf = new TablesEntity(4);
        assertEquals(4,asdf.getSeats());
    }
}