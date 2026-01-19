package dashketch.mods.custom_banner_patterns;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

import static dashketch.mods.custom_banner_patterns.Custom_banner_patterns.MODID;

public class ItemsBlocksRegister {;

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredBlock<Block> PATTERN_LOADER = BLOCKS.register("pattern_loader",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));

    public static final DeferredItem<BlockItem> PATTERN_LOADER_ITEM = ITEMS.register("pattern_loader",
            () -> new BlockItem(PATTERN_LOADER.get(), new Item.Properties()));

    public static final DeferredItem<Item> EMPTY_PATTERN = ITEMS.registerSimpleItem("empty_banner_pattern", new Item.Properties().stacksTo(16));

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CUSTOM_BANNERS = CREATIVE_MODE_TABS.register("custom_banners",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.custom_banner_patterns"))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(() -> EMPTY_PATTERN.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(EMPTY_PATTERN.get());
                        output.accept(PATTERN_LOADER_ITEM.get());
                    }).build());
}
