package com.hashicorp.vault.block.entity;

import com.github.hashicraft.stateful.blocks.StatefulBlockEntity;
import com.github.hashicraft.stateful.blocks.Syncable;

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
    return false;
  }

  public boolean verify(String input, String signature) {
    return false;
  }

  public String decrypt(String input) {
    return null;
  }

  public void setPolicy(String policy) {
    this.policy = policy;
    this.markForUpdate();
  }

  public String getPolicy() {
    return this.policy;
  }
}
