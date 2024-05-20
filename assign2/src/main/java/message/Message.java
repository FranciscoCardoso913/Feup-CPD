package message;

/**
 * Class representing a message with a header and body.
 */
public class Message {

    private MessageType header;
    private String body;

    /**
     * Constructor for the Message class.
     *
     * @param header The MessageType enum representing the message header.
     * @param body   The string representing the message body.
     */
    public Message(MessageType header, String body) {
        this.header = header;
        this.body = body;
    }

    /**
     * Getter for the message header.
     *
     * @return The MessageType enum representing the message header.
     */
    public MessageType getHeader() {
        return this.header;
    }

    /**
     * Getter for the message body.
     *
     * @return The string representing the message body.
     */
    public String getBody() {
        return this.body;
    }

    /**
     * Method to check if the message type matches a specified type.
     *
     * @param type The MessageType enum to compare against.
     * @return true if the message type matches the specified type, false otherwise.
     */
    public boolean isType(MessageType type) {
        return type.getType().equals(header.getType());
    }

    /**
     * Retrieves the type of the message.
     * 
     * @return The MessageType enumeration value.
     */
    public MessageType getType() {
        return header;
    }
}
