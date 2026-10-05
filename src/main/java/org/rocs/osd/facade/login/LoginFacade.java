package org.rocs.osd.facade.login;


import org.rocs.osd.model.login.Login;

/**
 * Facade interface for managing user login operations in the Office of Student
 * Discipline System.
 */
public interface LoginFacade {

    /**
     * Authenticates a user by checking the username and
     * password against the database.
     *
     * @param inputUserName the username entered by the user.
     * @param inputPassword the password entered by the user.
     * @return true if the username exists and
     * the password matches, false otherwise.
     */
    boolean login(String inputUserName, String inputPassword);
    /**
     * Retrieves a login record by its username.
     *
     * @param username the username to search for
     * @return the Login object if found, or null if not found
     */
    Login getByUsername(String username);

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
    boolean changePassword(
            String oldPassword,
            String newPassword
    );
}
