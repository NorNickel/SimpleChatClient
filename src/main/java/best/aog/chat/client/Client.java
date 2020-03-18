package best.aog.chat.client;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@Setter
@Getter
public class Client implements Serializable {
    private String login;
    private String password;

    public String toJSON() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
