package me.legadyn.noblockdrop.network;

import me.legadyn.noblockdrop.entity.NoBlockDropGlueEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class C2SRemoveGluePacket {
    private final BlockPos pos;
    private final Direction face;

    public C2SRemoveGluePacket(BlockPos pos, Direction face) {
        this.pos = pos;
        this.face = face;
    }

    public C2SRemoveGluePacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.face = buf.readEnum(Direction.class);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeEnum(face);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            Level level = player.level();
            AABB area = new AABB(pos);

            List<NoBlockDropGlueEntity> glues = level.getEntitiesOfClass(NoBlockDropGlueEntity.class,  new AABB(pos).inflate(1.1));
            for (NoBlockDropGlueEntity glue : glues) {
                if (glue.getBoundingBox().intersects(new AABB(pos))) {
                    glue.discard();
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}


