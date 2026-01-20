package dashketch.mods.custom_banner_patterns;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Mod(Custom_banner_patterns.MODID)
public class Custom_banner_patterns {
    public static final String MODID = "custom_banner_patterns";
    public static Logger LOGGER = LogUtils.getLogger();

    public Custom_banner_patterns(IEventBus modEventBus, ModContainer modContainer) {
        ItemsBlocksRegister.BLOCKS.register(modEventBus);
        ItemsBlocksRegister.ITEMS.register(modEventBus);
        GUI_HUD_Register.MENUS.register(modEventBus);
        ItemsBlocksRegister.CREATIVE_MODE_TABS.register(modEventBus);

        modEventBus.addListener(this::registerNetworking);
    }

    private void registerNetworking(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(MODID);
        registrar.playToServer(DropBlockPacket.TYPE, DropBlockPacket.STREAM_CODEC, DropBlockPacket::handle);
    }

    private void createPatternDirectory() {
        Path PatternPath = FMLPaths.CONFIGDIR.get().resolve("custom_patterns");
        try {
            if (!Files.exists(PatternPath)) {
                Files.createDirectories(PatternPath);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to create pattern directory", e);
        }
    }
}