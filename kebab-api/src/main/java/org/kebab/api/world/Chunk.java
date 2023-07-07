package org.kebab.api.world;

import org.kebab.api.block.BlockPosition;
import org.kebab.api.block.BlockState;

public interface Chunk extends Iterable<Section> {

    BlockState getBlockStateAt(int blockX, int blockY, int blockZ);

    BlockState getBlockStateAt(BlockPosition position);

    void setBlockStateAt(int blockX, int blockY, int blockZ, BlockState state);

    void setBlockStateAt(int blockX, int blockY, int blockZ, BlockState state, boolean cleanup);

    void setBlockStateAt(BlockPosition position, BlockState state);

    void setBlockStateAt(BlockPosition position, BlockState state, boolean cleanup);

    int getDataVersion();
}
