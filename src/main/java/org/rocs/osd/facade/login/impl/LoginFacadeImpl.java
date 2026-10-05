package org.rocs.osd.facade.login.impl;

import org.mindrot.jbcrypt.BCrypt;
import org.rocs.osd.data.dao.login.LoginDao;
import org.rocs.osd.facade.login.LoginFacade;
import org.rocs.osd.model.login.Login;

/**
 * Facade implementation for managing Login operations in the Office of Student
 * Discipline System.
 */
public class LoginFacadeImpl implements LoginFacade {

    /**
     * DAO used to access login data from the database.
     */
    private final LoginDao loginDao;

    /**
     * Constructor to set the login DAO dependency.
     *
     * @param pLoginDao the DAO used to access login information
     */
    public LoginFacadeImpl(LoginDao pLoginDao) {
        this.loginDao = pLoginDao;
    }

    /**
     * Authenticates a user using the provided username and password.
     *
     * @param inputUserName the username entered by the user
     * @param inputPassword the password entered by the user
     * @return true if authentication is successful, false otherwise
     */
    @Override
    public boolean login(String inputUserName, String inputPassword) {
        if (inputUserName == null || inputPassword == null
                || inputUserName.isBlank() || inputPassword.isBlank()) {
            return false;
        }

        if (!"prefect".equals(inputUserName)) {
            return false;
        }

        Login login = loginDao.findLoginByUsername(inputUserName);

        if (login == null || login.getPassword() == null
                || login.getPassword().isBlank()) {
            return false;
        }

        // Passwords are stored as BCrypt hashes (same scheme the backend's
        // Spring Security BCryptPasswordEncoder uses), so they must be
        // verified with BCrypt.checkpw() -- a plain String.equals() against
        // the hash would never match the entered plaintext password.
        //
        // The org.mindrot:jbcrypt library used here only recognizes the
        // "$2a$" version tag and throws "Invalid salt revision" for
        // "$2b$"/"$2y$" hashes, even though the actual hash algorithm is
        // identical -- the version tag alone changed to fix an unrelated
        // historical edge case with passwords longer than 255 bytes, which
        // doesn't apply to anything in this system. Normalizing the tag to
        // "$2a$" before checking is the standard, safe workaround.
        String storedHash = login.getPassword();
        if (storedHash.startsWith("$2b$") || storedHash.startsWith("$2y$")) {
            storedHash = "$2a$" + storedHash.substring(4);
        }

        try {
            return BCrypt.checkpw(inputPassword, storedHash);
        } catch (IllegalArgumentException e) {
            // Stored value isn't a valid BCrypt hash (e.g. leftover
            // plaintext test data) -- treat as a non-match rather than
            // letting the exception propagate out of a login attempt.
            return false;
        }
    }
    /**
     * Retrieves a Login object by username.
     *
     * If the provided username is null or blank, this method returns null.
     * Otherwise, it delegates the lookup to the LoginDao.
     *
     * @param username the username to search for
     * @return the Login object associated with the given username,
     *         or null if the username is null, blank, or not found
     */
    @Override
    public Login getByUsername(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }
        return loginDao.findLoginByUsername(username);
    }

    /**
     * Changes the user's password after validating the provided passwords
     * and verifying the old password against the stored password hash.
     * The method returns false if either password is null or blank,
     * if the user account cannot be found, if the stored password is invalid,
     * or if the provided old password does not match the stored password.
     * If validation is successful, the new password is securely hashed
     * before being passed to the login DAO for updating.
     *
     * @param oldPassword the user's current password
     * @param newPassword the new password to set
     * @return true if the password is successfully changed;
     *         false otherwise
     */
    @Override
    public boolean changePassword(
            String oldPassword,
            String newPassword
    ) {
        if (oldPassword == null
                || newPassword == null
                || oldPassword.isBlank()
                || newPassword.isBlank()
        ) {
            return false;
        }

        Login login = loginDao.findLoginByUsername("prefect");

        if (login == null || login.getPassword() == null
                || login.getPassword().isBlank()) {
            return false;
        }

        String storedHash = login.getPassword();

        if (storedHash.startsWith("$2b$") || storedHash.startsWith("$2y$")) {
            storedHash = "$2a$" + storedHash.substring(4);
        }

        if (!BCrypt.checkpw(oldPassword, storedHash)) {
            return false;
        }

        String hashedPassword = BCrypt.hashpw(
                newPassword,
                BCrypt.gensalt(12)
        );

        return loginDao.changePassword(hashedPassword);
    }

}
