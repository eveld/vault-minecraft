package com.hashicorp.vault.block.entity;

import com.hashicorp.vault.item.ModItems;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public class DispenserRenderer<T extends DispenserEntity> implements BlockEntityRenderer<T> {
  public DispenserRenderer(BlockEntityRendererFactory.Context ctx) {
  }

  @Override
  public void render(DispenserEntity entity, float tickDelta, MatrixStack matrices,
      VertexConsumerProvider vertexConsumers, int light, int overlay) {
    Direction direction = entity.getCachedState().get(Properties.HORIZONTAL_FACING);
    renderItem(ModItems.CARD_ITEM, 0.0f, direction, matrices, vertexConsumers, light, overlay);

    renderItem(ModItems.CARD_ITEM, 0.15f, direction, matrices, vertexConsumers, light, overlay);

    renderItem(ModItems.CARD_ITEM, 0.3f, direction, matrices, vertexConsumers, light, overlay);
  }

  public void renderItem(Item item, float offset, Direction direction, MatrixStack matrices,
      VertexConsumerProvider vertexConsumers, int light, int overlay) {
    matrices.push();

    float xTranslate = 0.0F;
    float yTranslate = 0.25F;
    float zTranslate = 0.0F;

    float scale = 0.4f;
    float rotation = 0.0F;

    switch (direction) {
      case UP:
        break;
      case DOWN:
        break;
      case NORTH:
        xTranslate = 0.5F;
        zTranslate = 0.45F + offset;
        rotation = 180.0F;
        break;
      case SOUTH:
        xTranslate = 0.5F;
        zTranslate = 0.55F - offset;
        rotation = 0.0F;
        break;
      case EAST:
        xTranslate = 0.55F - offset;
        zTranslate = 0.5F;
        rotation = 90.0F;
        break;
      case WEST:
        xTranslate = 0.45F + offset;
        zTranslate = 0.5F;
        rotation = 270.0F;
        break;
    }

    matrices.translate(xTranslate, yTranslate, zTranslate);
    matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(rotation));

    matrices.scale(scale, scale, scale);

    MinecraftClient.getInstance().getItemRenderer().renderItem(new ItemStack(item, 1),
        ModelTransformation.Mode.GUI,
        light, overlay, matrices, vertexConsumers, 0);

    matrices.pop();
  }
}