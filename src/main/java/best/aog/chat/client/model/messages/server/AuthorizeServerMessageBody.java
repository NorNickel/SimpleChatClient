package best.aog.chat.client.model.messages.server;

import best.aog.chat.client.model.User;
import best.aog.chat.client.model.messages.MessageBody;
import best.aog.chat.client.model.messages.MessageResultType;

public class AuthorizeServerMessageBody extends MessageBody {
    private MessageResultType result;
    private User user;
    private String errorMessage;
}
