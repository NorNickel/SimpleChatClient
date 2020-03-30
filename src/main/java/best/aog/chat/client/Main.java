package best.aog.chat.client;

import best.aog.chat.client.view.GUI;

import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import java.util.Collections;
import java.util.Map;

public class Main {
    //private Connection connection;
    private GUI gui;

    public Main() {
        /*
        RegularMessageBody body = new RegularMessageBody();
        body.setUser(new User("aaa", "bbb"));
        User[] receivers = {new User("a1", "a1"), new User("a2", "a2")};
        body.setReceivers(receivers);
        body.setMessage("Hello");
        Message message = new Message(MessageType.REGULAR_MESSAGE, body);

        Gson gson = new Gson();
        String jsonmessage = gson.toJson(message);
        System.out.println(jsonmessage);

         */
        final Map<String, ?> config = Collections.emptyMap();
        JsonBuilderFactory factory = Json.createBuilderFactory(null);
        JsonObject value = factory.createObjectBuilder()
                .add("firstName", "John")
                .add("lastName", "Smith")
                .add("age", 25)
                .add("address", factory.createObjectBuilder()
                        .add("streetAddress", "21 2nd Street")
                        .add("city", "New York")
                        .add("state", "NY")
                        .add("postalCode", "10021"))
                .add("phoneNumber", factory.createArrayBuilder()
                        .add(factory.createObjectBuilder()
                                .add("type", "home")
                                .add("number", "212 555-1234"))
                        .add(factory.createObjectBuilder()
                                .add("type", "fax")
                                .add("number", "646 555-4567")))
                .build();
        System.out.println(value);


        //connection = new Connection();
        //connection.connectToServer();
        gui = new GUI();
        gui.main(new String[0]);
    }

    public static void main(String[] args) {
        Main main = new Main();
    }
}
/*

1) properties
2) logger to file (server + db)
3) history
4) gui
5) site

 */