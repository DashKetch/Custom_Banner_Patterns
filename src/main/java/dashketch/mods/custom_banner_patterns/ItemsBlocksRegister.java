package dashketch.mods.custom_banner_patterns;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import static dashketch.mods.custom_banner_patterns.Custom_banner_patterns.MODID;

public class ItemsBlocksRegister {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredBlock<Block> PATTERN_LOADER = BLOCKS.register("pattern_loader",
            () -> new PatternLoaderBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(-1.0F, 3600000.0F).sound(SoundType.METAL).noOcclusion()));

    public static final DeferredItem<BlockItem> PATTERN_LOADER_ITEM = ITEMS.registerSimpleBlockItem("pattern_loader", PATTERN_LOADER);

    public static final DeferredHolder<MenuType<?>, MenuType<PatternLoaderMenu>> PATTERN_LOADER_MENU =
            MENUS.register("pattern_loader_menu", () -> IMenuTypeExtension.create(PatternLoaderMenu::new));

    public static final DeferredItem<Item> EMPTY_PATTERN = ITEMS.registerSimpleItem("empty_banner_pattern", new Item.Properties().stacksTo(16));

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CUSTOM_BANNERS = CREATIVE_MODE_TABS.register("custom_banners",
            () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.custom_banner_patterns")).icon(() -> EMPTY_PATTERN.get().getDefaultInstance())
                    .displayItems((p, output) -> { output.accept(EMPTY_PATTERN.get()); output.accept(PATTERN_LOADER_ITEM.get()); }).build());
}