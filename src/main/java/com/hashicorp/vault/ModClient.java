package com.hashicorp.vault;

import com.hashicorp.vault.block.ModBlocks;
import com.hashicorp.vault.block.entity.BlockEntities;
import com.hashicorp.vault.block.entity.DispenserRenderer;
import com.hashicorp.vault.ui.DispenserGui;
import com.hashicorp.vault.ui.DispenserScreen;
import com.hashicorp.vault.ui.LockGui;
import com.hashicorp.vault.ui.LockScreen;
import com.hashicorp.vault.ui.event.DispenserClicked;
import com.hashicorp.vault.ui.event.LockClicked;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.ActionResult;

@Environment(EnvType.CLIENT)
public class ModClient implements ClientModInitializer {
        @Override
        public void onInitializeClient() {
                BlockEntityRendererRegistry.register(BlockEntities.DISPENSER_ENTITY,
                                DispenserRenderer::new);
                BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DISPENSER_BLOCK,
                                RenderLayer.getTranslucent());

                LockClicked.EVENT.register((block, callback) -> {
                        LockGui gui = new LockGui(block, callback);
                        LockScreen screen = new LockScreen(gui);
                        MinecraftClient.getInstance().setScreen(screen);

                        return ActionResult.PASS;
                });

                DispenserClicked.EVENT.register((block, callback) -> {
                        DispenserGui gui = new DispenserGui(block, callback);
                        DispenserScreen screen = new DispenserScreen(gui);
                        MinecraftClient.getInstance().setScreen(screen);

                        return ActionResult.PASS;
                });
        }
}
