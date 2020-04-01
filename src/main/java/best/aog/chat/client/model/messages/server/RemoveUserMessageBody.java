package best.aog.chat.client.model.messages.server;

import best.aog.chat.client.model.messages.MessageBody;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RemoveUserMessageBody extends MessageBody {
    private String userName;
}