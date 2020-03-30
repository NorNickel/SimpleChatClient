package best.aog.chat.client.model.messageN;

import best.aog.chat.client.model.messages.MessageType;
import lombok.Getter;

@Getter
public class Message {
    MessageType type;
    String data;

    public Message(MessageType type) {
        this.type = type;
        this.data = null;
    }

    public Message(MessageType type, String data) {
        this.type = type;
        this.data = data;
    }
}
