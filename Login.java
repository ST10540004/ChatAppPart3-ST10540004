package com.mycompany.chatapppart1;

import java.util.regex.Pattern;

/**
 * Login class handles user registration and authentication.
 * Validates username, password, and cell phone number on registration.
 */
public class Login {

    private String username;
    private String password;
    private String cellPhoneNumber;
    private String firstName;
    private String lastName;

    public Login(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * Checks that username contains an underscore and is no more than 5 characters.
     */
    public boolean checkUserName(String username) {
        this.username = username;
        return username.contains("_") && username.length() <= 5;
    }

    /**
     * Registers username and returns success or failure message.
     */
    public String registerUsername(String username) {
        if (checkUserName(username)) {
            return "Username successfully captured.";
        } else {
            return "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters.";
        }
    }

    /**
     * Checks password meets complexity: >= 8 chars, capital letter,
     * number, and special character.
     */
    public boolean checkPasswordComplexity(String password) {
        this.password = password;
        boolean lengthCheck   = password.length() >= 8;
        boolean capitalCheck  = Pattern.compile("[A-Z]").matcher(password).find();
        boolean numberCheck   = Pattern.compile("[0-9]").matcher(password).find();
        boolean specialCheck  = Pattern.compile("[^a-zA-Z0-9]").matcher(password).find();
        return lengthCheck && capitalCheck && numberCheck && specialCheck;
    }

    /**
     * Registers password and returns success or failure message.
     */
    public String registerPassword(String password) {
        if (checkPasswordComplexity(password)) {
            return "Password successfully captured.";
        } else {
            return "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number and a special character.";
        }
    }

    /**
     * Checks cell phone number matches +27 followed by 9 digits.
     */
    public boolean checkCellPhoneNumber(String cellPhoneNumber) {
        this.cellPhoneNumber = cellPhoneNumber;
        return Pattern.matches("^\\+27\\d{9}$", cellPhoneNumber);
    }

    /**
     * Registers cell phone number and returns success or failure message.
     */
    public String registerCellPhoneNumber(String cellPhoneNumber) {
        if (checkCellPhoneNumber(cellPhoneNumber)) {
            return "Cell phone number successfully added.";
        } else {
            return "Cell phone number incorrectly formatted or does not contain international code.";
        }
    }

    /**
     * Verifies login credentials match registered username and password.
     */
    public boolean loginUser(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }

    /**
     * Returns welcome message on successful login, or error message on failure.
     */
    public String returnLoginStatus(String username, String password) {
        if (loginUser(username, password)) {
            return "Welcome " + firstName + ", " + lastName + " it is great to see you again.";
        } else {
            return "Username or password incorrect, please try again.";
        }
    }

    // Getters used by other classes
    public String getFirstName()       { return firstName; }
    public String getLastName()        { return lastName; }
    public String getUsername()        { return username; }
    public String getCellPhoneNumber() { return cellPhoneNumber; }
}
