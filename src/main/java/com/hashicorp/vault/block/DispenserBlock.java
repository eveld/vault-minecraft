package com.hashicorp.vault.block;

import com.github.hashicraft.stateful.blocks.StatefulBlock;
import com.hashicorp.vault.block.entity.DispenserEntity;
import com.hashicorp.vault.item.ModItems;
import com.hashicorp.vault.ui.event.DispenserClicked;
import com.hashicorp.vault.vault.Login;

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
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPointerImpl;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class DispenserBlock extends StatefulBlock {
  public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

  protected DispenserBlock(Settings settings) {
    super(settings);
    this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
  }

  @Override
  protected void appendProperties(Builder<Block, BlockState> builder) {
    builder.add(FACING);
  }

  @Override
  public BlockRenderType getRenderType(BlockState state) {
    return BlockRenderType.MODEL;
  }

  @Override
  public BlockState getPlacementState(ItemPlacementContext ctx) {
    return getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
  }

  @Override
  public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
    return new DispenserEntity(pos, state);
  }

  @Override
  public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
      BlockHitResult hit) {
    ItemStack stack = player.getStackInHand(hand);
    BlockEntity entity = world.getBlockEntity(pos);

    if (entity instanceof DispenserEntity) {
      DispenserEntity dispenser = (DispenserEntity) entity;
      Direction direction = dispenser.getCachedState().get(FACING);

      if (world.isClient) {
        if (stack.isOf(ModItems.WRENCH_ITEM)) {
          DispenserClicked.EVENT.invoker().interact(dispenser, () -> {
            dispenser.markForUpdate();
          });
        }
        return ActionResult.SUCCESS;
      }

      if (!stack.isOf(ModItems.WRENCH_ITEM)) {
        // Create a new card.
        ItemStack card = new ItemStack(ModItems.CARD_ITEM);

        // Use the player name as the username.
        String username = player.getName().getString();

        // Use the player uuid as the password.
        String uuid = player.getUuidAsString();

        // Create the vault user pass for the player.
        boolean created = dispenser.createUserPass(username, uuid, dispenser.getPolicy());
        if (!created) {
          player.sendMessage(Text.literal("ERROR - Could not create user pass"), true);
          return ActionResult.SUCCESS;
        }

        // Login using the user pass.
        Login login = dispenser.login(player);
        if (login == null) {
          player.sendMessage(Text.literal("ERROR - Could not login with user pass"), true);
          return ActionResult.SUCCESS;
        }

        // Encrypt the uuid of the player.
        String encrypted = dispenser.encrypt(uuid);

        // Sign the encrypted uuid.
        String signature = dispenser.sign(encrypted);

        // Create an Nbt containing the login details and write it to the card.
        NbtCompound identity = card.getOrCreateNbt();
        identity.putString("name", login.auth.metadata.username);
        identity.putString("token", login.auth.token);
        identity.putString("policies", String.join(", ", login.auth.policies));
        identity.putString("encrypted", encrypted);
        identity.putString("signature", signature);
        card.setNbt(identity);

        // Dispense a card with the login details.
        BlockPointerImpl pointer = new BlockPointerImpl((ServerWorld) world, pos);
        dispenser.dispense(world, pointer, card, 1, direction);
      }
    }

    return ActionResult.SUCCESS;
  }
}
