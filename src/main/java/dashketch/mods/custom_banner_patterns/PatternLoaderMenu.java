package dashketch.mods.custom_banner_patterns;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PatternLoaderMenu extends AbstractContainerMenu {
    private final BlockPos pos;

    public PatternLoaderMenu(int id, Inventory inv, FriendlyByteBuf data) { this(id, inv, data.readBlockPos()); }

    public PatternLoaderMenu(int id, Inventory inv, BlockPos pos) {
        super(ItemsBlocksRegister.PATTERN_LOADER_MENU.get(), id);
        this.pos = pos;
    }

    public BlockPos getPos() { return pos; }
    @Override public @NotNull ItemStack quickMoveStack(@NotNull Player p, int i) { return ItemStack.EMPTY; }
    @Override public boolean stillValid(@NotNull Player p) { return true; }
}