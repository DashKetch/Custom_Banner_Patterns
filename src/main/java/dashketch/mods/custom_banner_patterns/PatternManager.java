package dashketch.mods.custom_banner_patterns;

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

        if (!Files.exists(PATTERN_PATH)) {
            return;
        }

        try (Stream<Path> stream = Files.list(PATTERN_PATH)) {
            List<String> foundFiles = stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .filter(name -> name.endsWith(".txt"))
                    .map(name -> name.replace(".txt", "")) // Remove extension for the UI
                    .toList();

            CUSTOM_PATTERNS.addAll(foundFiles);
            LOGGER.info("Loaded {} custom banner patterns from config.", CUSTOM_PATTERNS.size());
        } catch (IOException e) {
            LOGGER.error("Failed to scan custom_patterns directory", e);
        }
    }

    public static List<String> readPatternContent(String patternName) {
        try {
            Path filePath = PATTERN_PATH.resolve(patternName + ".txt");
            if (Files.exists(filePath)) {
                return Files.readAllLines(filePath);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to read pattern: {}", patternName, e);
        }
        return List.of("Error reading file", "or file not found.");
    }

    public static List<String> getCustomPatterns() {
        return CUSTOM_PATTERNS;
    }
}