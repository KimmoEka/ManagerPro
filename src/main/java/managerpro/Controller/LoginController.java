package managerpro.Controller;

import managerpro.Model.UsersDao;
import managerpro.Model.UsersEntity;
import managerpro.Utils.LanguageUtils;
import managerpro.Utils.PasswordUtils;
import managerpro.View.MainApp;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.resourceloading.PlatformResourceBundleLocator;

import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.io.IOException;

/**
 * This is the Controller class for login-view. It takes user input for username and password.
 * It compares the username and password input to existing users in database.
 * SessionFactory and ValidatorFactory are also created here because this is the first controller the UI comes in contact with.
 * @author OTP7
 */
public class LoginController {
    /**
     * Boolean for Sessionfactory creation status.
     */
    private static boolean factoryCreated = false;
    /**
     * Hibernate SessionFactory
     */
    private static SessionFactory ourSessionFactory;
    /**
     * Javax.validation ValidatorFactory
     */
    private static ValidatorFactory validatorFactory;
    /**
     * JavaFX Label that is used to show a message if user has entered wrong credentials
     */
    @FXML private Label wrongInput;
    /**
     * JavaFX TextField text field for entering username
     */
    @FXML private TextField userName;
    /**
     * JavaFX PasswordField text field for enteting password
     */
    @FXML private PasswordField passWord;
    /**
     * JavaFX ChoiceBox, that holds the selection of available languages and is used to change the language of the UI
     */
    @FXML private ChoiceBox<String> choiceBox;

    /**
     * Constructor for LoginController.
     */
    public LoginController(){
        factoryCreated = false;
        ourSessionFactory = createSessionFactory();
        validatorFactory = createValidatorFactory();
    }

    @FXML
    public void initialize () {
        choiceBox.getItems().addAll(LanguageUtils.getLanguages());
        choiceBox.setValue(LanguageUtils.getCurrentLanguage());
        choiceBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            LanguageUtils.setLanguage(newValue);
            try {
                MainApp.switchLoginView();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Method for creating ValidatorFactory.
     * @return a built validatorFactory.
     */
    public static ValidatorFactory createValidatorFactory() {
        //Create ValidatorFactory which returns validator
        HibernateValidatorConfiguration configure = Validation.byProvider(HibernateValidator.class).configure();

        configure.messageInterpolator(new ResourceBundleMessageInterpolator(
                new PlatformResourceBundleLocator("messages")
        )
        {
            @Override
            public String interpolate(String message, Context context)
            {
                return super.interpolate(message, context, LanguageUtils.getLocale());
            }
        });

        //return Validation.buildDefaultValidatorFactory();
        return configure.buildValidatorFactory();
    }

    /**
     * Method for creating a SessionFactory, on successful build cannot be created again.
     * @return a Session from the created SessionFactory.
     */
    //creating of SessionFactory
    private static SessionFactory createSessionFactory() {
        if (!factoryCreated) {
            try {
                Configuration configuration = new Configuration();
                configuration.configure();
                factoryCreated = true;
                return configuration.buildSessionFactory();
            } catch (Throwable ex) {
                throw new ExceptionInInitializerError(ex);
            }
        }
        return null;
    }

    /**
     * Getter for session from SessionFactory.
     * @return a Session from SessionFactory.
     * @throws HibernateException Hibernate Exception is thrown if error in opening session
     */
    public static Session getSession() throws HibernateException {
        return ourSessionFactory.openSession();
    }

    /**
     * Getter for SessionFactory, used after creation of the factory.
     * @return returns the SessionFactory.
     */
    public static SessionFactory getSessionFactory(){

        return ourSessionFactory;
    }

    /**
     * Getter for ValidatorFactory.
     * @return the ValidatorFactory.
     */
    public static ValidatorFactory getValidatorFactory() {
        return validatorFactory;
    }

    /**
     * Method for handling the action of pressing the logIn-button.
     * @param event LogIn-button event.
     * @throws IOException Throws IOException if resource not found
     */
    //handling of logging in with clicking the button
    public void handleLogin(Event event) throws IOException {
        this.login();
    }

    /**
     * Method for handling the pressing of ENTER-key to log in.
     * @param e KeyEvent for ENTER-key on keyboard.
     * @throws IOException Throws IOException if resource not found
     */
    @FXML
    //handling of logging in with Enter
    public void onEnter(KeyEvent e) throws IOException {
        if(e.getCode().toString().equals("ENTER")) {
            this.login();
        }
    }

    /**
     * Method for checking the users input values for username and password.
     * On successful login shows mainView, on unsuccessful login shows error for incorrect input.
     * @throws IOException Throws IOException if resource not found
     */
    //Logging in user credentials check and switching to main view
    public void login() throws IOException {
        String uName = userName.getText();
        String pWord = passWord.getText();
        UsersDao userDao = new UsersDao(ourSessionFactory, validatorFactory);
        UsersEntity user = userDao.getUserByUsername(uName);
        if (user != null && PasswordUtils.checkPass(pWord, user.getPassword())) {
            MainApp.switchMainView();
        }else {
            wrongInput.setVisible(true);
        }
    }

}
