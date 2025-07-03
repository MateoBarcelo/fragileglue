package me.legadyn.noblockdrop.mixin;

import com.simibubi.create.content.contraptions.glue.SuperGlueEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SuperGlueEntity.class)

    public abstract class NoBlockDropGlueMixin {

        @Inject(method = "tick", at = @At("HEAD"))
        public void onTick(CallbackInfo ci) {

            //System.out.println("Glue personalizada en tick!");

        }

        // Podés inyectar en métodos como 'onHit', 'onUse', etc.
    }

