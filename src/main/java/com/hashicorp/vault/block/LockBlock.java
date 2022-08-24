package com.hashicorp.vault.block;

import com.github.hashicraft.stateful.blocks.StatefulBlock;
import com.hashicorp.vault.block.entity.LockEntity;
import com.hashicorp.vault.item.ModItems;
import com.hashicorp.vault.ui.event.LockClicked;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class LockBlock extends StatefulBlock {
  public static final BooleanProperty POWERED = BooleanProperty.of("powered");
  public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

  protected LockBlock(Settings settings) {
    super(settings);
    this.setDefaultState(
        this.stateManager.getDefaultState()
            .with(FACING, Direction.NORTH)
            .with(POWERED, false));
  }

  @Override
  protected void appendProperties(Builder<Block, BlockState> builder) {
    builder.add(FACING, POWERED);
  }

  @Override
  public BlockRenderType getRenderType(BlockState state) {
    return BlockRenderType.MODEL;
  }

  @Override
  public BlockState getPlacementState(ItemPlacementContext ctx) {
    return getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
  }

  public boolean emitsRedstonePower(BlockState state) {
    return true;
  }

  public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
    return state.get(POWERED) != false ? 15 : 0;
  }

  @Override
  public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
    return new LockEntity(pos, state);
  }

  @Override
  public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
      BlockHitResult hit) {
    ItemStack stack = player.getStackInHand(hand);
    BlockEntity entity = world.getBlockEntity(pos);

    if (entity instanceof LockEntity) {
      LockEntity lock = (LockEntity) entity;

      if (world.isClient) {
        if (stack.isOf(ModItems.WRENCH_ITEM)) {
          LockClicked.EVENT.invoker().interact(lock, () -> {
            lock.markForUpdate();
          });
        }
        return ActionResult.SUCCESS;
      }

      if (stack.isOf(ModItems.CARD_ITEM)) {
        NbtCompound identity = stack.getOrCreateNbt();
        String token = identity.getString("token");
        String policy = identity.getString("policy");

        if (token == null) {
          player.sendMessage(Text.literal("ACCESS DENIED - You need to be authenticated"), true);
          return ActionResult.SUCCESS;
        }

        if (policy == null) {
          player.sendMessage(Text.literal("ACCESS DENIED - No policies found"), true);
          return ActionResult.SUCCESS;
        }

        boolean access = lock.checkAccess(token, lock.getPolicy());
        if (access) {
          BlockState newState = state.with(POWERED, true);
          world.setBlockState(pos, newState, Block.NOTIFY_ALL);
          world.createAndScheduleBlockTick(new BlockPos(pos), this, 40);
          return ActionResult.SUCCESS;
        } else {
          player.sendMessage(Text.literal("ACCESS DENIED - You do not have the required policies"), true);
          return ActionResult.SUCCESS;
        }
      }
    }

    return ActionResult.SUCCESS;

  }

  @Override
  public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
    if (!state.get(POWERED).booleanValue()) {
      return;
    }

    BlockState newState = state.with(POWERED, false);
    world.setBlockState(pos, newState, Block.NOTIFY_ALL);
  }
}
