package com.mycompany.chatapppart1;

import com.mycompany.chatapppart1.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * MessageTest — Unit tests for the Message class.
 *
 * Contains all Part 2 tests AND the six new Part 3 tests.
 *
 * Test Data (5 messages):
 *   Message 1: +27834557896  "Did you get the cake?"                        FLAG: Sent
 *   Message 2: +27838884567  "Where are you? You are late! I have asked you to be on time."  FLAG: Stored
 *   Message 3: +27834484567  "Yohoooo, I am at your gate."                  FLAG: Disregard
 *   Message 4: 0838884567    "It is dinner time!"                            FLAG: Sent
 *   Message 5: +27838884567  "Ok, I am leaving without you."                FLAG: Stored
 */
public class MessageTest {

    // -------------------------------------------------------
    //  test data 
    // -------------------------------------------------------
    private static final String RECIPIENT_1 = "+27834557896";
    private static final String RECIPIENT_2 = "+27838884567";
    private static final String RECIPIENT_3 = "+27834484567";
    private static final String RECIPIENT_4 = "0838884567";
    private static final String RECIPIENT_5 = "+27838884567";

    private static final String MSG_1 = "Did you get the cake?";
    private static final String MSG_2 = "Where are you? You are late! I have asked you to be on time.";
    private static final String MSG_3 = "Yohoooo, I am at your gate.";
    private static final String MSG_4 = "It is dinner time!";
    private static final String MSG_5 = "Ok, I am leaving without you.";

    // -------------------------------------------------------
    // @BeforeEach — clears arrays and populates 
    // -------------------------------------------------------
    @BeforeEach
    public void setUp() {
        // Clear all static arrays before every test
        Message.clearAllArrays();

        // Message 1 — Sent
        Message m1 = new Message(1, RECIPIENT_1, MSG_1);
        m1.SentMessage(1);

        // Message 2 — Stored (written to JSON; storedMessages loaded separately)
        Message m2 = new Message(2, RECIPIENT_2, MSG_2);
        m2.SentMessage(3);
        // Manually add to storedMessages to simulate loadStoredMessages()
        Message.getStoredMessages().add(MSG_2);

        // Message 3 — Disregarded
        Message m3 = new Message(3, RECIPIENT_3, MSG_3);
        m3.SentMessage(2);

        // Message 4 — Sent (developer number — no international code, but still stored)
        Message m4 = new Message(4, RECIPIENT_4, MSG_4);
        m4.SentMessage(1);

        // Message 5 — Stored
        Message m5 = new Message(5, RECIPIENT_5, MSG_5);
        m5.SentMessage(3);
        // Manually add to storedMessages to simulate loadStoredMessages()
        Message.getStoredMessages().add(MSG_5);
    }

    // =======================================================
    // PART 2 TESTS 
    // =======================================================

    // --- Message length tests ---

    @Test
    public void testMessageLengthSuccess() {
        Message msg = new Message(1, "+271234567", "Hi there");
        assertEquals("Message ready to send.", msg.checkMessageLength());
    }

    @Test
    public void testMessageLengthFailure() {
        String longText = "A".repeat(260);
        Message msg = new Message(1, "+271234567", longText);
        assertTrue(msg.checkMessageLength().contains("exceeds 250 characters by 10"));
    }

    // --- Recipient tests ---

    @Test
    public void testCheckRecipientCellSuccess() {
        Message msg = new Message(1, "+271234567", "Test");
        assertEquals("Cell phone number successfully captured.",
                msg.checkRecipientCell());
    }

    @Test
    public void testCheckRecipientCellFailure() {
        Message msg = new Message(1, "08575975889", "Test");
        assertEquals(
            "Cell phone number is incorrectly formatted or does not contain "
          + "an international code. Please correct the number and try again.",
            msg.checkRecipientCell()
        );
    }

    // --- Hash tests ---

    @Test
    public void testMessageHashHasThreeParts() {
        Message msg = new Message(1, "+271234567", "Hi Mike, can you join us for dinner tonight?");
        assertEquals(3, msg.createMessageHash().split(":").length);
    }

    @Test
    public void testMessageHashIsAllCaps() {
        Message msg = new Message(1, "+271234567", "Hi Mike, can you join us for dinner tonight?");
        String hash = msg.createMessageHash();
        assertEquals(hash.toUpperCase(), hash);
    }

    @Test
    public void testMessageHashFirstAndLastWord() {
        Message msg = new Message(1, "+271234567", "Hi Mike, can you join us for dinner tonight?");
        String thirdPart = msg.createMessageHash().split(":")[2];
        assertEquals("HITONIGHT", thirdPart);
    }

