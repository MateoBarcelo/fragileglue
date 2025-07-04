package me.legadyn.noblockdrop.entity;

import me.legadyn.noblockdrop.item.NoBlockDropGlueItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import java.util.List;

public class NoBlockDropGlueEntity extends Entity {

    private Direction direction = Direction.NORTH;

    public NoBlockDropGlueEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    public NoBlockDropGlueEntity(EntityType<?> type, Level level, Direction direction, BlockPos pos) {
        this(type, level);
        this.direction = direction;
        this.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        this.setBoundingBox(makeBoundingBox(pos, direction));
    }

    public Direction getDirection() {
        return direction;
    }

    private static AABB makeBoundingBox(BlockPos pos, Direction direction) {
        // BBox delgada en la cara del bloque
        double thickness = 0.01;
        double x1 = pos.getX();
        double y1 = pos.getY();
        double z1 = pos.getZ();
        double x2 = x1 + 1;
        double y2 = y1 + 1;
        double z2 = z1 + 1;

        switch (direction) {
            case NORTH -> z1 = z2 - thickness;
            case SOUTH -> z2 = z1 + thickness;
            case WEST -> x1 = x2 - thickness;
            case EAST -> x2 = x1 + thickness;
            case DOWN -> y1 = y2 - thickness;
            case UP -> y2 = y1 + thickness;
        }

        return new AABB(x1, y1, z1, x2, y2, z2);
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("Facing")) {
            direction = Direction.from3DDataValue(tag.getByte("Facing"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putByte("Facing", (byte) direction.get3DDataValue());
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public static boolean isBlockGlued(Level level, BlockPos pos) {
        AABB box = new AABB(pos);
        List<NoBlockDropGlueEntity> glues = level.getEntitiesOfClass(NoBlockDropGlueEntity.class, box);

        for (NoBlockDropGlueEntity glue : glues) {
            if (glue.getBoundingBox().intersects(box.deflate(0.05f))) {
                return true;
            }
        }
        return false;
    }
    @Override
    public boolean isAttackable() {
        return true;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!this.level().isClientSide) {
            if (source.getEntity() instanceof Player player) {
                if (player.getMainHandItem().getItem() instanceof NoBlockDropGlueItem) {
                    this.discard();
                    return true; // Ya manejamos el daño
                }
            }
        }
        return false; // Ignorar otros tipos de daño
    }

}
