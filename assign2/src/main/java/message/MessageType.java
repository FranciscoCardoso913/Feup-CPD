package message;

/**
 * Enum representing different types of messages.
 */
public enum MessageType {
    MSG("MSG"),
    REQUEST("REQUEST"),
    CMD("CMD"),
    QUIT("QUIT"),
    PING("PING"),
    ACK("ACK");

    private final String type;

    /**
     * Constructor for MessageType enum.
     *
     * @param type The string representation of the message type.
     */
    MessageType(String type) {
        this.type = type;
    }

    /**
     * Getter for the string representation of the message type.
     *
     * @return The string representation of the message type.
     */
    public String getType() {
        return type;
    }

    /**
     * Method to get the MessageType enum from a string.
     *
     * @param text The string representation of the message type.
     * @return The corresponding MessageType enum.
     * @throws IllegalArgumentException if no constant with the specified text is found.
     */
    public static MessageType fromString(String text) {
        for (MessageType b : MessageType.values()) {
            if (b.type.equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }
}
