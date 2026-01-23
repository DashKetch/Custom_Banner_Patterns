package dashketch.mods.custom_banner_patterns;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PatternLoaderBlock extends Block {
    // 1. Define the Facing Property
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    // 2. The Base Shape (North-facing coordinates from your JSON)
    private static final VoxelShape SHAPE_NORTH = Shapes.or(
            Block.box(0, 15, 0, 16, 17, 16),   // Table Top
            Block.box(1, 0, 12, 4, 15, 15),    // Leg 1
            Block.box(12, 0, 12, 15, 15, 15),  // Leg 2
            Block.box(12, 0, 1, 15, 15, 4),    // Leg 3
            Block.box(1, 0, 1, 4, 15, 4),      // Leg 4
            Block.box(4, 19.5, 2, 6, 22.5, 14),// Printer Part A
            Block.box(3, 17, 2, 14, 20, 14),   // Printer Part B
            Block.box(-1, 18, 6, 3, 18.5, 10), // Side Tray
            Block.box(3, 24, 5, 9, 24.5, 11),  // Top Lid
            Block.box(5, 20, 2, 14, 22, 14),   // Printer Part C
            Block.box(0, 0, 0, 16, 0.1, 16)    // Rug
    );

    public PatternLoaderBlock(Properties props) {
        super(props);
        // Set default state
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    // 3. Handle the placement direction
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        // Returns the opposite of the player's direction so it faces them
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    // 4. Rotate the hitbox to match the model
    @Override
    public @NotNull VoxelShape getShape(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        Direction dir = state.getValue(FACING);
        return rotateShape(Direction.NORTH, dir, SHAPE_NORTH);
    }

    // Helper method to rotate VoxelShapes
    public static VoxelShape rotateShape(Direction from, Direction to, VoxelShape shape) {
        VoxelShape[] buffer = new VoxelShape[]{shape, Shapes.empty()};
        int times = (to.get2DDataValue() - from.get2DDataValue() + 4) % 4;
        for (int i = 0; i < times; i++) {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = Shapes.or(buffer[1],
                    Shapes.box(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX)));
            buffer[0] = buffer[1];
            buffer[1] = Shapes.empty();
        }
        return buffer[0];
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
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