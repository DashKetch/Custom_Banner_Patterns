package dashketch.mods.custom_banner_patterns;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record PrintPatternPacket(String filename) implements CustomPacketPayload {
    public static final Type<PrintPatternPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Custom_banner_patterns.MODID, "print_pattern"));

    // Codec for sending a String over the network
    public static final StreamCodec<RegistryFriendlyByteBuf, PrintPatternPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            PrintPatternPacket::filename,
            PrintPatternPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(final PrintPatternPacket payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            // This runs on the Server!
            if (context.player() instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                // Call the logic we wrote in PatternManager
                PatternManager.printPattern(serverPlayer, payload.filename());
            }
        });
    }
}