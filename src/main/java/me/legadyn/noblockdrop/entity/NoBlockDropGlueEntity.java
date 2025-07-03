package me.legadyn.noblockdrop.entity;

import com.simibubi.create.content.contraptions.glue.SuperGlueEntity;
import me.legadyn.noblockdrop.NoBlockDropGlueTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NoBlockDropGlueEntity extends SuperGlueEntity {

    public NoBlockDropGlueEntity(EntityType<?> type, Level level, AABB boundingBox) {
        super(type, level);
        this.setBoundingBox(boundingBox);
        this.resetPositionToBB(); // centra posición basada en bounding box
    }

    public NoBlockDropGlueEntity(EntityType<NoBlockDropGlueEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();
    }

    public static boolean isGlued(Level level, BlockPos pos, Direction face) {
        BlockPos other = pos.relative(face);
        AABB searchBox = inclusiveBox(pos, other).inflate(0.1); //0.1 default

        List<NoBlockDropGlueEntity> glues = level.getEntitiesOfClass(NoBlockDropGlueEntity.class, searchBox);

        for (NoBlockDropGlueEntity glue : glues) {
            Set<BlockPos> gluedBlocks = getAllBlocksTouchedByGlue(glue);

            if (gluedBlocks.contains(pos) && gluedBlocks.contains(other)) {
                return true;
            }
        }

        return false;
    }

    public static AABB inclusiveBox(BlockPos pos1, BlockPos pos2) {
        int minX = Math.min(pos1.getX(), pos2.getX());
        int minY = Math.min(pos1.getY(), pos2.getY());
        int minZ = Math.min(pos1.getZ(), pos2.getZ());
        int maxX = Math.max(pos1.getX(), pos2.getX());
        int maxY = Math.max(pos1.getY(), pos2.getY());
        int maxZ = Math.max(pos1.getZ(), pos2.getZ());

        return new AABB(
                minX, minY, minZ,
                maxX + 1, maxY + 1, maxZ + 1 // +1 para incluir el extremo
        );
    }

    public static Set<BlockPos> getAllBlocksTouchedByGlue(NoBlockDropGlueEntity glue) {
        AABB bb = glue.getBoundingBox();
        BlockPos min = new BlockPos((int) Math.floor(bb.minX), (int) Math.floor(bb.minY), (int) Math.floor(bb.minZ));
        BlockPos max = new BlockPos((int) Math.floor(bb.maxX), (int) Math.floor(bb.maxY), (int) Math.floor(bb.maxZ));

        Set<BlockPos> touched = new HashSet<>();
        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int y = min.getY(); y <= max.getY(); y++) {
                for (int z = min.getZ(); z <= max.getZ(); z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    AABB blockBox = new AABB(pos);
                    if (glue.getBoundingBox().intersects(blockBox)) {
                        touched.add(pos);
                    }
                }
            }
        }

        return touched;
    }
}


