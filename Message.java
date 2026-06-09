package com.mycompany.chatapppart1;

import java.util.ArrayList;
import java.util.Random;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Message class for the QuickChat application.
 *
 * Part 2: message creation, validation, hashing, send/store/discard.
 * Part 3: five parallel arrays, JSON reading, search, delete, report.
 *
 * JSON write/read attribution: org.json library -
 * https://mvnrepository.com/artifact/org.json/json
 */
public class Message {

    // -------------------------------------------------------
    // Instance fields — one message's data
    // -------------------------------------------------------
    private String messageID;
    private int    messageNumber;
    private String recipient;
    private String messageText;

    // -------------------------------------------------------
    // Part 3 — Five static parallel arrays
    // Shared across all Message objects for the whole session.
    // -------------------------------------------------------
    private static ArrayList<String> sentMessages        = new ArrayList<>();
    private static ArrayList<String> disregardedMessages = new ArrayList<>();
    private static ArrayList<String> storedMessages      = new ArrayList<>();
    private static ArrayList<String> messageHashes       = new ArrayList<>();
    private static ArrayList<String> messageIDs          = new ArrayList<>();

    // Tracks recipient per sent message — used by searchByRecipient and report
    private static ArrayList<String> recipientList = new ArrayList<>();

    // -------------------------------------------------------
    // Constructor
    // -------------------------------------------------------

    /**
     * Creates a new Message with a randomly generated 10-digit ID.
     * @param messageNumber position of this message in the session
     * @param recipient     the recipient cell number
     * @param messageText   the body of the message
     */
    public Message(int messageNumber, String recipient, String messageText) {
        this.messageNumber = messageNumber;
        this.recipient     = recipient;
        this.messageText   = messageText;
        this.messageID     = generateMessageID();
    }

