package me.legadyn.noblockdrop.mixin;

import com.simibubi.create.content.contraptions.glue.SuperGlueEntity;
import me.legadyn.noblockdrop.entity.NoBlockDropGlueEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ServerLevel.class)
public class LevelMixin {

    // cancel drop on blocks
    @Inject(method = "addFreshEntity", at = @At("HEAD"), cancellable = true)
    public void onAddFreshEntity(Entity pEntity, CallbackInfoReturnable<Boolean> cir) {
        if (!(pEntity instanceof ItemEntity item)) return;

        BlockPos pos = item.blockPosition();
        if (NoBlockDropGlueEntity.isBlockGlued((Level)(Object)this, pos) && item.getItem().getItem() instanceof BlockItem) {
            cir.cancel();

            // Remove glue entities touching this block
            for (NoBlockDropGlueEntity glue : ((Level)(Object)this).getEntitiesOfClass(NoBlockDropGlueEntity.class, new AABB(pos).inflate(1.1))) {
                if (glue.getBoundingBox().intersects(new AABB(pos))) {
                    glue.discard();
                }
            }
        }
    }
}
