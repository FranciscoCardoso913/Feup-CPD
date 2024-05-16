package message;

public class Message {
    private MessageType header;
    private String body;

    public Message(MessageType header, String body){
        this.header = header;
        this.body = body;
    }
     
    public MessageType getHeader(){
        return this.header;
    }

    public String getBody(){
        return this.body;
    }

    public boolean isType(MessageType type) {
        return type.getType().equals(header.getType());
    }
}
