package me.legadyn.noblockdrop.mixin;

import me.legadyn.noblockdrop.entity.NoBlockDropGlueEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(Explosion.class)
public class ExplosionMixin {

    @Inject(method = "finalizeExplosion", at = @At("HEAD"))
    public void onFinalizeExplosion(boolean spawnParticles, CallbackInfo ci) {
        Explosion explosion = (Explosion)(Object)this;
        Level level = ((ExplosionAccessor) explosion).getLevel();

        List<BlockPos> toRemove = new ArrayList<>();

        for (BlockPos pos : explosion.getToBlow()) {
            if (NoBlockDropGlueEntity.isBlockGlued(level, pos)) {
                toRemove.add(pos);
            }
        }

        explosion.getToBlow().removeAll(toRemove);
    }
}
