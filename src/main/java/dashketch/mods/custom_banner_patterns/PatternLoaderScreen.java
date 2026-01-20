package dashketch.mods.custom_banner_patterns;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public class PatternLoaderScreen extends Screen implements MenuAccess<PatternLoaderMenu> {
    private final PatternLoaderMenu menu;

    public PatternLoaderScreen(PatternLoaderMenu menu, Inventory inv, Component title) {
        super(title);
        this.menu = menu;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        this.addRenderableWidget(Button.builder(Component.literal("Drop Block"), b -> {
            PacketDistributor.sendToServer(new DropBlockPacket(menu.getPos()));
            this.onClose();
        }).bounds(centerX - 50, centerY - 10, 100, 20).build());
    }

    @Override
    public void render(@NotNull GuiGraphics g, int mouseX, int mouseY, float partial) {
        this.renderBackground(g, mouseX, mouseY, partial);
        super.render(g, mouseX, mouseY, partial);
        g.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
    }

    @Override public @NotNull PatternLoaderMenu getMenu() { return menu; }
}