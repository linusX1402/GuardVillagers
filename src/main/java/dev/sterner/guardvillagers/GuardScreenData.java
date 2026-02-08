package dev.sterner.guardvillagers;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record GuardScreenData(int entityId) {
    public static final PacketCodec<RegistryByteBuf, GuardScreenData> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.VAR_INT, GuardScreenData::entityId,
                    GuardScreenData::new
            );
}