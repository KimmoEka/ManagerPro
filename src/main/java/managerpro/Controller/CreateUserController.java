package managerpro.Controller;

import managerpro.Model.IUsersDao;
import managerpro.Model.UsersDao;
import managerpro.Model.UsersEntity;
import managerpro.Utils.GUIUtils;
import managerpro.Utils.LanguageUtils;
import managerpro.Utils.PasswordUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This is a Controller class for the create user -view. It gathers the information from the user interface,
 * validates it and sends it to database. On initialisation it adds listeners to TextFields and calls getConstraint violations
 * when the focus is lost from the TextField.
 * @author OTP7
 */
public class CreateUserController {
    /**
     * JavaFX TextField that holds a username
     */
    @FXML private TextField userName;
    /**
     * JavaFX TextField that holds a userlevel
     */
    @FXML private TextField userLevel;
    /**
     * JavaFX TextField that holds a password
     */
    @FXML private TextField passWord;
    /**
     * JavaFX TextField that holds password confirmation
     */
    @FXML private TextField confirmPassWord;
    /**
     * JavaFX Label that is used to show the status of the CRUD method under the textFields
     */
    @FXML private Label statusText;
    /**
     * JavaFX Label that is used to show an error message on the ui, if there is an error with the username
     */
    @FXML private Label usernameError;
    /**
     * JavaFX Label that is used to show an error message on the ui, if there is an error with the userlevel
     */
    @FXML private Label userLevelError;
    /**
     * JavaFX Label that is used to show an error message on the ui, if there is an error with the password
     */
    @FXML private Label passWordError;
    /**
     * JavaFX TableView, which is used to display the user information stored in database.
     */
    @FXML private TableView<UsersEntity> table;

    private IUsersDao usersDao;
    private UsersEntity selectedUser;

