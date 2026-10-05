package org.rocs.osd.data.dao.login.impl;

import org.rocs.osd.data.connection.ConnectionHelper;
import org.rocs.osd.data.dao.login.LoginDao;
import org.rocs.osd.model.department.Department;
import org.rocs.osd.model.login.Login;
import org.rocs.osd.model.person.Person;
import org.rocs.osd.model.person.employee.Employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

/**
 * DAO implementation for managing Login records in the Office of Student
 * Discipline System.
 */
public class LoginDaoImpl implements LoginDao {

    /**
     * Finds and retrieves a Login object from the database by username.
     *
     * @param username the username to search for.
     * @return a Login object with credentials and associated person info.
     * Returns an empty Login object if no match is found.
     */
    @Override
    public Login findLoginByUsername(String username) {
        Login login = new Login();

        try (Connection conn = ConnectionHelper.getConnection();
             PreparedStatement statement = conn.prepareStatement(
                     "SELECT l.id, l.username, l.password, l.personID, "
                             + "p.lastname, p.firstname, p.middleName, "
                             + "e.employeeID, e.department, e.employeeRole "
                             + "FROM Login l "
                             + "JOIN person p ON l.personID = p.personID "
                             + "LEFT JOIN employee e "
                             + "ON p.personID = e.personID "
                             + "WHERE l.username = ?"
             )) {

            statement.setString(1, username);
            try (ResultSet rs = statement.executeQuery()) {

                if (rs.next()) {
                    Person person = new Person();
                    person.setPersonID(rs.getLong("personID"));
                    person.setLastName(rs.getString("lastname"));
                    person.setFirstName(rs.getString("firstname"));
                    person.setMiddleName(rs.getString("middleName"));

                    Employee employee = new Employee();
                    String empId = rs.getString("employeeID");
                    if (empId != null) {
                        employee.setEmployeeId(empId);

                        String dept = rs.getString("department");
                        if (dept != null) {
                            employee.setDepartment(
                                    Department.valueOf(
                                            dept.toUpperCase(Locale.ROOT))
                            );
                        }

                        employee.setEmployeeRole(rs.getString("employeeRole"));
                        login.setEmployee(employee);
                    }

                    login.setId(rs.getLong("id"));
                    login.setUsername(rs.getString("username"));
                    login.setPassword(rs.getString("password"));
                    login.setPerson(person);
                }
            }
        } catch (SQLException e) {
            // Don't swallow this -- a failed DB connection/query looked
            // identical to a wrong password (both silently returned an
            // empty Login), which made "invalid username or password"
            // misleading when the real cause was a connectivity problem.
            // Surface it so the caller (and the UI) can tell the
            // difference.
            throw new RuntimeException(
                    "Database error while looking up user \"" + username
                            + "\": " + e.getMessage(), e);
        }

        return login;
    }

    /**
     * Changes the password of the "prefect" user.
     *  The new password is passed as a parameter to a prepared statement
     *  and saved to the login table. Returns true if the update
     *  executes successfully, or false if a database error occurs.
     *
     * @param newPassword new Password.
     * @return true if successful.
     */
    @Override
    public boolean changePassword(String newPassword) {
        try (Connection conn = ConnectionHelper.getConnection();
             PreparedStatement statement = conn.prepareStatement(
                     "UPDATE login "
                             + "SET password = ? "
                             + "WHERE username = 'prefect'"
             )) {

            statement.setString(1, newPassword);
            statement.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
