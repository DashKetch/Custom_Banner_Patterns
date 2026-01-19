package dashketch.mods.custom_banner_patterns;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PatternLoaderMenu extends AbstractContainerMenu {
    // Client-side constructor
    private final BlockPos pos; // Store the position

    // Client-side constructor (FriendlyByteBuf carries the extra data)
    public PatternLoaderMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, extraData.readBlockPos());
    }

    // Server-side constructor
    public PatternLoaderMenu(int containerId, Inventory inv, BlockPos pos) {
        super(GUI_HUD_Register.PATTERN_LOADER_MENU.get(), containerId);
        this.pos = pos;
    }

    public BlockPos getPos() { return this.pos; }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }
}