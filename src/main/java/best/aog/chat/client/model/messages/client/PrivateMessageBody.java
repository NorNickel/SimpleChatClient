package best.aog.chat.client.model.messages.client;

import best.aog.chat.client.model.messages.MessageBody;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PrivateMessageBody extends MessageBody {
    private String userName;
    private String message;
    private String receiverUserName;
}