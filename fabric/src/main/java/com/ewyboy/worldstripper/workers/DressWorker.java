package com.ewyboy.worldstripper.workers;

import com.ewyboy.worldstripper.stripclub.StripperCache;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.Deque;
import java.util.LinkedList;

public class DressWorker implements WorldWorker.IWorker {

    protected final BlockPos start;
    protected final int radiusX;
    protected final int radiusZ;
    private final int total;
    private final ServerWorld dim;
    private final Deque<BlockPos> queue;
    private final int notificationFrequency;
    private int lastNotification = 0;
    private long lastNotificationTime = 0;
    private int blockUpdateFlag;

    public DressWorker(BlockPos start, int radiusX, int radiusZ, ServerWorld dim, int interval, int blockUpdateFlag) {
        this.start = start;
        this.radiusX = radiusX;
        this.radiusZ = radiusZ;
        this.dim = dim;
        this.queue = dressQueue();
        this.total = queue.size();
        this.notificationFrequency = interval != -1 ? interval : Math.max(((radiusX + radiusZ) / 2) / 10, 100); // Every 5% or every 100, whichever is more.
        this.lastNotificationTime = System.currentTimeMillis(); // We also notify at least once every 60 seconds, to show we haven't frozen.
        this.blockUpdateFlag = blockUpdateFlag;
    }

    private Deque<BlockPos> dressQueue() {
        final Deque<BlockPos> queue = new LinkedList<>();
        final BlockPos neg = new BlockPos(start.getX() - radiusX, 0, start.getZ() - radiusZ);
        final BlockPos pos = new BlockPos(start.getX() + radiusX, 255, start.getZ() + radiusZ);

        BlockPos.stream(neg, pos)
                .map(BlockPos :: toImmutable)
                .filter(StripperCache.hashedBlockCache :: containsKey)
                .forEach(queue :: add);
        return queue;
    }

    private boolean isBlockCached(BlockPos next) {
        return StripperCache.hashedBlockCache.containsKey(next);
    }

    public boolean hasWork() {
        return queue.size() > 0;
    }

    public boolean doWork() {
        BlockPos next;
        do {
            next = queue.pollLast();
        } while (
            (next == null || !isBlockCached(next)) && !queue.isEmpty()
        );

        if (next != null) {
            if (++lastNotification >= notificationFrequency || lastNotificationTime < System.currentTimeMillis() - 60 * 1000) {
                lastNotification = 0;
                lastNotificationTime = System.currentTimeMillis();
            }
            dim.setBlockState(next, StripperCache.hashedBlockCache.remove(next), blockUpdateFlag);
        }

        return queue.size() != 0;
    }

}