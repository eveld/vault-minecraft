package com.hashicorp.vault.ui.event;

import com.hashicorp.vault.block.entity.DispenserEntity;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;

public interface DispenserClicked {
  Event<DispenserClicked> EVENT = EventFactory.createArrayBacked(DispenserClicked.class,
      (listeners) -> (block, callback) -> {
        for (DispenserClicked listener : listeners) {
          ActionResult result = listener.interact(block, callback);

          if (result != ActionResult.PASS) {
            return result;
          }
        }

        return ActionResult.PASS;
      });

  ActionResult interact(DispenserEntity block, DispenserCallback callback);
}