    /**
     * FXML initializer method. Attaches listeners to TextFields that call {@link #getConstraintViolations(Class, String, String)}
     * to validate the users input.
     */
    @FXML
    public void initialize() {
        if (usersDao == null) {
            usersDao = new UsersDao(LoginController.getSessionFactory(), LoginController.createValidatorFactory());
        }

        userName.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            System.out.println("GOES HERERE");
            if (!newValue) { //when focus lost
                usernameError.setText(
                    getConstraintViolations(
                        UsersEntity.class,
                        "username",
                        userName.getText()
                    )
                );
            }
        });

        userLevel.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            if (!newValue) { //when focus lost
                userLevelError.setText(
                        getConstraintViolations(
                                UsersEntity.class,
                                "userlevel",
                                userLevel.getText()
                        )
                );
            }
        });

        passWord.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            if (!newValue) { //when focus lost
                passWordError.setText(
                    getConstraintViolations(
                        UsersEntity.class,
                        "password",
                        passWord.getText()
                    )
                );
            }
        });
        // Add columns to table
        TableColumn<UsersEntity, String> column1 = new TableColumn<>(LanguageUtils.getString("username.user_management"));
        column1.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<UsersEntity, String> column2 = new TableColumn<>(LanguageUtils.getString("user_level.user_management"));
        column2.setCellValueFactory(new PropertyValueFactory<>("userlevel"));

        table.getColumns().add(column1);
        table.getColumns().add(column2);

        TableView.TableViewSelectionModel<UsersEntity> selectionModel = table.getSelectionModel();

        // set selection mode to only 1 row
        selectionModel.setSelectionMode(SelectionMode.SINGLE);

        // Listen to table row click
        selectionModel.selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            handleRowSelection(newValue);
        });

        refreshTable();
    }
    public void refreshTable() {
        List<UsersEntity> users = usersDao.getAllUsers();
        table.getItems().clear();

        for (UsersEntity user : users) {
            table.getItems().add(user);
        }

        GUIUtils.autoResizeColumns(table);
    }


    private void handleRowSelection(UsersEntity selectedUser) {
        this.selectedUser = selectedUser;
        userName.setText(selectedUser == null ? "" : selectedUser.getUsername());
        userLevel.setText(selectedUser == null ? "" : String.valueOf(selectedUser.getUserlevel()));
    }

    /**
     *
     * @param aClass class to be validated. It needs to have the annotations for the validator
     * @param fieldName field to validate
     * @param value value to be set to field
     * @param <T> Takes classes of generic type
     * @return Returns an empty string if all is fine, else return error generated by validator
     */
    private <T> String getConstraintViolations(Class<T> aClass, String fieldName, String value) {
        Validator validator = LoginController.getValidatorFactory().getValidator();
        Set<ConstraintViolation<T>> errors = validator.validateValue(aClass, fieldName, value);

        if (!errors.isEmpty()) {
            return errors.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(", "));
        } else {
            return "";
        }
    }

    /* Method to CREATE a user in the database */

    /**
     * Method for handling the creation of the user. Checks if the passwords match and are not empty
     * Creates a {@link managerpro.Model.UsersDao} and uses it to check if username is unique (no such user found in DB).
     * Then creates a new {@link managerpro.Model.UsersEntity}  form the user interface data (if validation is ok), adds it to databse and clears the
     * user creation form.
     */
    @FXML
    public void handleCreateUser(){
        statusText.setVisible(false);

        if (IsInvalidPassword()) return;
        if (IsInvalidUsername()) return;

        passWord.deselect();
        UsersEntity user = new UsersEntity();
        user.setUsername(userName.getText());
        user.setPassword(PasswordUtils.hashPassword(passWord.getText()));
        user.setUserlevel(userLevel.getText());
        try {
            System.out.println("Create user");
            usersDao.createUser(user);
        } catch (IllegalArgumentException e) {
            statusText.setText(e.getMessage());
            statusText.setTextFill(Color.RED);
            statusText.setVisible(true);
            return;
        }

        resetForm();

        statusText.setText(String.format(LanguageUtils.getString("user_was_created_successfully!.user_management"), user.getUsername()));
        statusText.setTextFill(Color.GREEN);
        statusText.setVisible(true);

        refreshTable();
    }

    private boolean IsInvalidPassword() {
        if (passWord.getText().length() == 0) {
            statusText.setText(LanguageUtils.getString("password_is_required!.user_management"));
            statusText.setTextFill(Color.RED);
            statusText.setVisible(true);
            return true;
        } else if (!passWord.getText().equals(confirmPassWord.getText())) {
            statusText.setText(LanguageUtils.getString("passwords_dont_match!.user_management"));
            statusText.setTextFill(Color.RED);
            statusText.setVisible(true);
            return true;
        }
        return false;
    }
    /**
     * Checks if user is selected, the password is valid, username is unique. Then updates the row in databse, sets status message and clears the from.
     */
    @FXML
    public void updateUser() {
        if (selectedUser == null || selectedUser.getId() == 0) return;

        if (passWord.getText() != null && !"".equals(passWord.getText())) {
            if (IsInvalidPassword()) return;

            selectedUser.setPassword(PasswordUtils.hashPassword(passWord.getText()));
        }

        if (!userName.getText().equals(selectedUser.getUsername())) {
            if (IsInvalidUsername()) return;

            selectedUser.setUsername(userName.getText());
        }

        selectedUser.setUserlevel(userLevel.getText());
        usersDao.updateUser(selectedUser);

        statusText.setText(String.format((LanguageUtils.getString("user_was_updated_successfully!.user_management")), selectedUser.getUsername()));
        statusText.setTextFill(Color.GREEN);
        statusText.setVisible(true);

        selectedUser = null;
        userName.setText("");
        userLevel.setText("");
        passWord.setText("");
        confirmPassWord.setText("");
        refreshTable();
    }

    private boolean IsInvalidUsername() {
        // Check that username doesnt exist
        UsersEntity existingUser = usersDao.getUserByUsername(userName.getText());
        if (existingUser != null) {
            statusText.setText(LanguageUtils.getString("username_already_exists.user_management"));
            statusText.setTextFill(Color.RED);
            statusText.setVisible(true);
            return true;
        }
        return false;
    }
    /**
     * If an user is selected, the method deletes the user from database, sets a status message and resets the form
     */
    @FXML
    public void deleteUser() {
        if (selectedUser == null || selectedUser.getId() == 0) return;
        usersDao.deleteUser(selectedUser);

        statusText.setText(String.format((LanguageUtils.getString("user_was_deleted_successfully!.user_management")), selectedUser.getUsername()));
        statusText.setTextFill(Color.GREEN);
        statusText.setVisible(true);

        selectedUser = null;
        userName.setText("");
        userLevel.setText("");
        passWord.setText("");
        confirmPassWord.setText("");
        refreshTable();
    }

    /**
     * Clears the textfields in the form and sets focus on the username field.
     */
    public void resetForm() {
        userName.setText("");
        userLevel.setText("");
        passWord.setText("");
        confirmPassWord.setText("");
        statusText.setVisible(false);
        userName.requestFocus();
    }

    /* Used for testing */

    public void setUserName(TextField userName) {
        this.userName = userName;
    }

    public void setUserLevel(TextField userLevel) {
        this.userLevel = userLevel;
    }

    public void setPassWord(TextField passWord) {
        this.passWord = passWord;
    }

    public void setConfirmPassWord(TextField confirmPassWord) {
        this.confirmPassWord = confirmPassWord;
    }

    public void setStatusText(Label statusText) {
        this.statusText = statusText;
    }

    public void setUsernameError(Label usernameError) {
        this.usernameError = usernameError;
    }

    public void setUserLevelError(Label userLevelError) {
        this.userLevelError = userLevelError;
    }

    public void setPassWordError(Label passWordError) {
        this.passWordError = passWordError;
    }

    public void setTable(TableView table) {
        this.table = table;
    }

    public void setSelectedUser(UsersEntity selectedUser) {
        this.selectedUser = selectedUser;
    }

    public void setUsersDao(IUsersDao usersDao) {
        this.usersDao = usersDao;
    }

    public UsersEntity getSelectedUser() {
        return this.selectedUser;
    }
}
