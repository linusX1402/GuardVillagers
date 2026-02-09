package dev.sterner.guardvillagers.common.network;

import dev.sterner.guardvillagers.GuardVillagers;
import dev.sterner.guardvillagers.common.entity.GuardEntity;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record GuardPatrolPacket(int id, boolean pressed) implements CustomPayload {

    public static final CustomPayload.Id<GuardPatrolPacket> ID = new CustomPayload.Id<>(Identifier.of(GuardVillagers.MODID, "guard_patrol"));
    public static final PacketCodec<PacketByteBuf, GuardPatrolPacket> CODEC = PacketCodec.of(GuardPatrolPacket::write, GuardPatrolPacket::read);

    public static void handle(GuardPatrolPacket packet, ServerPlayNetworking.Context context) {
        ServerPlayerEntity serverPlayerEntity = context.player();
        context.server().execute(() -> {
            Entity entity = serverPlayerEntity.getWorld().getEntityById(packet.id);
            if (entity instanceof GuardEntity guardEntity) {
                BlockPos pos = guardEntity.getBlockPos();
                if (guardEntity.getBlockPos() != null) {
                    guardEntity.setPatrolPos(pos);
                }
                guardEntity.setPatrolling(packet.pressed);
            }
        });
    }

    private static GuardPatrolPacket read(PacketByteBuf buf) {
        int id = buf.readInt();
        boolean pressed = buf.readBoolean();
        return new GuardPatrolPacket(id, pressed);
    }

    private static void write(GuardPatrolPacket packet, PacketByteBuf buf) {
        buf.writeInt(packet.id);
        buf.writeBoolean(packet.pressed);
    }

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}