package com.mycompany.chatapppart1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * LoginTest — Unit tests for the Login class (Part 1).
 */
public class LoginTest {

    private Login login;

    @BeforeEach
    public void setUp() {
        login = new Login("John", "Doe");
    }

    // -------------------------------------------------------
    // USERNAME TESTS
    // -------------------------------------------------------

    @Test
    public void testCheckUserNameSuccess() {
        assertTrue(login.checkUserName("jo_hn"),
                "Username with underscore and <= 5 chars should pass");
    }

    @Test
    public void testCheckUserNameFailNoUnderscore() {
        assertFalse(login.checkUserName("john"),
                "Username without underscore should fail");
    }

    @Test
    public void testCheckUserNameFailTooLong() {
        assertFalse(login.checkUserName("jo_hn1"),
                "Username longer than 5 chars should fail");
    }

    @Test
    public void testRegisterUsernameSuccess() {
        assertEquals("Username successfully captured.",
                login.registerUsername("jo_hn"));
    }

    @Test
    public void testRegisterUsernameFailure() {
        assertEquals(
            "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters.",
            login.registerUsername("johnsmith")
        );
    }

    // -------------------------------------------------------
    // PASSWORD TESTS
    // -------------------------------------------------------

    @Test
    public void testCheckPasswordComplexitySuccess() {
        assertTrue(login.checkPasswordComplexity("Password1!"),
                "Valid password should pass complexity check");
    }

    @Test
    public void testCheckPasswordComplexityFailTooShort() {
        assertFalse(login.checkPasswordComplexity("Pa1!"),
                "Password under 8 chars should fail");
    }

    @Test
    public void testCheckPasswordComplexityFailNoCapital() {
        assertFalse(login.checkPasswordComplexity("password1!"),
                "Password without capital letter should fail");
    }

    @Test
    public void testCheckPasswordComplexityFailNoNumber() {
        assertFalse(login.checkPasswordComplexity("Password!"),
                "Password without number should fail");
    }

    @Test
    public void testCheckPasswordComplexityFailNoSpecial() {
        assertFalse(login.checkPasswordComplexity("Password1"),
                "Password without special character should fail");
    }

    @Test
    public void testRegisterPasswordSuccess() {
        assertEquals("Password successfully captured.",
                login.registerPassword("Password1!"));
    }

    @Test
    public void testRegisterPasswordFailure() {
        assertEquals(
            "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number and a special character.",
            login.registerPassword("weak")
        );
    }

    // -------------------------------------------------------
    // CELL PHONE NUMBER TESTS
    // -------------------------------------------------------

    @Test
    public void testCheckCellPhoneNumberSuccess() {
        assertTrue(login.checkCellPhoneNumber("+27831234567"),
                "+27 followed by 9 digits should pass");
    }

    @Test
    public void testCheckCellPhoneNumberFailNoPlus() {
        assertFalse(login.checkCellPhoneNumber("27831234567"),
                "Number without + prefix should fail");
    }

    @Test
    public void testCheckCellPhoneNumberFailWrongCode() {
        assertFalse(login.checkCellPhoneNumber("+26831234567"),
                "Number not starting with +27 should fail");
    }

    @Test
    public void testRegisterCellPhoneNumberSuccess() {
        assertEquals("Cell phone number successfully added.",
                login.registerCellPhoneNumber("+27831234567"));
    }

    @Test
    public void testRegisterCellPhoneNumberFailure() {
        assertEquals("Cell phone number incorrectly formatted or does not contain international code.",
                login.registerCellPhoneNumber("08575975889"));
    }

    // -------------------------------------------------------
    // LOGIN TESTS
    // -------------------------------------------------------

    @Test
    public void testLoginUserSuccess() {
        login.checkUserName("jo_hn");
        login.checkPasswordComplexity("Password1!");
        assertTrue(login.loginUser("jo_hn", "Password1!"));
    }

    @Test
    public void testLoginUserFailWrongPassword() {
        login.checkUserName("jo_hn");
        login.checkPasswordComplexity("Password1!");
        assertFalse(login.loginUser("jo_hn", "WrongPass1!"));
    }

    @Test
    public void testReturnLoginStatusSuccess() {
        login.checkUserName("jo_hn");
        login.checkPasswordComplexity("Password1!");
        assertEquals("Welcome John, Doe it is great to see you again.",
                login.returnLoginStatus("jo_hn", "Password1!"));
    }

    @Test
    public void testReturnLoginStatusFailure() {
        login.checkUserName("jo_hn");
        login.checkPasswordComplexity("Password1!");
        assertEquals("Username or password incorrect, please try again.",
                login.returnLoginStatus("wrong", "wrong"));
    }
}
