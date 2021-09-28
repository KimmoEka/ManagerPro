package managerpro.Utils;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Currency;
import java.util.Locale;

/**
 * FormattingUtils class that holds utility methods for formatting various Locale sensitive elements of the UI
 * @author OTP7
 */
public class FormattingUtils {
    /**
     *  A formatter method, that uses a NumberFormat to format a floating point double -variable to a currency.
     * @param currency A double -floating point value, that is formatted to currency with a NumberFormat.
     * @return Returns a string representation of the parameter, formatted to Locale specific currency.
     */
    public static String formatCurrency(double currency) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(LanguageUtils.getLocale());
        formatter.setCurrency(Currency.getInstance(SettingUtils.getCurrency()));
        return formatter.format(currency);
    }
    /**
     * Uses a language tag for current language to return a DateTimeFormatter with locale of currentLanguage selection. This DateTimeFormatter is configured to format dates.
     * @return DateTimeFormatter for currentLanguage, configured to format dates
     */
    public static DateTimeFormatter getDateFormatter(){
        String langkey = LanguageUtils.getCurrentLanguageTag();
        return DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM ).withLocale( Locale.forLanguageTag(langkey) );
    }
    /**
     * Uses a language tag for current language to return a DateTimeFormatter with locale of currentLanguage selection. This DateTimeFormatter is configured to format times.
     * @return DateTimeFormatter for currentLanguage, configured to format times
     */
    public static DateTimeFormatter getTimeFormatter(){
        String langkey = LanguageUtils.getCurrentLanguageTag();
        return DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT ).withLocale( Locale.forLanguageTag(langkey) );
    }
    /**
     *         Takes a phone number as a string, tries to parse it, using user selected locale as default country
     *         If entered phone number has a country code +XX, then it overrides the default locale and the country code is used to parse the phone number
     *         Returns a phone number in International format of parsed country.
     *         If parsing fails, prints stacktrace and returns the string passed as parameter
     * @param phone the phone number that will be formatted
     * @return Returns a formatted phoneNumber if parsing was successful, returns the unchanged parameter if parsing failed.
     */
    public static String formatPhoneNumber(String phone){
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber phoneNumber = null;
        try {
            phoneNumber = phoneNumberUtil.parse(phone, LanguageUtils.getLocale().getCountry());
        }catch (Exception e){
            e.printStackTrace();
        }

        if(phoneNumber != null && phoneNumberUtil.isPossibleNumber(phoneNumber))
        {
            return phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
        }
        return phone;
    }
}
