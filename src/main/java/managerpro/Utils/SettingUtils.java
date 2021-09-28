package managerpro.Utils;

import java.util.ResourceBundle;

public class SettingUtils {

    public static final String SETTING_CURRENCY ="currency";
    public static final String SETTINGS_FILE = "Settings";

    private static ResourceBundle resourceBundle;
    static {resourceBundle = ResourceBundle.getBundle(SETTINGS_FILE);}

    public static ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public static String getSetting(String key) {
        return getResourceBundle().getString(key);
    }

    public static String getCurrency(){

        return getSetting(SETTING_CURRENCY);
    }
}
