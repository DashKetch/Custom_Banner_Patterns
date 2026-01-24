package dashketch.mods.custom_banner_patterns.mixin;

import dashketch.mods.custom_banner_patterns.ItemsBlocksRegister;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.LoomMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(LoomMenu.class)
public abstract class LoomMenuMixin extends AbstractContainerMenu {

    @Shadow @Final
    Slot bannerSlot;
    @Shadow @Final
    Slot dyeSlot;
    @Shadow @Final private Slot patternSlot;
    @Shadow @Final private Slot resultSlot;

    protected LoomMenuMixin(MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
    }

    @Inject(method = "setupResultSlot", at = @At("HEAD"), cancellable = true)
    private void custom_banner_patterns$applyDynamicFilePattern(Holder<BannerPattern> selectedPattern, CallbackInfo ci) {
        ItemStack patternStack = this.patternSlot.getItem();
        ItemStack bannerStack = this.bannerSlot.getItem();
        ItemStack dyeStack = this.dyeSlot.getItem();

        // 1. Verify our custom item is present
        if (patternStack.is(ItemsBlocksRegister.EMPTY_PATTERN.get())) {

            // 2. Check for required ingredients
            if (!bannerStack.isEmpty() && !dyeStack.isEmpty()) {
                ItemStack resultStack = bannerStack.copy();
                resultStack.setCount(1);

                DyeColor dyeColor = ((DyeItem) dyeStack.getItem()).getDyeColor();

                // 3. Handle Pattern Registry Lookup
                var registry = net.minecraft.client.Minecraft.getInstance().level.registryAccess()
                        .lookupOrThrow(Registries.BANNER_PATTERN);

                ResourceKey<BannerPattern> key = ResourceKey.create(Registries.BANNER_PATTERN,
                        ResourceLocation.fromNamespaceAndPath("custom_banner_patterns", "dynamic_pattern"));

                Optional<Holder.Reference<BannerPattern>> patternOptional = registry.get(key);

                if (patternOptional.isPresent()) {
                    // 4. Update the Banner Layers
                    BannerPatternLayers layers = resultStack.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY);
                    BannerPatternLayers.Builder builder = new BannerPatternLayers.Builder().addAll(layers);
                    builder.add(patternOptional.get(), dyeColor);
                    resultStack.set(DataComponents.BANNER_PATTERNS, builder.build());

                    // 5. DATA TRANSFER: Move the file contents (NBT) from Pattern to Banner
                    // This moves your "PatternID" and any file data you stored in CustomData
                    if (patternStack.has(DataComponents.CUSTOM_DATA)) {
                        resultStack.set(DataComponents.CUSTOM_DATA, patternStack.get(DataComponents.CUSTOM_DATA));
                    }

                    // 6. Finalize the slot update
                    if (!ItemStack.matches(resultStack, this.resultSlot.getItem())) {
                        this.resultSlot.set(resultStack);
                    }
                }

                ci.cancel();
            }
        }
    }
}