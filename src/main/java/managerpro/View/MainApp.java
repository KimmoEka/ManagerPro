package managerpro.View;


import managerpro.Controller.Controller;
import managerpro.Utils.LanguageUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * This is the MainClass of the project, which contains main-method and methods to switch UI-views.
 */
public class MainApp extends Application {

    /**
     * Main-method of the program, starts the program.
     * @param args Command-line parameters
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * JavaFX Stage
     */
    private static Stage primaryStage;
    /**
     * JavaFX Stage
     */
    private static Stage mainStage;

    /**
     * Start-method for the JavaFX user interface.
     * @param primaryStage Stage to be shown for the user.
     * @throws IOException Throws IOException if resource not found
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        MainApp.primaryStage = primaryStage;
        Parent root = FXMLLoader.load(MainApp.class.getResource("/loginUI.fxml"), LanguageUtils.getResourceBundle());
        primaryStage.setTitle("ManagerPro");
        primaryStage.setScene(new Scene(root, 700, 500));
        primaryStage.show();
    }

    /**
     * Method for switching the program to mainView after successful login.
     * @throws IOException Throws IOException if resource not found
     */
    //handling of switching into mainview after logging in
    public static void switchMainView() throws IOException {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/mainUI.fxml"), LanguageUtils.getResourceBundle());
        Parent root1 = loader.load();
        Controller controller = loader.getController();
        mainStage = new Stage();
        mainStage.setTitle("ManagerPro");
        mainStage.setScene(new Scene(root1, 1000, 800));
        mainStage.setMaximized(true);
        mainStage.show();
        controller.openMainInfoPanel();
    }

    /**
     * Method for switching back to loginView after successful logout.
     * @throws IOException Throws IOException if resource not found
     */
    //handling of switching back into login view
    public static void switchLoginView() throws IOException {
        Parent root2 = FXMLLoader.load(MainApp.class.getResource("/loginUI.fxml"), LanguageUtils.getResourceBundle());
        primaryStage.close();
        primaryStage = new Stage();
        primaryStage.setTitle("ManagerPro");
        primaryStage.setScene(new Scene(root2, 700, 500));
        primaryStage.show();
        //MainStage could be null, check first to prevent NPE
        if(mainStage != null)
        mainStage.close();
    }
}




