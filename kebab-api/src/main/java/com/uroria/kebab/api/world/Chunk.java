package com.uroria.kebab.api.world;

import com.uroria.kebab.api.block.BlockPosition;
import com.uroria.kebab.api.block.BlockState;
import com.uroria.kebab.api.world.Section;

public interface Chunk extends Iterable<Section> {

    BlockState getBlockStateAt(int blockX, int blockY, int blockZ);

    BlockState getBlockStateAt(BlockPosition position);

    void setBlockStateAt(int blockX, int blockY, int blockZ, BlockState state);

    void setBlockStateAt(int blockX, int blockY, int blockZ, BlockState state, boolean cleanup);

    void setBlockStateAt(BlockPosition position, BlockState state);

    void setBlockStateAt(BlockPosition position, BlockState state, boolean cleanup);

    int getDataVersion();
}
