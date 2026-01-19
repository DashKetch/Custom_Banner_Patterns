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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Mod(Custom_banner_patterns.MODID)
public class Custom_banner_patterns {
    public static final String MODID = "custom_banner_patterns";
    private static final Logger LOGGER = LogUtils.getLogger();

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

    public Custom_banner_patterns(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        // 4. Register the BLOCKS register to the bus
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        createPatternDirectory();
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM COMMON SETUP");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
        }
    }

    private void createPatternDirectory() {
        Path patternPath = FMLPaths.CONFIGDIR.get().resolve("custom_patterns");
        try {
            if (!Files.exists(patternPath)) {
                Files.createDirectories(patternPath);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to create pattern directory", e);
        }
    }
}