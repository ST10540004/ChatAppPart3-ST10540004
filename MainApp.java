package com.mycompany.chatapppart1;

import java.util.Scanner;

/**
 * MainApp — Entry point for the QuickChat application.
 *
 * Part 1: User registration and login.
 * Part 2: Send messages with for loop and while loop menu.
 * Part 3: Load stored messages, added menu option 4 with 6 sub-options.
 *
 * Tip: All logic stays in Message.java. MainApp only calls methods.
 */
public class MainApp {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // -------------------------------------------------------
        // PART 1 — REGISTRATION
        // -------------------------------------------------------
        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine();

        System.out.print("Enter last name: ");
        String lastName = scanner.nextLine();

        Login login = new Login(firstName, lastName);

        String username;
        String password;
        String cell;

        // USERNAME registration loop
        do {
            System.out.print("Enter username: ");
            username = scanner.nextLine();
            System.out.println(login.registerUsername(username));
        } while (!login.checkUserName(username));

        // PASSWORD registration loop
        do {
            System.out.print("Enter password: ");
            password = scanner.nextLine();
            System.out.println(login.registerPassword(password));
        } while (!login.checkPasswordComplexity(password));

        // CELL NUMBER registration loop
        do {
            System.out.print("Enter cell phone number (e.g. +27831234567): ");
            cell = scanner.nextLine();
            System.out.println(login.registerCellPhoneNumber(cell));
        } while (!login.checkCellPhoneNumber(cell));

        // LOGIN loop — must succeed before proceeding
        System.out.println("\n--- Login ---");
        String loginUser;
        String loginPass;
        do {
            System.out.print("Enter username: ");
            loginUser = scanner.nextLine();
            System.out.print("Enter password: ");
            loginPass = scanner.nextLine();
            System.out.println(login.returnLoginStatus(loginUser, loginPass));
        } while (!login.loginUser(loginUser, loginPass));

        // -------------------------------------------------------
        // PART 3 — Load stored messages from JSON right after login
        // -------------------------------------------------------
        Message.loadStoredMessages();

        // -------------------------------------------------------
        // PART 2 — WELCOME AND MESSAGE COUNT
        // -------------------------------------------------------
        System.out.println("\nWelcome to QuickChat.");

        int numMessages = 0;
        while (numMessages <= 0) {
            System.out.print("How many messages would you like to send? ");
            try {
                numMessages = Integer.parseInt(scanner.nextLine().trim());
                if (numMessages <= 0) {
                    System.out.println("Please enter a number greater than 0.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a whole number.");
            }
        }

        // -------------------------------------------------------
        // MAIN MENU — WHILE LOOP (runs until user selects Quit)
        // Part 3 adds option 4 to the existing 3 options
        // -------------------------------------------------------
        boolean running = true;
        while (running) {
            System.out.println("\n--- Menu ---");
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.println("4) Stored Messages");
            System.out.print("Choose an option: ");

            String menuChoice = scanner.nextLine().trim();

            switch (menuChoice) {
                case "1":
                    sendMessages(scanner, numMessages);
                    break;
                case "2":
                    System.out.println("Coming Soon.");
                    break;
                case "3":
                    running = false;
                    System.out.println("\nTotal messages sent: "
                            + Message.returnTotalMessagess());
                    System.out.println("Goodbye!");
                    break;
                case "4":
                    storedMessagesMenu(scanner);
                    break;
                default:
                    System.out.println("Invalid option. Please choose 1, 2, 3, or 4.");
            }
        }

        scanner.close();
    }

    // -------------------------------------------------------
    // Sends messages using a FOR LOOP for the set number
    // -------------------------------------------------------

