package message;

public enum MessageType {
    MSG("MSG"),
    REQUEST("REQUEST"),
    CMD("CMD"),
    QUIT("QUIT"),
    PING("PING"),
    ACK("ACK");

    private final String type;

    MessageType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static MessageType fromString(String text) {
        for (MessageType b : MessageType.values()) {
            if (b.type.equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }
}
