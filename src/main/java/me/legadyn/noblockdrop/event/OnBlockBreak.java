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

            if (NoBlockDropGlueEntity.isBlockGlued(level, pos)) {
                event.setCanceled(true); // cancel drop
                BlockState state = level.getBlockState(pos);

                level.removeBlock(pos, false); // false = no drops

                // Remove glue entities touching this block
                for (NoBlockDropGlueEntity glue : level.getEntitiesOfClass(NoBlockDropGlueEntity.class, new AABB(pos).inflate(1.1))) {
                    if (glue.getBoundingBox().contains(Vec3.atLowerCornerOf(pos))) {
                        glue.discard();
                    }
                }

                level.levelEvent(2001, pos, Block.getId(state));
            }
    }

}
