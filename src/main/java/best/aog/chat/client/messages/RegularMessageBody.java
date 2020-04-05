package best.aog.chat.client.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RegularMessageBody extends MessageBody {
    private String userName;
    private String message;
}