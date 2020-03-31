package best.aog.chat.client.model.messages;

import best.aog.chat.client.model.User;
import best.aog.chat.client.model.messages.client.AuthorizeMessageBody;
import best.aog.chat.client.model.messages.client.RegisterMessageBody;
import best.aog.chat.client.model.messages.client.RegularMessageBody;
import best.aog.chat.client.model.messages.server.ResultMessageBody;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Messages {
    private final static Gson gson = new Gson();

    public static String createRegularMessage(User user, String message, User[] receivers) {
        RegularMessageBody regBody = new RegularMessageBody(user, message, null);
        Message regularMessage = new Message(MessageType.REGULAR_MESSAGE, regBody);
        return gson.toJson(regularMessage);
    }

    public static String createRegisterMessage(User user) {
        RegisterMessageBody registerMessageBody = new RegisterMessageBody(user);
        Message registerMessage = new Message(MessageType.REGISTER_MESSAGE, registerMessageBody);
        return gson.toJson(registerMessage);
    }

    public static String createAuthorizeMessage(String login, String password) {
        AuthorizeMessageBody authorizeMessageBody = new AuthorizeMessageBody(login, password);
        Message registerMessage = new Message(MessageType.AUTHORIZE_MESSAGE, authorizeMessageBody);
        return gson.toJson(registerMessage);
    }

    public static Message parseMessage(String message) {
        JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();
        MessageType messageType = gson.fromJson(jsonObject.get("messageType"), MessageType.class);

        if (messageType == MessageType.RESULT_MESSAGE || messageType == MessageType.AUTHORIZE_MESSAGE) {
            ResultMessageBody resBody = gson.fromJson(jsonObject.get("messageBody"), ResultMessageBody.class);
            return new Message(messageType, resBody);
        } else if (messageType == MessageType.REGULAR_MESSAGE) {
            RegularMessageBody regBody = gson.fromJson(jsonObject.get("messageBody"), RegularMessageBody.class);
            return new Message(messageType, regBody);
        }
        return null;
    }
}
