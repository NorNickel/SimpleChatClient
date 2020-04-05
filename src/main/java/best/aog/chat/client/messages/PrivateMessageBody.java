package best.aog.chat.client.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PrivateMessageBody extends MessageBody {
    private String userName;
    private String message;
    private String receiverUserName;
}