    /**
     * Handles the send messages feature using a for loop.
     * Validates recipient and message length before processing.
     * @param scanner    the input scanner
     * @param numMessages number of messages to send this session
     */
    private static void sendMessages(Scanner scanner, int numMessages) {
        for (int i = 1; i <= numMessages; i++) {
            System.out.println("\n--- Message " + i + " of " + numMessages + " ---");

            // Validate recipient
            String recipient = "";
            while (true) {
                System.out.print("Enter recipient cell number (e.g. +27123456789): ");
                recipient = scanner.nextLine().trim();
                Message temp = new Message(i, recipient, "placeholder");
                String recipientResult = temp.checkRecipientCell();
                System.out.println(recipientResult);
                if (recipientResult.equals("Cell phone number successfully captured.")) {
                    break;
                }
            }

            // Validate message length
            String messageText = "";
            while (true) {
                System.out.print("Enter your message (max 250 characters): ");
                messageText = scanner.nextLine();
                Message temp = new Message(i, recipient, messageText);
                String lengthResult = temp.checkMessageLength();
                if (lengthResult.equals("Message ready to send.")) {
                    break;
                }
                System.out.println("Please enter a message of less than 250 characters.");
                System.out.println(lengthResult);
            }

            // Create final message object and display details
            Message msg = new Message(i, recipient, messageText);
            System.out.println("\n--- Message Details ---");
            System.out.println("Message ID:   " + msg.getMessageID());
            System.out.println("Message Hash: " + msg.createMessageHash());
            System.out.println("Recipient:    " + msg.getRecipient());
            System.out.println("Message:      " + msg.getMessageText());

            // Send / Disregard / Store choice
            System.out.println("\n1) Send Message");
            System.out.println("2) Disregard Message");
            System.out.println("3) Store Message");
            System.out.print("Choose: ");

            int sendChoice = 0;
            while (sendChoice < 1 || sendChoice > 3) {
                try {
                    sendChoice = Integer.parseInt(scanner.nextLine().trim());
                    if (sendChoice < 1 || sendChoice > 3) {
                        System.out.println("Please enter 1, 2, or 3.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter 1, 2, or 3.");
                }
            }
            System.out.println(msg.SentMessage(sendChoice));
        }

        // Display report and total after all messages processed
        System.out.println("\n" + Message.printMessages());
        System.out.println("Total messages sent: " + Message.returnTotalMessagess());
    }

    // -------------------------------------------------------
    // PART 3 — Stored Messages sub-menu with 6 options
    // -------------------------------------------------------

    /**
     * Displays the Stored Messages sub-menu and handles the 6 options.
     * All logic is delegated to methods in Message.java.
     * @param scanner the input scanner
     */
    private static void storedMessagesMenu(Scanner scanner) {
        boolean inSubMenu = true;
        while (inSubMenu) {
            System.out.println("\n--- Stored Messages ---");
            System.out.println("a) Display all stored messages");
            System.out.println("b) Display longest message");
            System.out.println("c) Search by message ID");
            System.out.println("d) Search by recipient");
            System.out.println("e) Delete by message hash");
            System.out.println("f) Display full report");
            System.out.println("q) Back to main menu");
            System.out.print("Choose: ");

            String subChoice = scanner.nextLine().trim().toLowerCase();

            switch (subChoice) {
                case "a":
                    System.out.println(Message.displayStoredMessages());
                    break;
                case "b":
                    System.out.println(Message.displayLongestMessage());
                    break;
                case "c":
                    System.out.print("Enter message ID to search: ");
                    String searchID = scanner.nextLine().trim();
                    System.out.println(Message.searchByMessageID(searchID));
                    break;
                case "d":
                    System.out.print("Enter recipient number to search: ");
                    String searchRecipient = scanner.nextLine().trim();
                    System.out.println(Message.searchByRecipient(searchRecipient));
                    break;
                case "e":
                    System.out.print("Enter message hash to delete: ");
                    String deleteHash = scanner.nextLine().trim();
                    System.out.println(Message.deleteByHash(deleteHash));
                    break;
                case "f":
                    System.out.println(Message.printMessages());
                    break;
                case "q":
                    inSubMenu = false;
                    break;
                default:
                    System.out.println("Invalid option. Choose a-f or q.");
            }
        }
    }
}
