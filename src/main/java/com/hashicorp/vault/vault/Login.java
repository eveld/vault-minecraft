package com.hashicorp.vault.vault;

import java.io.Serializable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import com.hashicorp.vault.Mod;

public class Login implements Serializable {
  public class MetaData {
    public String username;
  }

  public class Auth {
    @SerializedName("client_token")
    public String token;

    public String[] policies;

    public MetaData metadata;
  }

  public Auth auth;

  public Login() {
  }

  public byte[] toBytes() {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    String json = gson.toJson(this);
    return json.getBytes();
  }

  public static Login fromBytes(byte[] data) {
    try {
      GsonBuilder builder = new GsonBuilder();
      builder.registerTypeAdapter(Login.class, new LoginDataCreator());

      String json = new String(data);

      Gson gson = builder.create();
      Login state = gson.fromJson(json, Login.class);

      return state;
    } catch (JsonSyntaxException e) {
      e.printStackTrace();

      String json = new String(data);
      Mod.LOGGER.error("Unable to create Login from JSON:" + json);
      return null;
    }
  }
}
