package org.rocs.osd.facade.login.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rocs.osd.data.dao.login.LoginDao;
import org.rocs.osd.facade.login.LoginFacade;
import org.rocs.osd.model.login.Login;
import org.rocs.osd.model.person.Person;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LoginFacadeImplTest
{
    @Mock
    private LoginDao loginDao;

    private Login login;

    private LoginFacade loginFacade;

    @BeforeEach
    public void setUp()
    {
        Person person = new Person();

        loginFacade = new LoginFacadeImpl(loginDao);
        login = new Login(1, "prefect",
                "$2b$10$T8VCsavGpS/.Wz/cfDVjHuM4bfnVAUuCnSRXzST2bAPdoRJNVujF.",
                person);
    }

    @Test
    void testLogin()
    {
        when(loginDao.findLoginByUsername("prefect")).thenReturn(login);

        boolean result = loginFacade.login("prefect", "1234");

        assertTrue(result);
        verify(loginDao, times(1)).findLoginByUsername("prefect");
    }

    @Test
    void testLoginInvalidPassword() {
        when(loginDao.findLoginByUsername("prefect")).thenReturn(login);

        assertFalse(loginFacade.login("prefect", "wrong"));
    }

    @Test
    void testLoginNonexistentUser() {
        assertFalse(loginFacade.login("unknown", "1234"));
    }

    @Test
    void testLoginBlankUsernameOrPassword() {
        assertFalse(loginFacade.login("", "1234"));
        assertFalse(loginFacade.login("user", ""));
        assertFalse(loginFacade.login("", ""));
        assertFalse(loginFacade.login(null, null));
    }

    @Test
    void changePassword_ShouldReturnTrue_WhenPasswordAndOtpAreValid() {
        String oldPassword = "OldPassword123";
        String newPassword = "NewPassword123";

        Login login = new Login();
        login.setPassword(BCrypt.hashpw(oldPassword, BCrypt.gensalt(12)));

        when(loginDao.findLoginByUsername("prefect"))
                .thenReturn(login);

        when(loginDao.changePassword(anyString()))
                .thenReturn(true);

        boolean result = loginFacade.changePassword(
                oldPassword,
                newPassword
        );

        assertTrue(result);
        verify(loginDao).changePassword(anyString());
    }

}
