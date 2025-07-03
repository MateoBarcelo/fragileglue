package me.legadyn.noblockdrop;

import com.mojang.logging.LogUtils;
import com.simibubi.create.content.contraptions.glue.SuperGlueRenderer;
import me.legadyn.noblockdrop.entity.ModEntities;
import me.legadyn.noblockdrop.event.OnBlockBreak;
import me.legadyn.noblockdrop.item.ModItems;
import me.legadyn.noblockdrop.renderer.NoBlockDropGlueRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;


// The value here should match an entry in the META-INF/mods.toml file
@Mod(Noblockdrop.MOD_ID)
public class Noblockdrop {
    public static final String MOD_ID = "fragileglue";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Noblockdrop() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
        ModEntities.register(modEventBus);
        ModItems.register(modEventBus);
        modEventBus.register(new OnBlockBreak());
        modEventBus.addListener(this::addCreative);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

        }

        @SubscribeEvent
        public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
            System.out.println("RENDERER EVENT");
            event.registerEntityRenderer(ModEntities.CUSTOM_GLUE.get(), NoBlockDropGlueRenderer::new);
        }
    }
}