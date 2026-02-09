package dev.sterner.guardvillagers.common.network;

import dev.sterner.guardvillagers.GuardVillagers;
import dev.sterner.guardvillagers.common.entity.GuardEntity;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public record GuardFollowPacket(int id) implements CustomPayload {

    public static final CustomPayload.Id<GuardFollowPacket> ID = new CustomPayload.Id<>(Identifier.of(GuardVillagers.MODID, "guard_follow"));
    public static final PacketCodec<PacketByteBuf, GuardFollowPacket> CODEC = PacketCodec.of(GuardFollowPacket::write, GuardFollowPacket::read);

    public static void handle(GuardFollowPacket packet, ServerPlayNetworking.Context context) {
        ServerPlayerEntity serverPlayerEntity = context.player();
        context.server().execute(() -> {
            Entity entity = serverPlayerEntity.getWorld().getEntityById(packet.id);
            if (entity instanceof GuardEntity guardEntity) {
                guardEntity.setFollowing(!guardEntity.isFollowing());
                guardEntity.setOwnerId(serverPlayerEntity.getUuid());
                guardEntity.playSound(SoundEvents.ENTITY_VILLAGER_YES, 1, 1);
            }
        });
    }

    public static GuardFollowPacket read(PacketByteBuf buf) {
        return new GuardFollowPacket(buf.readInt());
    }

    public static void write(GuardFollowPacket packet, PacketByteBuf buf) {
        buf.writeInt(packet.id);
    }

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}