package com.hashicorp.vault.mixin;

import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.hashicorp.vault.Mod;

@Mixin(TitleScreen.class)
public class VaultMixin {
	@Inject(at = @At("HEAD"), method = "init()V")
	private void init(CallbackInfo info) {
		Mod.LOGGER.info("This line is printed by an example mod mixin!");
	}
}