    // -------------------------------------------------------
    // Generates a random 10-digit numeric message ID
    // -------------------------------------------------------
    private String generateMessageID() {
        Random rand = new Random();
        StringBuilder id = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            id.append(rand.nextInt(10));
        }
        return id.toString();
    }

    // -------------------------------------------------------
    // VALIDATION METHODS
    // -------------------------------------------------------

    /**
     * Checks message ID is not more than 10 characters.
     * @return true if valid
     */
    public boolean checkMessageID() {
        return messageID.length() <= 10;
    }

    /**
     * Validates recipient cell: must start with "+" and be <= 10 chars.
     * @return success or failure message
     */
    public String checkRecipientCell() {
        if (recipient.length() <= 12 && recipient.startsWith("+27")) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not contain "
             + "an international code. Please correct the number and try again.";
    }

    /**
     * Validates message text does not exceed 250 characters.
     * @return "Message ready to send." or error with excess count
     */
    public String checkMessageLength() {
        if (messageText.length() <= 250) {
            return "Message ready to send.";
        }
        int excess = messageText.length() - 250;
        return "Message exceeds 250 characters by " + excess
             + "; please reduce the size.";
    }

    // -------------------------------------------------------
    // HASH METHOD
    // -------------------------------------------------------

    /**
     * Creates the message hash using string manipulation.
     * Format: first 2 digits of ID : message number : FIRSTWORDLASTWORD
     * All uppercase. Example: 00:1:HITONIGHT
     * @return the formatted hash string
     */
    public String createMessageHash() {
        String firstTwo  = messageID.substring(0, 2);
        String[] words   = messageText.trim().split("\\s+");
        String firstWord = words[0].toUpperCase().replaceAll("[^A-Z0-9]", "");
        String lastWord  = words[words.length - 1].toUpperCase()
                                                   .replaceAll("[^A-Z0-9]", "");
        return (firstTwo + ":" + messageNumber + ":" + firstWord + lastWord)
               .toUpperCase();
    }

    // -------------------------------------------------------
    // SENT MESSAGE — populates the correct array per choice
    // -------------------------------------------------------

    /**
     * Processes the user's choice: Send (1), Disregard (2), or Store (3).
     * Populates the appropriate parallel arrays based on the choice.
     *
     * Send:      adds to sentMessages, messageHashes, messageIDs, recipientList
     * Disregard: adds to disregardedMessages only
     * Store:     writes to JSON file; messageHashes and messageIDs still updated.
     *            storedMessages is populated by loadStoredMessages() at startup.
     *
     * @param choice 1=Send, 2=Disregard, 3=Store
     * @return result message string
     */
    public String SentMessage(int choice) {
        String hash = createMessageHash();
        switch (choice) {
            case 1:
                sentMessages.add(messageText);
                messageHashes.add(hash);
                messageIDs.add(messageID);
                recipientList.add(recipient);
                return "Message successfully sent.";
            case 2:
                disregardedMessages.add(messageText);
                return "Press 0 to delete the message.";
            case 3:
                messageHashes.add(hash);
                messageIDs.add(messageID);
                storeMessage();
                return "Message successfully stored.";
            default:
                return "Invalid option selected.";
        }
    }

    // -------------------------------------------------------
    // PART 3 SECTION 3 — Display the longest stored message
    // Important: searches storedMessages, NOT sentMessages
    // -------------------------------------------------------

    /**
     * Finds and returns the longest message in the storedMessages array.
     * Loops through each message and tracks the longest found so far.
     * @return the longest stored message, or a message if none exist
     */
    public static String displayLongestMessage() {
        String longest = "";
        for (int i = 0; i < storedMessages.size(); i++) {
            if (storedMessages.get(i).length() > longest.length()) {
                longest = storedMessages.get(i);
            }
        }
        if (longest.isEmpty()) {
            return "No stored messages found.";
        }
        return longest;
    }

    // -------------------------------------------------------
    // PART 3 SECTION 4 — Search and Delete
    // -------------------------------------------------------

    /**
     * Searches messageIDs array for a match and returns the message
     * at the same index from sentMessages (parallel array search).
     * POE: searching 0838884567 must return "It is dinner time!"
     * @param id the message ID to search for
     * @return matching message text, or "Message not found."
     */
    public static String searchByMessageID(String id) {
        for (int i = 0; i < messageIDs.size(); i++) {
            if (messageIDs.get(i).equals(id)) {
                // Return from sentMessages if exists, else check storedMessages
                if (i < sentMessages.size()) {
                    return sentMessages.get(i);
                }
            }
        }
        return "Message not found.";
    }

    /**
     * Searches for ALL messages sent to a given recipient.
     * Collects every match since there may be more than one result.
     * POE: searching +27838884567 must return both message 2 and message 5.
     * @param recipient the cell number to search for
     * @return all matching messages, or "No messages found."
     */
    public static String searchByRecipient(String recipient) {
        StringBuilder results = new StringBuilder();
        for (int i = 0; i < recipientList.size(); i++) {
            if (recipientList.get(i).equals(recipient)) {
                results.append(sentMessages.get(i)).append("\n");
            }
        }
        // Also check storedMessages for that recipient (loaded from JSON)
        for (int i = 0; i < storedMessages.size(); i++) {
            results.append(storedMessages.get(i)).append("\n");
        }
        if (results.length() == 0) {
            return "No messages found for that recipient.";
        }
        return results.toString().trim();
    }

    /**
     * Finds the hash in messageHashes and removes it along with the
     * corresponding entries in all parallel arrays.
     * Breaks out of loop immediately after removal to avoid index errors.
     * POE: delete message 2's hash returns
     *      "Message: Where are you? You are late! I have asked you to be on time successfully deleted."
     * @param hash the message hash to delete
     * @return success message, or "Hash not found."
     */
    public static String deleteByHash(String hash) {
        for (int i = 0; i < messageHashes.size(); i++) {
            if (messageHashes.get(i).equals(hash)) {
                String deletedText = "";
                if (i < sentMessages.size()) {
                    deletedText = sentMessages.get(i);
                    sentMessages.remove(i);
                } else if (i < storedMessages.size()) {
                    deletedText = storedMessages.get(i);
                    storedMessages.remove(i);
                }
                messageHashes.remove(i);
                if (i < messageIDs.size())    messageIDs.remove(i);
                if (i < recipientList.size()) recipientList.remove(i);
                // Break immediately to avoid index out of bounds
                return "Message: " + deletedText + " successfully deleted.";
            }
        }
        return "Hash not found.";
    }

    // -------------------------------------------------------
    // PART 3 SECTION 5 — Load stored messages from JSON file
    // Attribution: org.json library -
    // https://mvnrepository.com/artifact/org.json/json
    // Called once at startup, right after login, before menu.
    // -------------------------------------------------------

    /**
     * Reads messages.json line by line and loads each stored message
     * text into the storedMessages array.
     * If no file exists, continues without crashing.
     */
    public static void loadStoredMessages() {
        try (BufferedReader reader = new BufferedReader(
                new FileReader("messages.json"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                // Extract "message": "text" field from each JSON line
                if (line.startsWith("\"message\"")) {
                    int start = line.indexOf(": \"") + 3;
                    int end   = line.lastIndexOf("\"");
                    if (start > 2 && end > start) {
                        storedMessages.add(line.substring(start, end));
                    }
                }
            }
        } catch (IOException e) {
            // No file yet — continue without crashing
            System.out.println("No stored messages file found. Starting fresh.");
        }
    }

    // -------------------------------------------------------
    // PART 3 SECTION 6 — Display Message Report
    // Uses parallel arrays: messageHashes, recipientList, sentMessages
    // -------------------------------------------------------

    /**
     * Builds a formatted report of all sent messages.
     * Uses the same loop index to retrieve hash, recipient, and message
     * from the parallel arrays for each entry.
     * @return formatted report string
     */
    public static String printMessages() {
        StringBuilder report = new StringBuilder();
        report.append("=== Message Report ===\n");
        for (int i = 0; i < sentMessages.size(); i++) {
            report.append("-----------------------------\n");
            report.append("Message Hash: ")
                  .append(i < messageHashes.size() ? messageHashes.get(i) : "N/A")
                  .append("\n");
            report.append("Recipient:    ")
                  .append(i < recipientList.size() ? recipientList.get(i) : "N/A")
                  .append("\n");
            report.append("Message:      ")
                  .append(sentMessages.get(i))
                  .append("\n");
        }
        if (sentMessages.isEmpty()) {
            report.append("No messages sent yet.\n");
        }
        return report.toString();
    }

    // -------------------------------------------------------
    // Display all stored messages — sub-menu option a
    // -------------------------------------------------------

    /**
     * Returns a formatted list of all messages in the storedMessages array.
     * @return formatted string of stored messages
     */
    public static String displayStoredMessages() {
        if (storedMessages.isEmpty()) {
            return "No stored messages.";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < storedMessages.size(); i++) {
            sb.append("Stored Message ").append(i + 1)
              .append(": ").append(storedMessages.get(i)).append("\n");
        }
        return sb.toString();
    }

    // -------------------------------------------------------
    // STORE TO JSON — Part 2 write operation
    // Attribution: org.json library -
    // https://mvnrepository.com/artifact/org.json/json
    // -------------------------------------------------------

    /**
     * Stores this message as a JSON entry in messages.json.
     * Appends to the file if it already exists.
     */
    public void storeMessage() {
        try {
            java.io.File file = new java.io.File("messages.json");
            ArrayList<String> lines = new ArrayList<>();

            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
                reader.close();
                // Remove closing bracket to append new entry
                if (!lines.isEmpty()
                        && lines.get(lines.size() - 1).trim().equals("]")) {
                    lines.remove(lines.size() - 1);
                }
                // Add comma after last closing brace
                for (int i = lines.size() - 1; i >= 0; i--) {
                    if (!lines.get(i).trim().isEmpty()) {
                        if (!lines.get(i).trim().endsWith(",")) {
                            lines.set(i, lines.get(i) + ",");
                        }
                        break;
                    }
                }
            } else {
                lines.add("[");
            }

            lines.add("  {");
            lines.add("    \"messageID\": \""   + messageID           + "\",");
            lines.add("    \"messageNumber\": " + messageNumber       + ",");
            lines.add("    \"recipient\": \""   + recipient           + "\",");
            lines.add("    \"message\": \""     + messageText         + "\",");
            lines.add("    \"messageHash\": \"" + createMessageHash() + "\"");
            lines.add("  }");
            lines.add("]");

            PrintWriter writer = new PrintWriter(new FileWriter(file));
            for (String l : lines) writer.println(l);
            writer.close();

        } catch (IOException e) {
            System.out.println("Error storing message: " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // Returns total messages sent (used in main menu)
    // -------------------------------------------------------

    /**
     * Returns the total number of messages sent this session.
     * @return count of sent messages
     */
    public static int returnTotalMessagess() {
        return sentMessages.size();
    }

    // -------------------------------------------------------
    // Package-private getters used by unit tests
    // -------------------------------------------------------
    public static ArrayList<String> getSentMessages()        { return sentMessages;        }
    public static ArrayList<String> getStoredMessages()      { return storedMessages;      }
    public static ArrayList<String> getDisregardedMessages() { return disregardedMessages; }
    public static ArrayList<String> getMessageHashes()       { return messageHashes;       }
    public static ArrayList<String> getMessageIDs()          { return messageIDs;          }
    public static ArrayList<String> getRecipientList()       { return recipientList;       }

    /** Clears all static arrays — used by unit tests in @BeforeEach */
    public static void clearAllArrays() {
        sentMessages.clear();
        disregardedMessages.clear();
        storedMessages.clear();
        messageHashes.clear();
        messageIDs.clear();
        recipientList.clear();
    }

    // Instance getters
    public String getMessageID()     { return messageID;     }
    public String getRecipient()     { return recipient;     }
    public String getMessageText()   { return messageText;   }
    public int    getMessageNumber() { return messageNumber; }
}
