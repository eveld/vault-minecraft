package com.hashicorp.vault.vault;

import java.io.Serializable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.hashicorp.vault.Mod;

public class Decrypted implements Serializable {
  public class Data {
    public String plaintext;
  }

  public Data data;

  public Decrypted() {
  }

  public byte[] toBytes() {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    String json = gson.toJson(this);
    return json.getBytes();
  }

  public static Decrypted fromBytes(byte[] data) {
    try {
      GsonBuilder builder = new GsonBuilder();
      builder.registerTypeAdapter(Decrypted.class, new EncryptedDataCreator());

      String json = new String(data);

      Gson gson = builder.create();
      Decrypted state = gson.fromJson(json, Decrypted.class);

      return state;
    } catch (JsonSyntaxException e) {
      e.printStackTrace();

      String json = new String(data);
      Mod.LOGGER.error("Unable to create Decrypted from JSON:" + json);
      return null;
    }
  }
}
