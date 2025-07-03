package me.legadyn.noblockdrop.item;

import com.simibubi.create.content.contraptions.glue.SuperGlueItem;
import jdk.swing.interop.SwingInterOpUtils;
import me.legadyn.noblockdrop.GlueClick;
import me.legadyn.noblockdrop.GlueClickTracker;
import me.legadyn.noblockdrop.NoBlockDropGlueTracker;
import me.legadyn.noblockdrop.entity.ModEntities;
import me.legadyn.noblockdrop.entity.NoBlockDropGlueEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class NoBlockDropGlueItem extends Item {
    public NoBlockDropGlueItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;

        BlockPos currentPos = context.getClickedPos();
        Direction currentFace = context.getClickedFace();

        GlueClick lastClick = GlueClickTracker.get(player);

        if (lastClick == null) {
            GlueClickTracker.set(player, currentPos, currentFace);
            player.displayClientMessage(Component.literal("Primer bloque seleccionado"), true);
            return InteractionResult.SUCCESS;
        }

        // Segundo clic: pegamos
        BlockPos first = lastClick.pos();
        BlockPos second = currentPos;

        if (first.equals(second)) {
            player.displayClientMessage(Component.literal("No se puede pegar el mismo bloque"), true);
            return InteractionResult.FAIL;
        }

        /*AABB glueBox = inclusiveBox(lastClick.pos(), context.getClickedPos());

        NoBlockDropGlueEntity glueEntity = new NoBlockDropGlueEntity(
                ModEntities.CUSTOM_GLUE.get(), level, glueBox
        );

        level.addFreshEntity(glueEntity);
        glueEntity.playPlaceSound();*/

        for (BlockPos pos : getAllBlocksBetween(context.getClickedPos(), lastClick.pos())) {
            AABB bb = new AABB(pos);
            NoBlockDropGlueEntity glue = new NoBlockDropGlueEntity(ModEntities.CUSTOM_GLUE.get(), level, bb);
            glue.setPos(Vec3.atCenterOf(pos));
            level.addFreshEntity(glue);
        }

        player.displayClientMessage(Component.literal("¡Bloques pegados!"), true);

        GlueClickTracker.clear(player);
        return InteractionResult.CONSUME;
    }

    public static AABB inclusiveBox(BlockPos pos1, BlockPos pos2) {
        int minX = Math.min(pos1.getX(), pos2.getX());
        int minY = Math.min(pos1.getY(), pos2.getY());
        int minZ = Math.min(pos1.getZ(), pos2.getZ());
        int maxX = Math.max(pos1.getX(), pos2.getX());
        int maxY = Math.max(pos1.getY(), pos2.getY());
        int maxZ = Math.max(pos1.getZ(), pos2.getZ());

        return new AABB(
                minX, minY, minZ,
                maxX + 1, maxY + 1, maxZ + 1 // +1 para incluir el extremo
        );
    }

    public static Iterable<BlockPos> getAllBlocksBetween(BlockPos a, BlockPos b) {
        int minX = Math.min(a.getX(), b.getX());
        int minY = Math.min(a.getY(), b.getY());
        int minZ = Math.min(a.getZ(), b.getZ());
        int maxX = Math.max(a.getX(), b.getX());
        int maxY = Math.max(a.getY(), b.getY());
        int maxZ = Math.max(a.getZ(), b.getZ());

        List<BlockPos> blocks = new ArrayList<>();
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    blocks.add(new BlockPos(x, y, z));
                }
            }
        }
        return blocks;
    }

}



