package dashketch.mods.custom_banner_patterns;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@Mod(Custom_banner_patterns.MODID)
public class Custom_banner_patterns {
    public static final String MODID = "custom_banner_patterns";

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
}