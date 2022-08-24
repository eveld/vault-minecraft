package com.hashicorp.vault.block;

import com.hashicorp.vault.Mod;
import com.hashicorp.vault.item.ItemGroups;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.registry.Registry;

public class ModBlocks {
  public static final Block DISPENSER_BLOCK = registerBlock("dispenser",
      new DispenserBlock(FabricBlockSettings.of(Material.GLASS).nonOpaque()),
      ItemGroups.VAULT);

  public static final Block LOCK_BLOCK = registerBlock("lock",
      new LockBlock(FabricBlockSettings.of(Material.METAL).nonOpaque()),
      ItemGroups.VAULT);

  private static Block registerBlock(String name, Block block, ItemGroup group) {
    registerBlockItem(name, block, group);
    return Registry.register(Registry.BLOCK, Mod.identifier(name), block);
  }

  private static Item registerBlockItem(String name, Block block, ItemGroup group) {
    return Registry.register(Registry.ITEM, Mod.identifier(name),
        new BlockItem(block, new FabricItemSettings().group(group)));
  }

  public static void register() {
  }
}
