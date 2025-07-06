package me.legadyn.noblockdrop.entity;

import me.legadyn.noblockdrop.Noblockdrop;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Noblockdrop.MOD_ID);

    public static final RegistryObject<EntityType<NoBlockDropGlueEntity>> CUSTOM_GLUE =
            ENTITIES.register("fragile_glue", () ->
                    EntityType.Builder.<NoBlockDropGlueEntity>of(NoBlockDropGlueEntity::new, MobCategory.MISC)
                            .sized(0.5f, 0.5f)            .clientTrackingRange(10)
                            .updateInterval(20)
                            .build("fragile_glue")
            );

    public static void register(IEventBus bus) {
        ENTITIES.register(bus);
    }
}
