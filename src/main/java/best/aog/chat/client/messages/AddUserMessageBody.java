package best.aog.chat.client.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public class AddUserMessageBody extends MessageBody {
    private String userName;
}