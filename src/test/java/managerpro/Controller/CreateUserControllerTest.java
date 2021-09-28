package managerpro.Controller;

import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import managerpro.Model.UsersDao;
import managerpro.Model.UsersEntity;
import managerpro.Utils.LanguageUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CreateUserControllerTest extends ApplicationTest {

    private CreateUserController createUserController;
    private UsersDao usersDao;

    private TextField userName;
    private TextField userLevel;
    private TextField passWord;
    private TextField confirmPassWord;
    private Label statusText;
    private Label usernameError;
    private Label userLevelError;
    private Label passWordError;
    private TableView<UsersEntity> table;


    @BeforeAll
    public static void settingUp(){
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
        System.out.println("start setup");
        userName = new TextField();
        userLevel = new TextField();
        passWord = new TextField();
        confirmPassWord = new TextField();
        statusText = new Label();
        usernameError = new Label();
        userLevelError = new Label();
        passWordError = new Label();
        table = new TableView<>();

        createUserController = new CreateUserController();
        usersDao = mock(UsersDao.class);
        List<UsersEntity> users = new ArrayList<UsersEntity>();
        UsersEntity user1 = new UsersEntity("Amal", "amal123" ,"13");
        user1.setId(123);
        users.add(user1);
        users.add(new UsersEntity("Ville", "123ville456" ,"15"));
        when(usersDao.getAllUsers()).thenReturn(users);
        when(usersDao.getUserByUsername(user1.getUsername())).thenReturn(user1);
        createUserController.setUsersDao(usersDao);

        createUserController.setUserName(userName);
        createUserController.setUserLevel(userLevel);
        createUserController.setPassWord(passWord);
        createUserController.setConfirmPassWord(confirmPassWord);
        createUserController.setStatusText(statusText);
        createUserController.setUsernameError(usernameError);
        createUserController.setUserLevelError(userLevelError);
        createUserController.setPassWordError(passWordError);
        createUserController.setTable(table);
        createUserController.initialize();
        System.out.println("end setup");
    }

    @Test
    public void TestThatTableHasTwoColumns() {
        assertEquals(table.getColumns().size(), 2);
    }

    @Test
    public void TestRowSelection() {

        UsersEntity user = new UsersEntity();
        user.setUsername("Amal");
        user.setUserlevel("123");

        UsersEntity user2 = new UsersEntity();
        user2.setUsername("Ville");
        user2.setUserlevel("12");

        table.getItems().clear();
        table.getItems().add(user);
        table.getItems().add(user2);

        table.getSelectionModel().select(0);
        assertEquals(userName.getText(), user.getUsername());
        assertEquals(userLevel.getText(), user.getUserlevel());
        assertEquals(createUserController.getSelectedUser(), user);

        table.getSelectionModel().select(1);
        assertEquals(userName.getText(), user2.getUsername());
        assertEquals(userLevel.getText(), user2.getUserlevel());
        assertEquals(createUserController.getSelectedUser(), user2);
    }

    @Test public void TestRefreshTable() {
        table.getItems().clear();
        createUserController.refreshTable();

        // After refresh should have the mocked data from UsersDao.getAllUsers()
        assertEquals(table.getItems().get(0).getUsername(), "Amal");
        assertEquals(table.getItems().get(1).getUsername(), "Ville");
    }

    @Test public void TestUserCreationPasswordValidation() {
        statusText.setTextFill(Color.GREEN);
        statusText.setVisible(false);
        // No password
        passWord.setText("");
        createUserController.handleCreateUser();
        assertEquals(statusText.getText(), LanguageUtils.getString("password_is_required!.user_management"));
        assertEquals(statusText.getTextFill(), Color.RED);
        assertTrue(statusText.isVisible());


        // Passwords don't match
        statusText.setTextFill(Color.GREEN);
        statusText.setVisible(false);
        passWord.setText("amal123");
        createUserController.handleCreateUser();
        assertEquals(statusText.getText(), LanguageUtils.getString("passwords_dont_match!.user_management"));
        assertEquals(statusText.getTextFill(), Color.RED);
        assertTrue(statusText.isVisible());
    }

    @Test public void TestUserCreationUsernameValidation() {
        userName.setText("Amal");
        passWord.setText("123456");
        confirmPassWord.setText("123456");
        statusText.setTextFill(Color.GREEN);
        statusText.setVisible(false);
        createUserController.handleCreateUser();
        assertEquals(statusText.getText(), LanguageUtils.getString("username_already_exists.user_management"));
        assertEquals(statusText.getTextFill(), Color.RED);
        assertTrue(statusText.isVisible());
    }

    @Test public void TestUserCreationSuccessful() {
        UsersEntity user = new UsersEntity("Heikki", "1234567", "123");
        userName.setText(user.getUsername());
        userLevel.setText(user.getUserlevel());
        passWord.setText(user.getPassword());
        confirmPassWord.setText(user.getPassword());

        statusText.setTextFill(Color.WHITE);
        table.getItems().clear();

        createUserController.handleCreateUser();

        // Test that the usersDao createUser was called
        verify(usersDao, times(1)).createUser(argThat((createdUser) -> {
            boolean nameMatch = createdUser.getUsername().equals(user.getUsername());

            // Password should be hashed and NOT match with the inputted one
            boolean passwordMatch = createdUser.getPassword().equals(user.getPassword());

            return nameMatch && !passwordMatch;
        }));

        // Test that the form was reset
        assertEquals(userName.getText(), "");
        assertEquals(userLevel.getText(), "");
        assertEquals(passWord.getText(), "");
        assertEquals(confirmPassWord.getText(), "");

        // Test status text
        assertEquals(statusText.getText(), String.format(LanguageUtils.getString("user_was_created_successfully!.user_management"), user.getUsername()));
        assertEquals(statusText.getTextFill(), Color.GREEN);
        assertTrue(statusText.isVisible());

        // Test that table was refreshed
        assertEquals(2, table.getItems().size());
    }

    @Test
    public void TestUserUpdatePasswordValidation() {
        passWord.setText("12345");
        confirmPassWord.setText("1234");
        statusText.setTextFill(Color.GREEN);
        statusText.setVisible(false);
        UsersEntity selectedUser = new UsersEntity("Amal", "567890", "11");
        selectedUser.setId(123);
        createUserController.setSelectedUser(selectedUser);

        createUserController.updateUser();

        assertEquals(LanguageUtils.getString("passwords_dont_match!.user_management"), statusText.getText());
        assertEquals(statusText.getTextFill(), Color.RED);
        assertTrue(statusText.isVisible());

        verify(usersDao, never()).updateUser(null);
    }

    @Test public void TestUserUpdateUsernameValidation() {
        userName.setText("Amal");
        passWord.setText("123456");
        confirmPassWord.setText("123456");
        statusText.setTextFill(Color.GREEN);
        statusText.setVisible(false);

        UsersEntity selectedUser = new UsersEntity("Heikki", "567890", "11");
        selectedUser.setId(123);
        createUserController.setSelectedUser(selectedUser);

        createUserController.updateUser();

        assertEquals(LanguageUtils.getString("username_already_exists.user_management"), statusText.getText());
        assertEquals(Color.RED, statusText.getTextFill());
        assertTrue(statusText.isVisible());
    }

    @Test public void TestUserUpdateSuccessful() {
        UsersEntity user = new UsersEntity("Heikki", "1234567", "123");
        user.setId(123);

        userName.setText("Lauri");
        userLevel.setText("144");
        passWord.setText("123Lauri123");
        confirmPassWord.setText("123Lauri123");
        createUserController.setSelectedUser(user);

        statusText.setTextFill(Color.WHITE);
        table.getItems().clear();

        createUserController.updateUser();

        // Test that the usersDao updateUser was called
        verify(usersDao, times(1)).updateUser(argThat((updatedUser) -> {
            // Name should match the new one
            boolean nameMatch = updatedUser.getUsername().equals("Lauri");

            // Password should be hashed and NOT match with the inputted one nor the original one
            boolean passwordMatch = updatedUser.getPassword().equals("123Lauri123") || updatedUser.getPassword().equals("1234567");

            return nameMatch && !passwordMatch;
        }));

        // Test that the form was reset
        assertEquals(userName.getText(), "");
        assertEquals(userLevel.getText(), "");
        assertEquals(passWord.getText(), "");
        assertEquals(confirmPassWord.getText(), "");

        // Test status text
        assertEquals(statusText.getText(), String.format((LanguageUtils.getString("user_was_updated_successfully!.user_management")), "Lauri"));
        assertEquals(statusText.getTextFill(), Color.GREEN);
        assertTrue(statusText.isVisible());

        // Test that table was refreshed
        assertEquals(2, table.getItems().size());
    }

    @Test public void TestDeleteUser() {
        UsersEntity user = new UsersEntity("Heikki", "1234567", "123");
        user.setId(123);

        table.getItems().clear();
        table.getItems().add(user);

        statusText.setTextFill(Color.WHITE);
        statusText.setVisible(false);

        createUserController.setSelectedUser(user);
        createUserController.deleteUser();

        // Test that the usersDao deleteUser was called
        verify(usersDao, times(1)).deleteUser(argThat((deletedUser) -> {
            // Confirm that the selected user is deleted
            return deletedUser == user;
        }));

        assertEquals(statusText.getText(), String.format((LanguageUtils.getString("user_was_deleted_successfully!.user_management")), user.getUsername()));
        assertEquals(statusText.getTextFill(), Color.GREEN);
        assertTrue(statusText.isVisible());

        // Test that the form was reset
        assertEquals(userName.getText(), "");
        assertEquals(userLevel.getText(), "");
        assertEquals(passWord.getText(), "");
        assertEquals(confirmPassWord.getText(), "");

        // Test that table was refreshed
        assertEquals(2, table.getItems().size());
    }
}
