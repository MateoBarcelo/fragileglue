package me.legadyn.noblockdrop;

import com.mojang.logging.LogUtils;
import me.legadyn.noblockdrop.entity.ModEntities;
import me.legadyn.noblockdrop.entity.NoBlockDropGlueEntity;
import me.legadyn.noblockdrop.event.OnBlockBreak;
import me.legadyn.noblockdrop.item.ModItems;
import me.legadyn.noblockdrop.item.NoBlockDropGlueItem;
import me.legadyn.noblockdrop.network.C2SRemoveGluePacket;
import me.legadyn.noblockdrop.network.ModNetworking;
import me.legadyn.noblockdrop.renderer.NoBlockDropGlueRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.TickEvent;
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
        ModNetworking.register();
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
            event.registerEntityRenderer(ModEntities.CUSTOM_GLUE.get(), NoBlockDropGlueRenderer::new);
        }

        @Mod.EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT)
        public class ClientEvents {

            @SubscribeEvent
            public static void onClientTick(TickEvent.ClientTickEvent event) {
                Minecraft mc = Minecraft.getInstance();

                if (mc.level == null || mc.player == null || mc.screen != null) return;

                // Solo si tiene el ítem en la mano
                if (!(mc.player.getMainHandItem().getItem() instanceof NoBlockDropGlueItem)) return;

                // Detectar click izquierdo
                if (mc.options.keyAttack.isDown()) {
                    if (mc.hitResult instanceof BlockHitResult bhr) {
                        BlockPos pos = bhr.getBlockPos();
                        Direction face = bhr.getDirection();

                        if (NoBlockDropGlueEntity.isBlockGlued(mc.level, pos)) {
                            ModNetworking.INSTANCE.sendToServer(new C2SRemoveGluePacket(pos, face));
                        }
                    }
                }
            }
        }

    }
}