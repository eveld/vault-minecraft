package com.hashicorp.vault.block.entity;

import com.hashicorp.vault.Mod;
import com.hashicorp.vault.block.ModBlocks;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;

public class BlockEntities {
  public static BlockEntityType<DispenserEntity> DISPENSER_ENTITY;
  public static BlockEntityType<LockEntity> LOCK_ENTITY;

  public static void register() {
    DISPENSER_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, Mod.identifier("dispenser"),
        FabricBlockEntityTypeBuilder
            .create(DispenserEntity::new, ModBlocks.DISPENSER_BLOCK)
            .build(null));

    LOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, Mod.identifier("lock"),
        FabricBlockEntityTypeBuilder
            .create(LockEntity::new, ModBlocks.LOCK_BLOCK)
            .build(null));
  }
}
