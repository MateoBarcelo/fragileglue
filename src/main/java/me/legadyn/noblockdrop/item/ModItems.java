package me.legadyn.noblockdrop.item;

import me.legadyn.noblockdrop.Noblockdrop;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Noblockdrop.MOD_ID);

    public static final RegistryObject<Item> CUSTOM_GLUE_ITEM = ITEMS.register("fragile_glue", () ->
            new NoBlockDropGlueItem(new Item.Properties().stacksTo(1))
    );

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}

