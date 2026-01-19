package dashketch.mods.custom_banner_patterns;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess; // Import this
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public class PatternLoaderScreen extends Screen implements MenuAccess<PatternLoaderMenu> {
    private final PatternLoaderMenu menu;

    public PatternLoaderScreen(PatternLoaderMenu menu, Component title) {
        super(title);
        this.menu = menu;
    }

    @Override
    public @NotNull PatternLoaderMenu getMenu() {
        return this.menu;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        this.addRenderableWidget(Button.builder(Component.literal("Drop Block"), b -> {
            // Send the packet to the server
            PacketDistributor.sendToServer(new DropBlockPacket(this.menu.getPos()));
            this.onClose();
        }).bounds(centerX - 50, centerY - 10, 100, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Close"), b -> this.onClose())
                .bounds(centerX - 50, this.height - 30, 100, 20).build());
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        int centerX = this.width / 2;
        guiGraphics.drawCenteredString(this.font, this.title, centerX, 20, 0xFFFFFF);
    }
}