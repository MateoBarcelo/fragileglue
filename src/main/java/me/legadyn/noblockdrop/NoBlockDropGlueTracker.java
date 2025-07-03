package me.legadyn.noblockdrop;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.*;

public class NoBlockDropGlueTracker {

        private static final Map<Level, Set<BlockPos>> gluedBlocks = new WeakHashMap<>();

        public static void glue(Level level, BlockPos pos) {
            gluedBlocks.computeIfAbsent(level, l -> new HashSet<>()).add(pos.immutable());
        }

        public static boolean isGlued(Level level, BlockPos pos) {
            return gluedBlocks.getOrDefault(level, Collections.emptySet()).contains(pos);
        }

        public static void unglue(Level level, BlockPos pos) {
            gluedBlocks.getOrDefault(level, Collections.emptySet()).remove(pos);

    }

}
