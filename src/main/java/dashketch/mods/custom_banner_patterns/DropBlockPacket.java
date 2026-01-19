package dashketch.mods.custom_banner_patterns;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record DropBlockPacket(BlockPos pos) implements CustomPacketPayload {
    public static final Type<DropBlockPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Custom_banner_patterns.MODID, "drop_block"));

    public static final StreamCodec<RegistryFriendlyByteBuf, DropBlockPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            DropBlockPacket::pos,
            DropBlockPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    // This is the handler that the registrar calls
    public static void handle(final DropBlockPacket payload, final IPayloadContext context) {
        // Enqueue ensures this runs on the Main Server Thread
        context.enqueueWork(() -> {
            Level level = context.player().level();
            BlockPos pos = payload.pos();

            if (level.isLoaded(pos)) {
                // Drop the item
                ItemStack stack = new ItemStack(ItemsBlocksRegister.PATTERN_LOADER_ITEM.get());
                ItemEntity entity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
                level.addFreshEntity(entity);

                // Remove the bedrock-strength block
                level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            }
        });
    }
}