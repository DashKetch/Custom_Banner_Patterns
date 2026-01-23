package dashketch.mods.custom_banner_patterns;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class PatternLoaderScreen extends Screen implements MenuAccess<PatternLoaderMenu> {
    private final PatternLoaderMenu menu;

    // Gallery State
    private int currentPatternIndex = 0;
    private List<String> currentFileLines = Collections.emptyList();

    // Scrolling State
    private float scrollOffset = 0;
    private static final int BASE_LINE_HEIGHT = 10;

    // Gallery Bounds
    private final int minX = 40;
    private final int maxX = 430;
    private final int minY = 40;
    private final int maxY = 120;

    public PatternLoaderScreen(PatternLoaderMenu menu, Inventory inv, Component title) {
        super(title);
        this.menu = menu;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        this.addRenderableWidget(Button.builder(Component.literal("<"), b -> changePattern(-1))
                .bounds(maxX - 45, maxY + 5, 20, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal(">"), b -> changePattern(1))
                .bounds(maxX - 20, maxY + 5, 20, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Drop Block"), b -> {
            PacketDistributor.sendToServer(new DropBlockPacket(menu.getPos()));
            this.onClose();
        }).bounds(centerX - 50, centerY + 100, 100, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Refresh Patterns"), b -> {
            PatternManager.loadPatterns();
            currentPatternIndex = 0;
            loadCurrentPatternContent();
            assert this.minecraft != null;
            this.init(this.minecraft, this.width, this.height);
        }).bounds(centerX - 50, centerY + 20, 100, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Print Pattern"), b -> {
            List<String> patterns = PatternManager.getCustomPatterns();
            if (!patterns.isEmpty() && currentPatternIndex < patterns.size()) {
                String filename = patterns.get(currentPatternIndex);
                // Send the packet!
                PacketDistributor.sendToServer(new PrintPatternPacket(filename));
                this.onClose(); // Optional: Close menu after printing
            }
        }).bounds(centerX + 60, centerY + 100, 100, 20).build());

        loadCurrentPatternContent();
    }

    private void changePattern(int direction) {
        List<String> patterns = PatternManager.getCustomPatterns();
        if (patterns.isEmpty()) return;

        this.currentPatternIndex += direction;
        if (this.currentPatternIndex >= patterns.size()) this.currentPatternIndex = 0;
        else if (this.currentPatternIndex < 0) this.currentPatternIndex = patterns.size() - 1;

        loadCurrentPatternContent();
    }

    public void loadCurrentPatternContent() {
        this.scrollOffset = 0; // Reset scroll
        List<String> patterns = PatternManager.getCustomPatterns();
        if (!patterns.isEmpty() && currentPatternIndex < patterns.size()) {
            this.currentFileLines = PatternManager.readPatternContent(patterns.get(currentPatternIndex));
        } else {
            this.currentFileLines = Collections.singletonList("No patterns found.");
        }
    }

    private float calculateScale() {
        if (currentFileLines.isEmpty()) return 1.0f;

        int availableHeight = maxY - minY;
        int totalRawHeight = currentFileLines.size() * BASE_LINE_HEIGHT;

        // Calculate exact scale needed to fit everything
        float fitScale = (float) availableHeight / totalRawHeight;

        // Clamp scale:
        // - Max 1.0 (don't zoom IN if the file is short)
        // - Min 0.5 (don't become microscopic if the file is huge)
        return Mth.clamp(fitScale, 0.5f, 1.0f);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (mouseX >= minX && mouseX <= maxX && mouseY >= minY && mouseY <= maxY) {
            float scale = calculateScale();
            float scaledLineHeight = BASE_LINE_HEIGHT * scale;

            float totalContentHeight = currentFileLines.size() * scaledLineHeight;
            float visibleHeight = maxY - minY;

            if (totalContentHeight > visibleHeight) {
                // Scroll speed adjusted by scale so it feels natural
                float scrollSpeed = scaledLineHeight * 3;
                this.scrollOffset = (float)Mth.clamp(this.scrollOffset - scrollY * scrollSpeed, 0, totalContentHeight - visibleHeight);
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public void render(@NotNull GuiGraphics g, int mouseX, int mouseY, float partial) {
        this.renderBackground(g, mouseX, mouseY, partial);
        super.render(g, mouseX, mouseY, partial);

        // 1. Background & Borders
        g.fill(minX, minY, maxX, maxY, 0xFF000000);
        g.hLine(minX - 1, maxX, minY - 1, 0xFFAAAAAA);
        g.hLine(minX - 1, maxX, maxY, 0xFFAAAAAA);
        g.vLine(minX - 1, minY - 1, maxY, 0xFFAAAAAA);
        g.vLine(maxX, minY - 1, maxY, 0xFFAAAAAA);

        // 2. Header
        List<String> patterns = PatternManager.getCustomPatterns();
        String header = patterns.isEmpty() ? "No Files" :
                String.format("File (%d/%d): %s", currentPatternIndex + 1, patterns.size(), patterns.get(currentPatternIndex));
        g.drawString(this.font, header, minX, minY - 12, 0xFFFFAA00);

        // 3. Render Scaled Content
        float scale = calculateScale();

        // We use Scissor to clip ANY content that flows outside the box, even if zoomed
        g.enableScissor(minX, minY, maxX, maxY);

        // Save the current matrix state
        g.pose().pushPose();

        // Move the origin to the top-left of the gallery box
        g.pose().translate(minX, minY, 0);

        // Apply the zoom
        g.pose().scale(scale, scale, 1.0f);

        // Move UP based on scroll (we divide by scale because we are currently inside the scaled world)
        g.pose().translate(0, -scrollOffset / scale, 0);

        for (int i = 0; i < currentFileLines.size(); i++) {
            // Draw at 0 + padding, because we already translated to minX
            // The Y position is just line index * height
            g.drawString(this.font, currentFileLines.get(i), 5, i * BASE_LINE_HEIGHT + 2, 0xFFFFFF);
        }

        g.pose().popPose(); // Restore matrix
        g.disableScissor();

        // 4. Scrollbar (Optional)
        float totalHeight = currentFileLines.size() * BASE_LINE_HEIGHT * scale;
        float visibleHeight = maxY - minY;
        if (totalHeight > visibleHeight) {
            int barHeight = (int) ((visibleHeight / totalHeight) * visibleHeight);
            if (barHeight < 10) barHeight = 10;
            int barY = minY + (int) ((scrollOffset / (totalHeight - visibleHeight)) * (visibleHeight - barHeight));
            g.fill(maxX - 2, barY, maxX, barY + barHeight, 0xFF888888);
        }

        g.drawCenteredString(this.font, this.title, this.width / 2, 10, 0xFFFFFF);
    }

    @Override public @NotNull PatternLoaderMenu getMenu() { return menu; }
}