    // --- Message ID tests ---

    @Test
    public void testCheckMessageIDLength() {
        Message msg = new Message(1, "+271234567", "Test");
        assertTrue(msg.checkMessageID());
    }

    // --- SentMessage option tests ---

    @Test
    public void testSentMessageSend() {
        Message msg = new Message(1, "+271234567", "Hi test message here");
        assertEquals("Message successfully sent.", msg.SentMessage(1));
    }

    @Test
    public void testSentMessageDisregard() {
        Message msg = new Message(1, "+271234567", "Hi test message here");
        assertEquals("Press 0 to delete the message.", msg.SentMessage(2));
    }

    @Test
    public void testSentMessageStore() {
        Message msg = new Message(1, "+271234567", "Hi test message here");
        assertEquals("Message successfully stored.", msg.SentMessage(3));
    }

    // =======================================================
    // PART 3 TESTS — Six new tests using exact 
    // =======================================================

    /**
     * Test 1: sentMessages array correctly populated.
     * Messages 1 and 4 are flagged as Sent.
     *  expected: contains "Did you get the cake?" and "It is dinner time!"
     */
    @Test
    public void testSentMessagesArray_correctlyPopulated() {
        assertTrue(
            Message.getSentMessages().contains(MSG_1),
            "sentMessages should contain: Did you get the cake?"
        );
        assertTrue(
            Message.getSentMessages().contains(MSG_4),
            "sentMessages should contain: It is dinner time!"
        );
    }

    /**
     * Test 2: displayLongestMessage returns the correct message.
     * Uses storedMessages array (NOT sentMessages).
     *  expected: "Where are you? You are late! I have asked you to be on time."
     */
    @Test
    public void testDisplayLongestMessage_returnsCorrectMessage() {
        assertEquals(
            MSG_2,
            Message.displayLongestMessage()
        );
    }

    /**
     * Test 3: searchByMessageID returns the correct message.
     * Message 4 uses developer number 0838884567 as the ID search input.
     *  expected: "It is dinner time!"
     */
    @Test
    public void testSearchByMessageID_returnsCorrectMessage() {
        // Find the actual generated ID of message 4 from the messageIDs array
        // The POE says "0838884567" is the developer number used for searching
        // We test that searching message 4's ID returns its message text
        String idToSearch = Message.getMessageIDs().size() >= 2
                ? Message.getMessageIDs().get(1)  // index 1 = msg 4 (second sent message)
                : "";
        String result = Message.searchByMessageID(idToSearch);
        assertEquals(MSG_4, result);
    }

    /**
     * Test 4: searchByRecipient returns all matching messages.
     * Searching +27838884567 must return both message 2 and message 5.
     * POE expected: contains both
     *   "Where are you? You are late! I have asked you to be on time."
     *   "Ok, I am leaving without you."
     */
    @Test
    public void testSearchByRecipient_returnsAllMatchingMessages() {
        String result = Message.searchByRecipient(RECIPIENT_2);
        assertTrue(result.contains(MSG_2),
                "Result should contain message 2");
        assertTrue(result.contains(MSG_5),
                "Result should contain message 5");
    }

    /**
     * Test 5: deleteByHash removes the correct message.
     * Delete message 2 by its hash.
     * POE expected: "Message: Where are you? You are late! I have asked you
     *                to be on time successfully deleted."
     */
    @Test
    public void testDeleteByHash_removesCorrectMessage() {
        // Get message 2's hash from messageHashes array
        // Message 2 was stored (choice 3), so its hash is at index 0
        String hashToDelete = Message.getMessageHashes().size() > 0
                ? Message.getMessageHashes().get(0)
                : "";
        String result = Message.deleteByHash(hashToDelete);
        assertTrue(result.contains(MSG_2.substring(0, 20)),
                "Delete result should contain message 2 text");
        assertTrue(result.contains("successfully deleted"),
                "Delete result should confirm deletion");
    }

    /**
     * Test 6: printMessages report contains all required fields.
     * Report must contain hash, recipient, and message for all sent messages.
     */
    @Test
    public void testDisplayReport_containsRequiredFields() {
        String report = Message.printMessages();
        assertTrue(report.contains("Message Hash:"),
                "Report must contain Message Hash field");
        assertTrue(report.contains("Recipient:"),
                "Report must contain Recipient field");
        assertTrue(report.contains("Message:"),
                "Report must contain Message field");
        assertTrue(report.contains(MSG_1),
                "Report must contain message 1 text");
        assertTrue(report.contains(MSG_4),
                "Report must contain message 4 text");
    }
}
