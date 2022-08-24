package com.hashicorp.vault.item;

import com.hashicorp.vault.Mod;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ItemGroups {
  public static final ItemGroup VAULT = FabricItemGroupBuilder.build(Mod.identifier("vault"),
      () -> new ItemStack(ModItems.CARD_ITEM));
}
