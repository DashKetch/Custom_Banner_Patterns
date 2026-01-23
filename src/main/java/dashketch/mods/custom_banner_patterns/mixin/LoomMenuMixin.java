package dashketch.mods.custom_banner_patterns.mixin;

import dashketch.mods.custom_banner_patterns.ItemsBlocksRegister;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.LoomMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.entity.BannerPatterns;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LoomMenu.class)
public abstract class LoomMenuMixin extends AbstractContainerMenu {

    @Shadow @Final
    Slot bannerSlot;
    @Shadow @Final
    Slot dyeSlot;
    @Shadow @Final private Slot patternSlot;
    @Shadow @Final private Slot resultSlot;

    // We must match the constructor of the parent class
    protected LoomMenuMixin(MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
    }

    @Inject(method = "setupResultSlot", at = @At("HEAD"), cancellable = true)
    private void customPatternLogic(Holder<BannerPattern> selectedPattern, CallbackInfo ci) {
        ItemStack patternStack = this.patternSlot.getItem();
        ItemStack bannerStack = this.bannerSlot.getItem();
        ItemStack dyeStack = this.dyeSlot.getItem();

        // 1. Check if the item in the pattern slot is OUR item
        if (patternStack.is(ItemsBlocksRegister.EMPTY_PATTERN.get())) {

            // 2. Ensure we have a Banner and Dye
            if (!bannerStack.isEmpty() && !dyeStack.isEmpty()) {

                // 3. Read the ID from our item's NBT
                CustomData customData = patternStack.get(DataComponents.CUSTOM_DATA);
                String patternId = "unknown";
                if (customData != null) {
                    CompoundTag tag = customData.copyTag();
                    if (tag.contains("PatternID")) {
                        patternId = tag.getString("PatternID");
                    }
                }

                // 4. Generate the Output Banner
                ItemStack resultStack = bannerStack.copy();
                resultStack.setCount(1);

                DyeColor dyeColor = ((net.minecraft.world.item.DyeItem) dyeStack.getItem()).getDyeColor();
                BannerPatternLayers layers = resultStack.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY);

                // --- LOGIC: MAP FILENAME TO PATTERN ---
                // HERE is where you decide what "cool_skull.txt" actually does.
                // For now, I am mapping ANY file to the "GLOBE" pattern as a test.
                // You would parse your text file here to pick the right pattern.
                Holder<BannerPattern> patternToApply = this.customBannerPatterns$getPatternFromId(patternId);
                // --------------------------------------

                if (patternToApply != null) {
                    BannerPatternLayers.Layer newLayer = new BannerPatternLayers.Layer(patternToApply, dyeColor);
                    resultStack.set(DataComponents.BANNER_PATTERNS, new BannerPatternLayers.Builder().addAll(layers).add(newLayer).build());
                }

                // 5. Set the result and CANCEL vanilla logic
                if (!ItemStack.matches(resultStack, this.resultSlot.getItem())) {
                    this.resultSlot.set(resultStack);
                }

                ci.cancel(); // Stop vanilla Minecraft from saying "Invalid Recipe"
            }
        }
    }

    // Helper to map string ID to a real Minecraft Pattern
    @Unique
    private Holder<BannerPattern> customBannerPatterns$getPatternFromId(String id) {
        // TODO: In the future, read .txt file content to decide this!
        // For now, hardcode it to see it working.
        if (id.contains("creeper")) return customBannerPatterns$getPattern(BannerPatterns.CREEPER);
        if (id.contains("skull")) return customBannerPatterns$getPattern(BannerPatterns.SKULL);
        return customBannerPatterns$getPattern(BannerPatterns.GLOBE); // Default
    }

    // Helper to fetch registry holders
    @SuppressWarnings("DataFlowIssue")
    @Unique
    private Holder<BannerPattern> customBannerPatterns$getPattern(net.minecraft.resources.ResourceKey<BannerPattern> key) {
        if (this.bannerSlot.getItem().getItem() instanceof BannerItem bannerItem) {
            // We need access to the registry, easiest way is via the level
            // But inside a Menu, we are limited.
            // For this snippet, we will try to look it up dynamically or return null.
            // Simplification:
            var registry = net.minecraft.client.Minecraft.getInstance().level.registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.BANNER_PATTERN);
            return registry.getHolder(key).orElse(null);
        }
        return null;
    }
}