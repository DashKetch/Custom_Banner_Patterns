package dashketch.mods.custom_banner_patterns;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;

import static dashketch.mods.custom_banner_patterns.Custom_banner_patterns.MODID;

public class GUI_HUD_Register {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<PatternLoaderMenu>> PATTERN_LOADER_MENU =
            MENUS.register("pattern_loader_menu", () -> IMenuTypeExtension.create(PatternLoaderMenu::new));
}
