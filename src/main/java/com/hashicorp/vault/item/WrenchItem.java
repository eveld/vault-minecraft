package com.hashicorp.vault.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;

public class WrenchItem extends Item {
  public WrenchItem(Settings settings) {
    super(settings);
  }

  @Override
  public ActionResult useOnBlock(ItemUsageContext context) {
    return ActionResult.SUCCESS;
  }

  @Override
  public String getTranslationKey() {
    return "item.vault.wrench";
  }
}
