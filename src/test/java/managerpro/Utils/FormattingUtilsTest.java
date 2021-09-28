
package managerpro.Utils;
import org.junit.jupiter.api.*;
import java.sql.Date;
import java.time.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testataan formatointityökaluja")

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FormattingUtilsTest {
    @Test
    @Order(1)
    void formatDateWithDateFormatter(){
        LanguageUtils.setLanguage("");
        Date date = new Date(1000);
        assertEquals("Jan 1, 1970",date.toLocalDate().format(FormattingUtils.getDateFormatter()));
        LanguageUtils.setLanguage("Suomi");
        assertEquals("1.1.1970",date.toLocalDate().format(FormattingUtils.getDateFormatter()));

    }
    @Test
    @Order(2)
    void formatTimeWithDateFormatter(){
        LanguageUtils.setLanguage("English");
        LocalTime time = LocalTime.of(13,00);
        assertEquals("1:00 PM",time.format(FormattingUtils.getTimeFormatter()));
        LanguageUtils.setLanguage("Suomi");
        assertEquals("13.00",time.format(FormattingUtils.getTimeFormatter()));

    }
    @Test
    @Order(3)
    void phoneNumberFormatterBR(){
        assertEquals("+55 11 98456-5666", FormattingUtils.formatPhoneNumber("+5511984565666"));
    }
    @Test
    @Order(4)
    void phoneNumberFormatterFI(){
        assertEquals("+358 44 0608260", FormattingUtils.formatPhoneNumber("+358 440 608 260"));
    }
    @Test
    @Order(5)
    void phoneNumberFormatterUS(){
        assertEquals("+1 202-555-0115", FormattingUtils.formatPhoneNumber("+1 2025550115"));
    }
    @Test
    @Order(6)
    void phoneNumberFormatterBadPhoneNumber(){
        assertEquals("0321532984568465889", FormattingUtils.formatPhoneNumber("0321532984568465889"));
    }
}