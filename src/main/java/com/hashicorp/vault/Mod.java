package com.hashicorp.vault;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.hashicraft.stateful.blocks.EntityServerState;
import com.hashicorp.vault.block.ModBlocks;
import com.hashicorp.vault.block.entity.BlockEntities;
import com.hashicorp.vault.item.ModItems;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class Mod implements ModInitializer {
	public static final String MOD_ID = "vault";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		EntityServerState.RegisterStateUpdates();

		ModBlocks.register();
		BlockEntities.register();
		ModItems.register();
	}

	public static Identifier identifier(String path) {
		return new Identifier(MOD_ID, path);
	}
}
