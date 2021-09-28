package managerpro.View;

import managerpro.Controller.Controller;
import managerpro.Controller.LoginController;
import managerpro.Model.*;
import managerpro.Utils.LanguageUtils;
import managerpro.Utils.PasswordUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import java.io.IOException;

@ExtendWith(ApplicationExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class KitchenTest {
    //The usual setup..
    private static TablesEntity table;
    private static PortionsEntity portion;
    private static OrdersEntity order;
    private static OrderedPortionsEntity orderedPortion;
    private static OrderedPortionsEntity orderedPortion2;

    static LoginController loginController;
    @BeforeAll
    static void setUp(){
        //Set the test to run headless
        System.setProperty("testfx.robot", "glass");
        System.setProperty("glass.platform", "Monocle");
        System.setProperty("monocle.platform", "Headless");
        System.setProperty("testfx.headless", "true");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text","native");
        System.setProperty("java.awt.headless", "true");
        //Use login controller to get a session factory
        loginController = new LoginController();
        //Create a test user
        UsersEntity user = new UsersEntity("testUser", PasswordUtils.hashPassword("testpassword"),"1");
        UsersDao usersDao = new UsersDao(LoginController.getSessionFactory(),LoginController.createValidatorFactory());
        //Test user to DB
        usersDao.createUser(user);
    }

    private static Stage primaryStage=null;
    private static Stage mainStage;
    @Start
    private void start(Stage primaryStage) {
        if(KitchenTest.primaryStage == null) {
            KitchenTest.primaryStage = primaryStage;
            try {
                Parent root = FXMLLoader.load(MainApp.class.getResource("/loginUI.fxml"), LanguageUtils.getResourceBundle());
                primaryStage.setTitle("ManagerPro");
                primaryStage.setScene(new Scene(root, 700, 500));
                primaryStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Test
    @Order(1)
    public void test_title_bar (FxRobot robot) {
        //Check that the app started
        Assertions.assertThat(primaryStage.getTitle()).hasToString("ManagerPro");
    }
    @Test
    @Order(2)
    public void test_login(FxRobot robot) {
        //Login with test user
        robot.clickOn("#userName");
        robot.write("testUser");
        robot.clickOn("#passWord");
        robot.write("testpassword");
        robot.clickOn("#loginButton");
    }
    @Test
    @Order(3)
    public void test_open_kitchenTab(FxRobot robot){
        //Change to kitchenTab
        robot.clickOn("#kitchenTab");
        //Assert that the orders FlowPane is empty
        Assertions.assertThat(robot.lookup("#ordersFlowPane").queryAs(FlowPane.class).getChildren().size()).isEqualTo(0);

        //Create a test table
        table = new TablesEntity(4);
        Controller.model.addTable(table);

        //...test order
        order = new OrdersEntity("waiting",table.getId());
        Controller.model.addOrder(order);
        //..test portion
        portion  = new PortionsEntity("ASDF",5.5);
        Controller.model.addPortion(portion);
        //..test orderedPortion
        orderedPortion = new OrderedPortionsEntity(order.getId(),portion.getId(),"Ei juustoa",0);
        orderedPortion2 = new OrderedPortionsEntity(order.getId(),portion.getId(),"Ei juustoa2",0);
        Controller.model.addOrderedPortion(orderedPortion);
        Controller.model.addOrderedPortion(orderedPortion2);
        //Sleep for 500ms
        try {
            Thread.sleep(500);
        }catch (Exception e){e.printStackTrace();}

    }
    @Test
    @Order(4)
    public void test_select_order(FxRobot robot){

        //assert that flowpane has one child
        Assertions.assertThat(robot.lookup("#ordersFlowPane").queryAs(FlowPane.class).getChildren().size()).isEqualTo(1);
        //assert that orderedPortions flowPane is empty
        Assertions.assertThat(robot.lookup("#orderedPortionsFlowPane").queryAs(FlowPane.class).getChildren().size()).isEqualTo(0);
        //Click the order
        robot.clickOn("#kth"+order.getId());
        //assert that orderedPortions flowPane now has two children
        Assertions.assertThat(robot.lookup("#orderedPortionsFlowPane").queryAs(FlowPane.class).getChildren().size()).isEqualTo(2);
    }
    @Test
    @Order(5)
    public void test_select_op(FxRobot robot){

        //assert that orderedPortions flowPane now has two children
        Assertions.assertThat(robot.lookup("#orderedPortionsFlowPane").queryAs(FlowPane.class).getChildren().size()).isEqualTo(2);
        //assert that singlePortionVBox VBox is empty
        Assertions.assertThat(robot.lookup("#singlePortionVBox").queryAs(VBox.class).getChildren().size()).isEqualTo(0);
        //Click the orderedPortion
        robot.clickOn("#opKth"+orderedPortion.getJunctionId());
        //Assert that the VBox now has a child
        Assertions.assertThat(robot.lookup("#singlePortionVBox").queryAs(VBox.class).getChildren().size()).isEqualTo(1);
        //Assert that the VBox child has the OP details in it
        Assertions.assertThat(robot.lookup("#opText").queryAs(Text.class)).hasText("Portion Name: ASDF\nPortion Details: Ei juustoa");
    }
    @Test
    @Order(6)
    public void test_set_op_ready(FxRobot robot){
        System.out.println(robot.lookup("#opKth"+orderedPortion.getJunctionId()).queryAs(Button.class).getStyleClass());
        //assert that orderedPortion has style class "kitchenNotReady"
        Assertions.assertThat(robot.lookup("#opKth"+orderedPortion.getJunctionId()).queryAs(Button.class).getStyleClass()).contains("kitchenNotReady");
        //Click the readyButton
        robot.clickOn("#portionReadyBtn");
        //assert that orderedPortion has style class "kitchenReady"
        Assertions.assertThat(robot.lookup("#opKth"+orderedPortion.getJunctionId()).queryAs(Button.class).getStyleClass()).contains("kitchenReady");
    }
    @Test
    @Order(7)
    public void test_all_op_ready_flowpane_clear(FxRobot robot){
        //Click second orderedPortion
        robot.clickOn("#opKth"+orderedPortion2.getJunctionId());
        //Click the readyButton
        robot.clickOn("#portionReadyBtn");
        //Assert ordersFlowPane empty
        Assertions.assertThat(robot.lookup("#ordersFlowPane").queryAs(FlowPane.class).getChildren().size()).isEqualTo(0);
        //Assert orderedPortions flowPanee mpty
        Assertions.assertThat(robot.lookup("#orderedPortionsFlowPane").queryAs(FlowPane.class).getChildren().size()).isEqualTo(0);
    }
//The usual cleanup
    @AfterAll
    static void cleanUp(){
        Session session = null;
        Transaction transaction = null;
        try {
            session = LoginController.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            Query queryDel1 = session.createQuery("DELETE from OrdersEntity ");
            Query queryDel2 = session.createQuery("DELETE from PortionsEntity ");
            Query queryDel3 = session.createQuery("DELETE from OrderedPortionsEntity ");
            Query queryDel4 = session.createQuery("DELETE from TablesEntity ");
            Query queryDel5 = session.createQuery("DELETE from UsersEntity ");

            //OrderedPortions first!
            queryDel3.executeUpdate();
            //Then the rest...
            queryDel1.executeUpdate();
            queryDel2.executeUpdate();
            queryDel4.executeUpdate();
            queryDel5.executeUpdate();

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
