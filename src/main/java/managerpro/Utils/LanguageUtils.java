package managerpro.Utils;

import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
/**
 * Utility class, that hold methods and properties for formatting different elements of the UI based on Locale. It's used to bring higher degree of i18n to the
 * software, and to enable easy addition of new languages. It also contains a rudimentary translator, that can be used to translate the Hibernate entities toString -outputs
 * to different language. The translator needs some work to improve reliability over wider range of Locales. Majority of the methods and properties are available only in static context.
 * @author OTP7
 */
public class LanguageUtils {
    /**
    * ResourceBundle, that holds all the string resources. A method invoked by a GUI element is used to load a different resource bundle based on locale selection
    */
    private static ResourceBundle resourceBundle;
    /**
     * A string representation of current language.
     */
    private static String currentLanguage;
    /**
     * A map of Strings with Strings as keys. Holds the supported languages and their corresponding language keys.
     */
    private static final Map<String, String> languages = Map.of(
            "English", "en-US",
            "Suomi", "fi-FI",
            "Português brasileiro", "pt-BR"
    );
    /**
     * Default language.
     * TODO: Maybe make this changeable through software settings?
     */
    private static final String defaultLanguage = "English";

    static {
        setLanguage("");
    }

    /**
     * Returns the private resourceBundle -member.
     * @return ResourceBundle currently loaded resourceBundle
     */
    public static ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    /**
     * A method to get string resources from the bundle. Will throw a NPE if key is null, MissingResourceException if no object for the given key can be found or a ClassCastException, if the object found for the given key is not a string
     * @param key A string, that will be used as a key to get a resource from the resourceBundle
     * @return A string value that was found with the key.
     */
    public static String getString(String key) {
        return getResourceBundle().getString(key);
    }
    /**
     * Sets the currentLanguage member to parameter and checks if the language is in the languages -map.
     * If not, the language is set to default. Then the resourceBundle is loaded for the language
     * @param language A string parameter that is set to currentLanguage and is used as a key for languages -map
     */
    public static void setLanguage(String language) {
        currentLanguage = language;
        String langKey = languages.get(language);
        if (langKey == null) {
            currentLanguage = defaultLanguage;
            langKey = languages.get(defaultLanguage);
        }
        resourceBundle = ResourceBundle.getBundle("messages", Locale.forLanguageTag(langKey));
    }

    /**
     * Returns an ArrayList of available languages, which is a List of languages -maps key set.
     * @return An ArrayList of available languages
     */
    public static List<String> getLanguages() {
        return new ArrayList<>(languages.keySet());
    }

    /**
     * Getter for currentLanguage
     * @return String, currentLanguage
     */
    public static String getCurrentLanguage() {
        return currentLanguage;
    }

    /**
     * A method that uses the currentLanguage to find a Locale and return it.
     * @return returns a Locale based on current language
     */
    public static Locale getLocale(){
        String langkey = languages.get(currentLanguage);
        return Locale.forLanguageTag(langkey);
    }


    /**
     * A rudimentary translator function, that takes an object, uses the objects toString() method, splits the return value at whitespace and looks for values in the resourceBundle
     * The algorithm finds phrases and translates them. The algorithm reads 'words' from the whitespace -split string array, until it encounters a word that has a 'phrase ending character'.
     * After reading as many words before the phrase ending character, it builds a string from all of the words and tries to translate it.
     * If no translation was found in resourceBundle, it drops the last word and tries a shorter phrase, until it finds a translation, or all the words have been dropped.
     * The algorithm keeps track of it's progress, and iterates until the output of o.toString() has been handled.
     *
     * @param o Object whose toString() -method needs to be translated
     * @return Returns the translated string.
     */
    public static String translateToString(Object o) {
        //StringBuilder for the output
        StringBuilder output = new StringBuilder();
        //Split the o.toString to array by whitespace -> get array of words
        String[] arr = o.toString().split(" ");
        //List the 'words' going to be translated next
        List<String> toTranslator = new ArrayList<>();
        //The current phrase ending character
        String endChar="";
        //String of all phrase ending characters
        String phraseEndingCharacters = "!`\"#¤%&/()=?@£${[]}\\´`*¨-.,;:_<>|^~€";
        //The loop in which the magick happens.
        //The first loop moves a index pointer along the array of words
        for (int i = 0; i < arr.length; i++) {
            toTranslator.clear();
            //The second starts from the index the first loop pointer points to and adds words to 'toTranslator' -list, until it encounters a phrase ending character
            for (int j = i; j < arr.length; j++) {
                if (!phraseEndingCharacters.contains(Character.toString(arr[j].charAt(arr[j].length() - 1)))) {
                    toTranslator.add(arr[j]);
                } else {
                    toTranslator.add(arr[j].substring(0, arr[j].length() - 1));
                    //the index of the first loop is changed to j, since the algorithm presumes the words that were picked are now dealt with
                    i = j;
                    //Remember the ending character
                    endChar = arr[j].substring(arr[j].length() - 1);
                    break;
                }
            }
            //This loop builds a string and uses it as a key for the resource bundle
            //If the string is not a key, then discard one word from the toTranslate -list and try again with a shorter key
            //Every time we append to output, we break out from the loop.
            for( int a=0;a<toTranslator.size();a++) {
                StringBuilder s = new StringBuilder();
                for (int k = 0; k < toTranslator.size()-a; k++) {
                    s.append(toTranslator.get(k).toLowerCase());
                    if (k != toTranslator.size() - 1) {
                        s.append("_");
                    }
                }
                //If the string is a key, append the resource to output, along with the ending character and a whitespace
                if (getResourceBundle().containsKey(s.toString())) {
                    output.append(getResourceBundle().getString(s.toString()));
                    output.append(endChar);
                    output.append(" ");
                    break;
                }
                //If there was only one word to translate and it was not a key, just append it to output
                else if(toTranslator.size() == 1){
                    output.append(toTranslator.get(0));
                    //If it wasn't the last word, append endChar and a whitespace
                    if(i != arr.length-1) {
                        output.append(endChar);
                        output.append(" ");
                    }
                    //If it WAS the last word and the toString output ends with a phrase ending character, append endChar
                    if(i == arr.length-1 && phraseEndingCharacters.contains(o.toString().substring(o.toString().length()-1)))
                        output.append(endChar);
                    break;
                }
                //If we have exhausted the toTranslator list, and have not successfully appended anything to output yet,
                //Just append the first word along with a whitespace to output and move on
                else if(a== toTranslator.size()-1){
                    output.append(toTranslator.get(0));
                    output.append(" ");
                    break;
                }
                //Every time we discard a word from the toTranslate -list, we must move i backwards
                //to ensure the word will be included in the next iteration. This prevents the skipping of words.
                i--;
            }
        }
        return output.toString();
    }

    /**
     *  Return the language tag of the current language.
     * @return A language tag string (eg en-US)
     */
    public static String getCurrentLanguageTag() {
        return languages.get(currentLanguage);
    }
}
