package dashketch.mods.custom_banner_patterns.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.core.component.DataComponents;
import org.jetbrains.annotations.NotNull;

public class DynamicBannerRenderer implements BlockEntityRenderer<BannerBlockEntity> {
    private final Font font;
    private final BannerRenderer vanillaRenderer;

    public DynamicBannerRenderer(BlockEntityRendererProvider.Context context) {
        this.font = context.getFont();
        // We create a vanilla renderer instance to draw the actual banner cloth
        this.vanillaRenderer = new BannerRenderer(context);
    }

    @Override
    public void render(@NotNull BannerBlockEntity blockEntity, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        // 1. ALWAYS render the vanilla banner first so it's not transparent
        this.vanillaRenderer.render(blockEntity, partialTick, poseStack, bufferSource, combinedLight, combinedOverlay);

        // 2. SAFETY CHECK: Check for our custom data
        CustomData customData = blockEntity.components().get(DataComponents.CUSTOM_DATA);
        if (customData == null) return;

        CompoundTag tag = customData.copyTag();
        // Check for the key you use in your PrintPatternPacket (e.g., "PatternID")
        if (!tag.contains("PatternID")) return;

        String textToDraw = tag.getString("PatternID");

        // 3. DRAW TEXT OVERLAY
        poseStack.pushPose();

        // Position the text slightly in front of the banner cloth
        // Use 0.51 to prevent "z-fighting" (flickering) with the banner texture
        poseStack.translate(0.5, 0.7, 0.51);
        poseStack.scale(0.012f, -0.012f, 0.012f);

        float x = (float)(-font.width(textToDraw) / 2);

        // Draw the text in white
        this.font.drawInBatch(textToDraw, x, 0, 0xFFFFFFFF, false,
                poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, combinedLight);

        poseStack.popPose();
    }
}