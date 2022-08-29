package com.hashicorp.vault.vault;

import java.io.Serializable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.hashicorp.vault.Mod;

public class Encrypted implements Serializable {
  public class Data {
    public String ciphertext;
  }

  public Data data;

  public Encrypted() {
  }

  public byte[] toBytes() {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    String json = gson.toJson(this);
    return json.getBytes();
  }

  public static Encrypted fromBytes(byte[] data) {
    try {
      GsonBuilder builder = new GsonBuilder();
      builder.registerTypeAdapter(Encrypted.class, new EncryptedDataCreator());

      String json = new String(data);

      Gson gson = builder.create();
      Encrypted state = gson.fromJson(json, Encrypted.class);

      return state;
    } catch (JsonSyntaxException e) {
      e.printStackTrace();

      String json = new String(data);
      Mod.LOGGER.error("Unable to create Encrypted from JSON:" + json);
      return null;
    }
  }
}
