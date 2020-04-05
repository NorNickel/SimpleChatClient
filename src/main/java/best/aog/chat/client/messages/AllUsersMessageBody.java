package best.aog.chat.client.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AllUsersMessageBody extends MessageBody {
    private String[] userName;
}
