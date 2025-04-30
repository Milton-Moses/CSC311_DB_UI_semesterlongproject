package service;

import java.security.MessageDigest;
import java.util.prefs.Preferences;

/**
 * Manages user session information with thread-safe singleton pattern.
 * Stores and retrieves user credentials and privileges.
 */
public class UserSession {
    private static volatile UserSession instance;
    private static final int MIN_PASSWORD_LENGTH = 6;

    private String userName;
    private String password;
    private String privileges;

    /**
     * Private constructor for singleton pattern.
     * @param userName the username
     * @param password the password (will be hashed)
     * @param privileges the privilege level
     * @throws IllegalArgumentException if validation fails
     */
    private UserSession(String userName, String password, String privileges) {
        if (userName == null || userName.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("Password must be at least " + MIN_PASSWORD_LENGTH + " characters");
        }
        if (privileges == null) {
            privileges = "USER";
        }

        this.userName = userName;
        this.password = password;
        this.privileges = privileges;

        Preferences userPreferences = Preferences.userRoot();
        userPreferences.put("USERNAME", userName);
        userPreferences.put("PASSWORD", hashPassword(password));
        userPreferences.put("PRIVILEGES", privileges);
    }

    /**
     * Gets the singleton instance with full parameters.
     * @param userName the username
     * @param password the password
     * @param privileges the privilege level
     * @return UserSession instance
     */
    public static UserSession getInstance(String userName, String password, String privileges) {
        if (instance == null) {
            synchronized (UserSession.class) {
                if (instance == null) {
                    instance = new UserSession(userName, password, privileges);
                }
            }
        }
        return instance;
    }

    /**
     * Gets the singleton instance with default USER privileges.
     * @param userName the username
     * @param password the password
     * @return UserSession instance
     */
    public static UserSession getInstance(String userName, String password) {
        return getInstance(userName, password, "USER");
    }

    /**
     * @return the username
     */
    public String getUserName() {
        return this.userName;
    }

    /**
     * @return the password (unhashed)
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Hashes the password using SHA-256 algorithm.
     * @param password the password to hash
     * @return hashed password as hex string
     */
    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    /**
     * @return the privilege level
     */
    public String getPrivileges() {
        return this.privileges;
    }

    /**
     * Clears the current user session.
     */
    public synchronized void cleanUserSession() {
        this.userName = "";
        this.password = "";
        this.privileges = "";
    }

    /**
     * @return string representation of the UserSession
     */
    @Override
    public String toString() {
        return "UserSession{" +
                "userName='" + this.userName + '\'' +
                ", privileges=" + this.privileges +
                '}';
    }
}