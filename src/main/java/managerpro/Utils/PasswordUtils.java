package managerpro.Utils;

import org.mindrot.jbcrypt.BCrypt;

/**
 *  A class for checking Login information. Uses Mindrot JBCrypt library to
 *  salt, hash and check passwords
 * @author OTP7
 */
public class PasswordUtils {
    /**
     * Method for generating salted passwords
     * @param plainTextPassword A string that will be salted and hashed with BCrypt
     * @return Returns a salted hash of the string.
     */
    public static String hashPassword(String plainTextPassword){
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(12));
    }

    /**
     * Method for checking if plaintext password matches the hash provided
     * @param plainPassword Plaintext password
     * @param hashedPassword Hash to compare agains
     * @return Returns true if password matches the hash, else return false
     */
    public static boolean checkPass(String plainPassword, String hashedPassword) {
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            if (plainPassword != null) {
                return plainPassword.equals(hashedPassword);
            }
        }

        return false;
    }
}
