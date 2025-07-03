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
    }

    public NoBlockDropGlueEntity(EntityType<NoBlockDropGlueEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();
    }

    public static boolean isBlockGlued(Level level, BlockPos pos) {
        // Crear AABB exacto del bloque
        AABB blockBB = new AABB(pos);

        List<NoBlockDropGlueEntity> glues = level.getEntitiesOfClass(NoBlockDropGlueEntity.class, blockBB);

        for (NoBlockDropGlueEntity glue : glues) {
            AABB glueBB = glue.getBoundingBox();

            if (glueBB.maxX >= blockBB.minX && glueBB.minX <= blockBB.maxX &&
                    glueBB.maxY >= blockBB.minY && glueBB.minY <= blockBB.maxY &&
                    glueBB.maxZ >= blockBB.minZ && glueBB.minZ <= blockBB.maxZ) {
                return true;
            }
        }

        return false;
    }
}


