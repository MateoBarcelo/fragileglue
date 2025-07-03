package me.legadyn.noblockdrop.event;

import me.legadyn.noblockdrop.NoBlockDropGlueTracker;
import me.legadyn.noblockdrop.Noblockdrop;
import me.legadyn.noblockdrop.entity.NoBlockDropGlueEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Noblockdrop.MOD_ID)
public class OnBlockBreak {
        @SubscribeEvent
        public static void onBlockBreak(BlockEvent.BreakEvent event) {
            Level level = (Level) event.getLevel();
            BlockPos pos = event.getPos();

            System.out.printf("is it %s block %s", NoBlockDropGlueTracker.isGlued(level, pos), pos.toString());
            for (Direction direction : Direction.values()) {
                if (NoBlockDropGlueEntity.isGlued(level, pos, direction)) {
                    // Evitar drops
                    event.setCanceled(true);
                    System.out.println("glued!");
                    // Rompemos el bloque manualmente pero sin drops
                    BlockState state = level.getBlockState(pos);

                    level.removeBlock(pos, false); // false = no drops

                    // Remove glue entities touching this block
                    for (NoBlockDropGlueEntity glue : level.getEntitiesOfClass(NoBlockDropGlueEntity.class, new AABB(pos).inflate(1.1))) {
                        if (glue.getBoundingBox().contains(Vec3.atCenterOf(pos))) {
                            glue.discard();

                            System.out.println("Removed glue entity at " + glue.getBoundingBox());
                        }
                    }

                    // Si querés hacer efectos extra
                    level.levelEvent(2001, pos, Block.getId(state)); // Partículas de rotura

                    // Despegamos el bloque
                    NoBlockDropGlueTracker.unglue(level, pos);
                    return;
                }
            }

    }

}
