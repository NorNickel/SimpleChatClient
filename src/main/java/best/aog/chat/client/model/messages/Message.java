package best.aog.chat.client.model.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Message {
    private MessageType messageType;
    private MessageBody messageBody;
}