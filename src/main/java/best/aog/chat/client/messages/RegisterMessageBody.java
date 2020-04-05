package best.aog.chat.client.messages;

import best.aog.chat.client.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RegisterMessageBody extends MessageBody {
    private User user;
}