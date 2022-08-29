package com.hashicorp.vault.block.entity;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Base64;

import com.github.hashicraft.stateful.blocks.StatefulBlockEntity;
import com.github.hashicraft.stateful.blocks.Syncable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hashicorp.vault.Mod;
import com.hashicorp.vault.vault.Decrypted;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class LockEntity extends StatefulBlockEntity {
  public static final String vaultAddress = System.getenv().getOrDefault("VAULT_ADDR", "http://localhost:8200");
  public static final String vaultToken = System.getenv().getOrDefault("VAULT_TOKEN", "root");

  @Syncable
  private String policy = "default";

  public LockEntity(BlockPos pos, BlockState state) {
    super(BlockEntities.LOCK_ENTITY, pos, state, null);
  }

  public LockEntity(BlockPos pos, BlockState state, Block parent) {
    super(BlockEntities.LOCK_ENTITY, pos, state, parent);
  }

  public boolean checkAccess(String token, String policy) {
    try {
      HttpClient client = HttpClient.newHttpClient();
      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(vaultAddress + "/v1/secret/data/minecraft/" + policy))
          .header("Accept", "application/json")
          .header("X-Vault-Token", token)
          .GET()
          .build();

      HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
      if (response.statusCode() >= 400) {
        Mod.LOGGER.warn(response.body());
        return false;
      }

      Mod.LOGGER.info(response.body());

      return true;
    } catch (Exception e) {
      Mod.LOGGER.warn(e.getStackTrace().toString());
      return false;
    }
  }

  public boolean verify(String input, String signature) {
    try {
      String payload = String.format("""
          {
            "hash_algorithm":"sha2-256",
            "signature_algorithm":"pkcs1v15",
            "input":"%s",
            "signature": "%s"
          }
          """, Base64.getEncoder().encodeToString(input.getBytes()), signature);

      HttpClient client = HttpClient.newHttpClient();
      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(vaultAddress + "/v1/transit/verify/minecraft"))
          .header("Accept", "application/json")
          .header("X-Vault-Token", vaultToken)
          .POST(HttpRequest.BodyPublishers.ofString(payload))
          .build();

      HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
      if (response.statusCode() >= 400) {
        Mod.LOGGER.warn(response.body());
        return false;
      }

      Mod.LOGGER.info(response.body());

      return true;
    } catch (Exception e) {
      Mod.LOGGER.warn(e.getStackTrace().toString());
      return false;
    }
  }

  public String decrypt(String input) {
    try {
      String payload = String.format("""
          {
            "ciphertext": "%s"
          }
          """, input);

      HttpClient client = HttpClient.newHttpClient();
      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(vaultAddress + "/v1/transit/decrypt/minecraft"))
          .header("Accept", "application/json")
          .header("X-Vault-Token", vaultToken)
          .POST(HttpRequest.BodyPublishers.ofString(payload))
          .build();

      HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
      if (response.statusCode() >= 400) {
        Mod.LOGGER.warn(response.body());
        return null;
      }

      Mod.LOGGER.info(response.body());

      GsonBuilder builder = new GsonBuilder();
      Gson gson = builder.create();
      Decrypted decrypted = gson.fromJson(response.body(), Decrypted.class);
      return decrypted.data.plaintext;
    } catch (Exception e) {
      Mod.LOGGER.warn(e.getStackTrace().toString());
      return null;
    }
  }

  public void setPolicy(String policy) {
    this.policy = policy;
    this.markForUpdate();
  }

  public String getPolicy() {
    return this.policy;
  }
}
