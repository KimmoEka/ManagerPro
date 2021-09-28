package managerpro.Controller;

import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import managerpro.Model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

class PortionsControllerTest extends ApplicationTest {
    private PortionsController portionsController;
    private PortionsDao portionsDao;

    private TextField name;
    private TextField price;
    private TableView<PortionsEntity> table;
    public final double DELTA = 0.0001;

    @BeforeAll
    public static void initJFX() {
        System.setProperty("testfx.robot", "glass");
        System.setProperty("glass.platform", "Monocle");
        System.setProperty("monocle.platform", "Headless");
        System.setProperty("testfx.headless", "true");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text","native");
        System.setProperty("java.awt.headless", "true");
    }

    @BeforeEach
    public void setup () {

        name = new TextField();
        price = new TextField();
        table = new TableView<>();

        portionsController = new PortionsController();
        portionsDao = mock(PortionsDao.class);
        List<PortionsEntity> portions = new ArrayList<PortionsEntity>();
        PortionsEntity portion1 = new PortionsEntity("Risotto", 22.0);
        portion1.setId(1);
        portions.add(portion1);
        portions.add(new PortionsEntity("Chicken",15));
        when(portionsDao.getAllPortions()).thenReturn(portions);
        when(portionsDao.getPortion(portion1.getId())).thenReturn(portion1);
        portionsController.setPortionsDao(portionsDao);

        portionsController.setName(name);
        portionsController.setPrice(price);
        portionsController.setTable(table);

        portionsController.initialize();
    }

    @Test
    public void TestThatTableHasTwoColumns() {
        assertEquals(table.getColumns().size(), 2);
    }

    @Test
    public void TestRowSelection() {

        PortionsEntity portion = new PortionsEntity();
        portion.setName("Chicken");
        portion.setPrice(24.0);

        PortionsEntity portion2 = new PortionsEntity();
        portion2.setName("Soup");
        portion2.setPrice(22.0);

        table.getItems().clear();
        table.getItems().add(portion);
        table.getItems().add(portion2);

        table.getSelectionModel().select(0);
        assertEquals(name.getText(), portion.getName());
        assertEquals(price.getText(), String.valueOf(portion.getPrice()));
        assertEquals(portionsController.getSelectedPortion(), portion);

        table.getSelectionModel().select(1);
        assertEquals(name.getText(), portion2.getName());
        assertEquals(price.getText(), String.valueOf(portion2.getPrice()));
        assertEquals(portionsController.getSelectedPortion(), portion2);
    }

    @Test void TestRefreshTable() {
        table.getItems().clear();
        portionsController.refreshTable();

        // After refresh should have the mocked data from UsersDao.getAllUsers()
        assertEquals(table.getItems().get(0).getName(), "Risotto");
        assertEquals(table.getItems().get(1).getPrice(), 15.0, DELTA);
    }

    @Test void TestPortionCreationSuccessful() {
        PortionsEntity portion = new PortionsEntity("Risotto", 55);
        name.setText(portion.getName());
        price.setText(String.valueOf(portion.getPrice()));

        table.getItems().clear();

        portionsController.createPortion();

        // Test that the portionsDao createPortion was called
        verify(portionsDao, times(1)).createPortion(argThat((createdPortion) -> {
            boolean nameMatch = createdPortion.getName().equals(portion.getName());

            return nameMatch;
        }));

        // Test that the form was reset
        assertEquals(name.getText(), "");
        assertEquals(price.getText(), "");

        // Test that table was refreshed
        assertEquals(2, table.getItems().size());
    }

    @Test void TestPortionUpdateSuccessful() {
        PortionsEntity portion = new PortionsEntity("Risotto",55);
        portion.setId(12);

        name.setText("Risotto");
        price.setText("55");
        portionsController.setSelectedPortion(portion);

        table.getItems().clear();

        portionsController.updatePortion();

        // Test that the portionsDao updatePortion was called
        verify(portionsDao, times(1)).updatePortion(argThat((updatedPortion) -> {
            // Name should match the new one
            boolean nameMatch = updatedPortion.getName().equals("Risotto");

            return nameMatch;
        }));

        // Test that the form was reset
        assertEquals(name.getText(), "");
        assertEquals(price.getText(), "");

        // Test that table was refreshed
        assertEquals(2, table.getItems().size());
    }

    @Test void TestDeletePortion() {
        PortionsEntity portion = new PortionsEntity("Risotto",55);
        portion.setId(12);

        table.getItems().clear();
        table.getItems().add(portion);

        portionsController.setSelectedPortion(portion);
        portionsController.deletePortion();

        // Test that the portionsDao deletePortion was called
        verify(portionsDao, times(1)).deletePortion(argThat((deletedPortion) -> {
            // Confirm that the selected portion is deleted
            return deletedPortion == portion;
        }));

        // Test that the form was reset
        assertEquals(name.getText(), "");
        assertEquals(price.getText(), "");

        // Test that table was refreshed
        assertEquals(2, table.getItems().size());
    }
}