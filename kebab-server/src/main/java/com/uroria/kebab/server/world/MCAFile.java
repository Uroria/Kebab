package com.uroria.kebab.server.world;

import com.uroria.kebab.protocol.tag.CompoundTag;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Iterator;

public final class MCAFile implements Iterable<KebabChunk> {
    public static final int DEFAULT_DATA_VERSION = 1628;

    private final int regionX, regionZ;
    private KebabChunk[] chunks;

    MCAFile(int regionX, int regionZ) {
        this.regionX = regionX;
        this.regionZ = regionZ;
    }

    public void deserialize(RandomAccessFile raf) throws IOException {
        deserialize(raf, LoadFlags.ALL_DATA);
    }

    public void deserialize(RandomAccessFile raf, long loadFlags) throws IOException {
        chunks = new KebabChunk[1024];
        for (int i = 0; i < 1024; i++) {
            raf.seek(i * 4);
            int offset = raf.read() << 16;
            offset |= (raf.read() & 0xFF) << 8;
            offset |= raf.read() & 0xFF;
            if (raf.readByte() == 0) {
                continue;
            }
            raf.seek(4096 + i * 4);
            int timestamp = raf.readInt();
            KebabChunk chunk = new KebabChunk(timestamp);
            raf.seek(4096 * offset + 4);
            chunk.deserialize(raf, loadFlags);
            chunks[i] = chunk;
        }
    }

    public int serialize(RandomAccessFile raf) throws IOException {
        return serialize(raf, false);
    }

    public int serialize(RandomAccessFile raf, boolean changeLastUpdate) throws IOException {
        int globalOffset = 2;
        int lastWritten = 0;
        int timestamp = (int) (System.currentTimeMillis() / 1000L);
        int chunksWritten = 0;
        int chunkXOffset = MCAUtil.regionToChunk(regionX);
        int chunkZOffset = MCAUtil.regionToChunk(regionZ);

        if (chunks == null) {
            return 0;
        }

        for (int cx = 0; cx < 32; cx++) {
            for (int cz = 0; cz < 32; cz++) {
                int index = getChunkIndex(cx, cz);
                KebabChunk chunk = chunks[index];
                if (chunk == null) {
                    continue;
                }
                raf.seek(4096 * globalOffset);
                lastWritten = chunk.serialize(raf, chunkXOffset + cx, chunkZOffset + cz);

                if (lastWritten == 0) {
                    continue;
                }

                chunksWritten++;

                int sectors = (lastWritten >> 12) + (lastWritten % 4096 == 0 ? 0 : 1);

                raf.seek(index * 4);
                raf.writeByte(globalOffset >>> 16);
                raf.writeByte(globalOffset >> 8 & 0xFF);
                raf.writeByte(globalOffset & 0xFF);
                raf.writeByte(sectors);

                raf.seek(index * 4 + 4096);
                raf.writeInt(changeLastUpdate ? timestamp : chunk.getLastMCAUpdate());

                globalOffset += sectors;
            }
        }

        if (lastWritten % 4096 != 0) {
            raf.seek(globalOffset * 4096 - 1);
            raf.write(0);
        }
        return chunksWritten;
    }

    public void setChunk(int index, KebabChunk chunk) {
        checkIndex(index);
        if (chunks == null) {
            chunks = new KebabChunk[1024];
        }
        chunks[index] = chunk;
    }

    public void setChunk(int chunkX, int chunkZ, KebabChunk chunk) {
        setChunk(getChunkIndex(chunkX, chunkZ), chunk);
    }

    public KebabChunk getChunk(int index) {
        checkIndex(index);
        if (chunks == null) {
            return null;
        }
        return chunks[index];
    }

    public KebabChunk getChunk(int chunkX, int chunkZ) {
        return getChunk(getChunkIndex(chunkX, chunkZ));
    }

    public static int getChunkIndex(int chunkX, int chunkZ) {
        return (chunkX & 0x1F) + (chunkZ & 0x1F) * 32;
    }

    private int checkIndex(int index) {
        if (index < 0 || index > 1023) {
            throw new IndexOutOfBoundsException();
        }
        return index;
    }

    private KebabChunk createChunkIfMissing(int blockX, int blockZ) {
        int chunkX = MCAUtil.blockToChunk(blockX), chunkZ = MCAUtil.blockToChunk(blockZ);
        KebabChunk chunk = getChunk(chunkX, chunkZ);
        if (chunk == null) {
            chunk = KebabChunk.newChunk();
            setChunk(getChunkIndex(chunkX, chunkZ), chunk);
        }
        return chunk;
    }

    @Deprecated
    public void setBiomeAt(int blockX, int blockZ, int biomeID) {
        createChunkIfMissing(blockX, blockZ).setBiomeAt(blockX, blockZ, biomeID);
    }

    public void setBiomeAt(int blockX, int blockY, int blockZ, int biomeID) {
        createChunkIfMissing(blockX, blockZ).setBiomeAt(blockX, blockY, blockZ, biomeID);
    }

    public int getBiomeAt(int blockX, int blockY, int blockZ) {
        int chunkX = MCAUtil.blockToChunk(blockX), chunkZ = MCAUtil.blockToChunk(blockZ);
        KebabChunk chunk = getChunk(getChunkIndex(chunkX, chunkZ));
        if (chunk == null) {
            return -1;
        }
        return chunk.getBiomeAt(blockX,blockY, blockZ);
    }

    public void setBlockStateAt(int blockX, int blockY, int blockZ, CompoundTag state, boolean cleanup) {
        createChunkIfMissing(blockX, blockZ).setBlockStateAt(blockX, blockY, blockZ, state, cleanup);
    }

    public CompoundTag getBlockStateAt(int blockX, int blockY, int blockZ) {
        int chunkX = MCAUtil.blockToChunk(blockX), chunkZ = MCAUtil.blockToChunk(blockZ);
        KebabChunk chunk = getChunk(chunkX, chunkZ);
        if (chunk == null) {
            return null;
        }
        return chunk.getBlockStateAt(blockX, blockY, blockZ);
    }

    public void cleanupPalettesAndBlockStates() {
        for (KebabChunk chunk : chunks) {
            if (chunk != null) {
                chunk.cleanupPalettesAndBlockStates();
            }
        }
    }

    @Override
    public Iterator<KebabChunk> iterator() {
        return Arrays.stream(chunks).iterator();
    }
}
