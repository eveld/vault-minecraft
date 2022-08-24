package com.hashicorp.vault.ui.event;

import com.hashicorp.vault.block.entity.LockEntity;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;

public interface LockClicked {
  Event<LockClicked> EVENT = EventFactory.createArrayBacked(LockClicked.class,
      (listeners) -> (block, callback) -> {
        for (LockClicked listener : listeners) {
          ActionResult result = listener.interact(block, callback);

          if (result != ActionResult.PASS) {
            return result;
          }
        }

        return ActionResult.PASS;
      });

  ActionResult interact(LockEntity block, LockCallback callback);
}
