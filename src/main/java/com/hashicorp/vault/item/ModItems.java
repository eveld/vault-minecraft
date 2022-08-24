package com.hashicorp.vault.item;

import com.hashicorp.vault.Mod;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

public class ModItems {
  public static final Item CARD_ITEM = registerItem("card",
      new CardItem(new FabricItemSettings().group(ItemGroups.VAULT).maxCount(1)));

  public static final Item WRENCH_ITEM = registerItem("wrench",
      new WrenchItem(new FabricItemSettings().group(ItemGroups.VAULT).maxCount(1)));

  private static Item registerItem(String name, Item item) {
    return Registry.register(Registry.ITEM, Mod.identifier(name), item);
  }

  public static void register() {
  }
}
