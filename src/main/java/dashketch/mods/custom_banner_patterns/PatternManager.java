package dashketch.mods.custom_banner_patterns;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.fml.loading.FMLPaths;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class PatternManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final List<String> CUSTOM_PATTERNS = new ArrayList<>();
    private static final Path PATTERN_PATH = FMLPaths.CONFIGDIR.get().resolve("custom_patterns");

    public static void loadPatterns() {
        CUSTOM_PATTERNS.clear();
        if (!Files.exists(PATTERN_PATH)) return;

        try (Stream<Path> stream = Files.list(PATTERN_PATH)) {
            List<String> foundFiles = stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .filter(name -> name.endsWith(".txt"))
                    .map(name -> name.replace(".txt", ""))
                    .toList();

            CUSTOM_PATTERNS.addAll(foundFiles);
            LOGGER.info("Loaded {} custom banner patterns.", CUSTOM_PATTERNS.size());
        } catch (IOException e) {
            LOGGER.error("Failed to scan custom_patterns directory", e);
        }
    }

    public static List<String> readPatternContent(String patternName) {
        try {
            Path filePath = PATTERN_PATH.resolve(patternName + ".txt");
            if (Files.exists(filePath)) return Files.readAllLines(filePath);
        } catch (IOException e) {
            LOGGER.error("Failed to read pattern: {}", patternName, e);
        }
        return List.of("Error reading file");
    }

    // UPDATED: Logic to run on the Server
    public static void printPattern(ServerPlayer player, String filename) {
        // 1. Check for Ingredients
        // We need: 1 Feather, 1 Ink Sac, 1 Empty Pattern
        boolean hasFeather = player.getInventory().countItem(Items.FEATHER) > 0;
        boolean hasInk = player.getInventory().countItem(Items.INK_SAC) > 0;
        boolean hasPattern = player.getInventory().countItem(ItemsBlocksRegister.EMPTY_PATTERN.get()) > 0;

        if (hasFeather && hasInk && hasPattern) {
            // 2. Consume Items (Remove 1 of each)
            consumeItem(player, Items.FEATHER);
            consumeItem(player, Items.INK_SAC);
            consumeItem(player, ItemsBlocksRegister.EMPTY_PATTERN.get());

            // 3. Create the New Item
            // We use the same 'Empty Pattern' item but fill it with NBT data
            ItemStack filledPattern = new ItemStack(ItemsBlocksRegister.EMPTY_PATTERN.get());

            // 4. Rename Item (Italicized Filename)
            filledPattern.set(DataComponents.CUSTOM_NAME, Component.literal(filename).withStyle(ChatFormatting.ITALIC));

            // 5. Store the Pattern ID in Custom Data (NBT)
            // This allows us to read "filename" later when we put it in a Loom or Renderer
            CompoundTag tag = new CompoundTag();
            tag.putString("PatternID", filename);
            filledPattern.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));

            // 6. Give to Player
            if (!player.getInventory().add(filledPattern)) {
                // If inventory is full, drop it on the ground
                player.drop(filledPattern, false);
            }

            // Optional: Play a sound to confirm success
            // player.level().playSound(null, player.blockPosition(), SoundEvents.UI_LOOM_TAKE_RESULT, SoundSource.BLOCKS, 1.0f, 1.0f);
        } else {
            player.sendSystemMessage(Component.literal("Missing Materials! Need: Feather, Ink Sac, Empty Pattern."));
        }
    }

    // Helper to safely remove 1 item
    private static void consumeItem(ServerPlayer player, net.minecraft.world.item.Item item) {
        int slot = player.getInventory().findSlotMatchingItem(new ItemStack(item));
        if (slot != -1) {
            player.getInventory().getItem(slot).shrink(1);
        }
    }

    public static List<String> getCustomPatterns() {
        return CUSTOM_PATTERNS;
    }
}