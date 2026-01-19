package dashketch.mods.custom_banner_patterns;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class PatternLoaderBlock extends Block {
    public PatternLoaderBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult) {
        if (player instanceof ServerPlayer serverPlayer) {
            // We use a provider that writes the BlockPos to the buffer so the client can read it
            serverPlayer.openMenu(new SimpleMenuProvider((id, inv, p) ->
                    new PatternLoaderMenu(id, inv, pos), Component.literal("Pattern Loader")), pos); // Note the extra 'pos' here
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}