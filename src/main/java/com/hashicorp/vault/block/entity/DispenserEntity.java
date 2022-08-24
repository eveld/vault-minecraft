package com.hashicorp.vault.block.entity;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import com.github.hashicraft.stateful.blocks.StatefulBlockEntity;
import com.github.hashicraft.stateful.blocks.Syncable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hashicorp.vault.Mod;
import com.hashicorp.vault.vault.Login;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointerImpl;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class DispenserEntity extends StatefulBlockEntity {
  public static final String vaultAddress = System.getenv().getOrDefault("VAULT_ADDR", "http://localhost:8200");
  public static final String vaultToken = System.getenv().getOrDefault("VAULT_TOKEN", "root");

  @Syncable
  private String policy = "default";

  public DispenserEntity(BlockPos pos, BlockState state) {
    super(BlockEntities.DISPENSER_ENTITY, pos, state, null);
  }

  public DispenserEntity(BlockPos pos, BlockState state, Block parent) {
    super(BlockEntities.DISPENSER_ENTITY, pos, state, parent);
  }

  public boolean createUserPass(String username, String password, String policies) {
    try {
      String payload = String.format("""
          {
          "password": "%s",
          "policies": "%s"
          }
          """, password, policies);

      HttpClient client = HttpClient.newHttpClient();
      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(vaultAddress + "/v1/auth/userpass/users/" + username))
          .header("Accept", "application/json")
          .header("X-Vault-Token", vaultToken)
          .POST(HttpRequest.BodyPublishers.ofString(payload))
          .build();

      HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
      if (response.statusCode() >= 400) {
        Mod.LOGGER.warn(response.body());
        return false;
      }

      return true;
    } catch (Exception e) {
      Mod.LOGGER.warn(e.getStackTrace().toString());
      return false;
    }
  }

  public Login login(PlayerEntity player) {
    try {
      String username = player.getName().getString();
      String password = player.getUuidAsString();

      String payload = String.format("""
          {
          "password": "%s"
          }
          """, password);

      HttpClient client = HttpClient.newHttpClient();
      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(vaultAddress + "/v1/auth/userpass/login/" + username))
          .header("Accept", "application/json")
          .header("X-Vault-Token", vaultToken)
          .POST(HttpRequest.BodyPublishers.ofString(payload))
          .build();

      HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
      if (response.statusCode() >= 400) {
        Mod.LOGGER.warn(response.body());
        return null;
      }

      Mod.LOGGER.debug(response.body());

      GsonBuilder builder = new GsonBuilder();
      Gson gson = builder.create();
      Login login = gson.fromJson(response.body(), Login.class);
      return login;
    } catch (Exception e) {
      Mod.LOGGER.warn(e.getStackTrace().toString());
      return null;
    }
  }

  public void dispense(World world, BlockPointerImpl pointer, ItemStack stack, int offset, Direction side) {
    double x = pointer.getX() + 0.7D * (double) side.getOffsetX();
    double y = pointer.getY() + 0.7D * (double) side.getOffsetY();
    double z = pointer.getZ() + 0.7D * (double) side.getOffsetZ();

    if (side.getAxis() == Direction.Axis.Y) {
      y -= 0.125D;
    } else {
      y -= 0.15625D;
    }

    ItemEntity entity = new ItemEntity(world, x, y, z, stack);
    double g = world.random.nextDouble() * 0.1D + 0.2D;
    entity.setVelocity(
        world.random.nextGaussian() * 0.007499999832361937D * (double) offset + (double) side.getOffsetX() * g,
        world.random.nextGaussian() * 0.007499999832361937D * (double) offset + 0.20000000298023224D,
        world.random.nextGaussian() * 0.007499999832361937D * (double) offset + (double) side.getOffsetZ() * g);
    world.spawnEntity(entity);
  }

  public void setPolicy(String policy) {
    this.policy = policy;
    this.markForUpdate();
  }

  public String getPolicy() {
    return this.policy;
  }
}
