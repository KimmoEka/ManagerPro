package managerpro.Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.sql.Time;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReservationsEntityTest {
    private static ReservationsEntity reservationsEntity;
    private static Date date = new Date(System.currentTimeMillis());
    //private static TablesDao tablesDao;

    @BeforeEach
    public void alkutoimet() {
        reservationsEntity = new ReservationsEntity("Elina", "46222333", date, 1, new Time(10), new Time(15));
        reservationsEntity.setId(12);
        date = new Date(date.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
    }

    @Test
    void ReservationsEntity() {
        assertEquals("Elina", reservationsEntity.getClientName(), "Nimi väärin ");
        assertEquals("46222333", reservationsEntity.getClientPhone(), "Puhelin numero väärin ");
        assertEquals(date, reservationsEntity.getReservationDate(), "Tilausten päivämäärä väärin ");
        assertEquals(new Time(10), reservationsEntity.getStartTime(), "Tilausten alkuaika väärin ");
        assertEquals(new Time(15), reservationsEntity.getEndTime(), "Tilausten loppuaika väärin ");
        assertEquals(1, reservationsEntity.getTableNumber(), "Pöydän numero väärin ");
    }


    @Test
    void testGetId() {
        assertEquals(12, reservationsEntity.getId(), "ID väärin ");
    }

    @Test
    void testSetId() {
        reservationsEntity.setId(2);
        assertEquals(2, reservationsEntity.getId(), "ID väärin ");
    }

    @Test
    void testGetClientName() {
        assertEquals("Elina", reservationsEntity.getClientName(), "Nimi väärin ");
    }

    @Test
    void testSetClientName() {
        reservationsEntity.setClientName("Elina");
        assertEquals("Elina", reservationsEntity.getClientName(), "Nimi väärin ");
    }

    @Test
    void testGetClientPhone() {
        assertEquals("46222333", reservationsEntity.getClientPhone(), "Puhelin numero väärin ");
    }

    @Test
    void testSetClientPhone() {
        reservationsEntity.setClientPhone("46222333");
        assertEquals("46222333", reservationsEntity.getClientPhone(), "Puhelin numero väärin ");
    }

    @Test
    void testGetReservationDate() {
        assertEquals(date, reservationsEntity.getReservationDate(), "Tilausten aika väärin ");
    }

    @Test
    void testSetReservationDate() {
        Date temp = new Date(System.currentTimeMillis());
        Date newDate =new Date(temp.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
            reservationsEntity.setReservationDate(newDate);
            assertEquals(newDate, reservationsEntity.getReservationDate(), "Date väärin ");
    }

    @Test
    void testGetTableNumber() {
        assertEquals(1 , reservationsEntity.getTableNumber(), "Pöydän numero väärin ");
    }

    @Test
    void testSetTableNumber() {
        reservationsEntity.setTableNumber(2);
        assertEquals(2, reservationsEntity.getTableNumber(), "Pöydän numero väärin ");
    }

    @Test
    void testEquals() {

        ReservationsEntity reservation1 = new ReservationsEntity("Elina", "46222333", date, 1, new Time(10), new Time(15));
        ReservationsEntity reservation2 = new ReservationsEntity("Elina", "46222333", date, 1, new Time(10), new Time(15));
        ReservationsEntity reservation3 = new ReservationsEntity("Amal", "46222333", date, 1, new Time(10), new Time(15));

        assertEquals(true, reservation1.equals(reservation2));
        reservation2.setId(18);
        assertEquals(false, reservation1.equals(reservation2));

        assertEquals(false, reservation1.equals(reservation3));
    }

    @Test
    void testHashCode() {
        ReservationsEntity reservation1 = new ReservationsEntity("Elina", "46222333", date, 1, new Time(10), new Time(15));
        ReservationsEntity reservation2 = new ReservationsEntity("Elina", "46222333", date, 1, new Time(10), new Time(15));
        ReservationsEntity reservation3 = new ReservationsEntity("Amal", "46222333", date, 1, new Time(10), new Time(15));

        assertEquals(true, reservation1.hashCode() == reservation1.hashCode());
        reservation2.setId(36);
        assertEquals(false, reservation1.hashCode() == reservation2.hashCode());

        assertEquals(false, reservation1.hashCode() == reservation3.hashCode());
    }

    @Test
    void testToString() {
        // String str = reservationsEntity.toString();
       // assertEquals( "ReservationsEntity{id=12, clientName='Elina', clientPhone=46222333, reservationDate=1970-01-01 02:00:01.0, tableNumber=1}",str, "Nimi väärin : pitäisi olla " + reservationsEntity.getClientName());
    }
}
