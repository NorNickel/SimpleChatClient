package best.aog.chat.client.model.messages.client;

import best.aog.chat.client.model.User;
import best.aog.chat.client.model.messages.MessageBody;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class RegularMessageBody extends MessageBody {
    private User user;
    private String message;
    private User[] receivers;
}