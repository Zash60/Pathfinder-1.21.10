package com.exemplo.visualpath;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.*;

public class Pathfinder {
    private static final int MAX_SEARCH_NODES = 10000;

    public static List<BlockPos> findPath(BlockPos start, BlockPos end, World world) {
        Queue<BlockPos> frontier = new LinkedList<>();
        Map<BlockPos, BlockPos> cameFrom = new HashMap<>();
        
        frontier.add(start);
        cameFrom.put(start, null);
        
        int nodesSearched = 0;

        while (!frontier.isEmpty() && nodesSearched < MAX_SEARCH_NODES) {
            BlockPos current = frontier.poll();
            nodesSearched++;

            if (current.equals(end) || current.getSquaredDistance(end) < 2.0) {
                return reconstructPath(cameFrom, current);
            }

            for (BlockPos next : getNeighbors(current)) {
                if (!cameFrom.containsKey(next) && isWalkable(world, next)) {
                    frontier.add(next);
                    cameFrom.put(next, current);
                }
            }
        }
        return null;
    }

    private static List<BlockPos> getNeighbors(BlockPos pos) {
        List<BlockPos> neighbors = new ArrayList<>();
        neighbors.add(pos.north());
        neighbors.add(pos.south());
        neighbors.add(pos.east());
        neighbors.add(pos.west());
        neighbors.add(pos.up());
        neighbors.add(pos.down());
        return neighbors;
    }

    private static boolean isWalkable(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        BlockState below = world.getBlockState(pos.down());
        // Verifica se o bloco é ar e se tem chão sólido
        return !state.isSolidBlock(world, pos) && below.isSolidBlock(world, pos.down());
    }

    private static List<BlockPos> reconstructPath(Map<BlockPos, BlockPos> cameFrom, BlockPos current) {
        List<BlockPos> path = new ArrayList<>();
        while (current != null) {
            path.add(current);
            current = cameFrom.get(current);
        }
        Collections.reverse(path);
        return path;
    }
}
