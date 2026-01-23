package dashketch.mods.custom_banner_patterns;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class PatternLoaderBlock extends Block {

    // Combining all elements from JSON
    private static final VoxelShape SHAPE = Shapes.or(
            Block.box(0, 15, 0, 16, 17, 16),   // Table Top
            Block.box(1, 0, 12, 4, 15, 15),    // Leg 1
            Block.box(12, 0, 12, 15, 15, 15),  // Leg 2
            Block.box(12, 0, 1, 15, 15, 4),    // Leg 3
            Block.box(1, 0, 1, 4, 15, 4),      // Leg 4
            Block.box(4, 19.5, 2, 6, 22.5, 14),// Printer Part A
            Block.box(3, 17, 2, 14, 20, 14),   // Printer Part B
            Block.box(-1, 18, 6, 3, 18.5, 10), // Side Tray (Note: -1 is outside 1x1 block!)
            Block.box(3, 24, 5, 9, 24.5, 11),  // Top Lid
            Block.box(5, 20, 2, 14, 22, 14),   // Printer Part C
            Block.box(0, 0, 0, 16, 0.1, 16)    // Rug (slightly thicker than 0 to avoid flickering)
    );

    public PatternLoaderBlock(Properties props) {
        super(props);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hit) {
        if (!level.isClientSide && player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            serverPlayer.openMenu(new SimpleMenuProvider((id, inv, p) ->
                    new PatternLoaderMenu(id, inv, pos), Component.literal("Pattern Loader")), pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}