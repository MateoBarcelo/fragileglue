package me.legadyn.noblockdrop.mixin;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(Explosion.class)
public interface ExplosionAccessor {
    @Accessor("level")
    Level getLevel();

    @Accessor("toBlow")
    ObjectArrayList<BlockPos> getToBlow();
}

