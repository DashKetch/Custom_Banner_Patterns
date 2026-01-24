package dashketch.mods.custom_banner_patterns;

import com.mojang.logging.LogUtils;
import dashketch.mods.custom_banner_patterns.client.DynamicBannerRenderer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Mod(Custom_banner_patterns.MODID)
public class Custom_banner_patterns {
    public static final String MODID = "custom_banner_patterns";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Custom_banner_patterns(IEventBus modEventBus, ModContainer modContainer) {
        ItemsBlocksRegister.BLOCKS.register(modEventBus);
        ItemsBlocksRegister.ITEMS.register(modEventBus);
        GUI_HUD_Register.MENUS.register(modEventBus);
        ItemsBlocksRegister.CREATIVE_MODE_TABS.register(modEventBus);

        // Standard listeners
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerNetworking);

        // Client-only listener for the renderer
        modEventBus.addListener(this::registerRenderers);

        createPatternDirectory();
    }

    // This method tells Minecraft to use your custom renderer for Banners
    private void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        LOGGER.info("Registering dynamic banner renderer");
        event.registerBlockEntityRenderer(BlockEntityType.BANNER, DynamicBannerRenderer::new);
    }

    private void registerNetworking(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(MODID);
        registrar.playToServer(DropBlockPacket.TYPE, DropBlockPacket.STREAM_CODEC, DropBlockPacket::handle);
        registrar.playToServer(PrintPatternPacket.TYPE, PrintPatternPacket.STREAM_CODEC, PrintPatternPacket::handle);
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

    private void commonSetup(final FMLCommonSetupEvent event) {
        PatternManager.loadPatterns();
    }
}