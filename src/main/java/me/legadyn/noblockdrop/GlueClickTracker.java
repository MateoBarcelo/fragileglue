package me.legadyn.noblockdrop;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GlueClickTracker {
    private static final Map<UUID, GlueClick> lastClicks = new HashMap<>();

    public static void set(Player player, BlockPos pos, Direction face) {
        lastClicks.put(player.getUUID(), new GlueClick(pos, face));
    }

    public static @Nullable
    GlueClick get(Player player) {
        return lastClicks.get(player.getUUID());
    }

    public static void clear(Player player) {
        lastClicks.remove(player.getUUID());
    }
}

