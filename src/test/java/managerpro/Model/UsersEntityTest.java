package managerpro.Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UsersEntityTest {
    private static UsersEntity usersEntity;

    @BeforeEach
    public void alkutoimet() {
        usersEntity = new UsersEntity("Elina", "123456", "1");
        usersEntity.setId(123);
    }
    @Test
     void UsersEntity() {
        assertEquals("Elina", usersEntity.getUsername(), "Nimi väärin ");
        assertEquals("123456", usersEntity.getPassword(), "Salasana väärin ");
        assertEquals("1", usersEntity.getUserlevel(), "Kättäjän taso väärin ");
    }

    @Test
    void getId() {
        assertEquals(123, usersEntity.getId(), "ID väärin ");
    }

    @Test
    void setId() {
        usersEntity.setId(2);
        assertEquals(2, usersEntity.getId(), "ID väärin ");
    }

    @Test
    void getUsername() {
        assertEquals("Elina", usersEntity.getUsername(), "Nimi väärin ");
    }

    @Test
    void setUsername() {
        usersEntity.setUsername("Elina");
        assertEquals("Elina", usersEntity.getUsername(), "Nimi väärin ");
    }

    @Test
    void getPassword() {
        assertEquals("123456", usersEntity.getPassword(), "Salasana väärin ");
    }

    @Test
    void setPassword() {
        usersEntity.setUsername("123456");
        assertEquals("123456", usersEntity.getPassword(), "Salasana väärin ");
    }

    @Test
    void getUserlevel() {
        assertEquals("1", usersEntity.getUserlevel(), "Käyttäjän taso väärin ");
    }

    @Test
    void setUserlevel() {
        usersEntity.setUserlevel("1");
        assertEquals("1", usersEntity.getUserlevel(), "Käyttäjän taso väärin ");
    }

    @Test
    void testEquals() {
        UsersEntity user1 = new UsersEntity("asd","asd","asd");
        UsersEntity user2 = new UsersEntity("asd","asd","asd");
        UsersEntity user3 = new UsersEntity("dfg","asd","asd");

        assertEquals(true, user1.equals(user2));
        user2.setId(234);
        assertEquals(false, user1.equals(user2));

        assertEquals(false, user1.equals(user3));
    }

    @Test
    void testHashCode() {
        UsersEntity user1 = new UsersEntity("asd","asd","asd");
        UsersEntity user2 = new UsersEntity("asd","asd","asd");
        UsersEntity user3 = new UsersEntity("dfg","asd","asd");

        assertEquals(true, user1.hashCode() == user2.hashCode());
        user2.setId(234);
        assertEquals(false, user1.hashCode() == user2.hashCode());

        assertEquals(false, user1.hashCode() == user3.hashCode());
    }

    @Test
    void testToString() {
        String str = usersEntity.toString();
        assertEquals(123, usersEntity.getId(), "Syötä Id");
        assertEquals(true, str.contains(usersEntity.getUsername()),"Nimi väärin : pitäisi olla " + usersEntity.getUsername());
        assertEquals(true, str.contains(usersEntity.getPassword()));
        assertEquals(true, str.contains(usersEntity.getUserlevel()),"Syötä käyttäjän taso");

    }
}