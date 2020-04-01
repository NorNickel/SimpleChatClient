package best.aog.chat.client.model.messages.server;

import best.aog.chat.client.model.messages.MessageBody;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AllUsersMessageBody extends MessageBody {
    private String[] login;
}
