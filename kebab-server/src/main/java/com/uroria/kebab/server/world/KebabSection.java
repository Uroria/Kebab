package com.uroria.kebab.server.world;

import com.uroria.kebab.api.world.Section;
import com.uroria.kebab.api.block.BlockState;
import com.uroria.kebab.protocol.tag.CompoundTag;
import com.uroria.kebab.protocol.tag.ListTag;
import com.uroria.kebab.protocol.tag.array.ByteArrayTag;
import com.uroria.kebab.protocol.tag.array.LongArrayTag;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.uroria.kebab.server.world.LoadFlags.*;

public class KebabSection implements Section {

    private CompoundTag data;
    private Map<String, List<PaletteIndex>> valueIndexedPalette = new HashMap<>();
    private ListTag<CompoundTag> palette;
    private byte[] blockLight;
    private long[] blockStates;
    private byte[] skyLight;
    private int height;
    int dataVersion;

    public KebabSection(CompoundTag sectionRoot, int dataVersion) {
        this(sectionRoot, dataVersion, ALL_DATA);
    }

    public KebabSection(CompoundTag sectionRoot, int dataVersion, long loadFlags) {
        data = sectionRoot;
        this.dataVersion = dataVersion;
        height = sectionRoot.getNumber("Y").byteValue();

        ListTag<?> rawPalette = sectionRoot.getListTag("Palette");
        if (rawPalette == null) {
            return;
        }
        palette = rawPalette.asCompoundTagList();
        for (int i = 0; i < palette.size(); i++) {
            CompoundTag data = palette.get(i);
            putValueIndexedPalette(data, i);
        }

        ByteArrayTag blockLight = sectionRoot.getByteArrayTag("BlockLight");
        LongArrayTag blockStates = sectionRoot.getLongArrayTag("BlockStates");
        ByteArrayTag skyLight = sectionRoot.getByteArrayTag("SkyLight");

        if ((loadFlags & BLOCK_LIGHTS) != 0) {
            this.blockLight = blockLight != null ? blockLight.getValue() : null;
        }
        if ((loadFlags & BLOCK_STATES) != 0) {
            this.blockStates = blockStates != null ? blockStates.getValue() : null;
        }
        if ((loadFlags & SKY_LIGHT) != 0) {
            this.skyLight = skyLight != null ? skyLight.getValue() : null;
        }
    }

    KebabSection() {}

    void putValueIndexedPalette(CompoundTag data, int index) {
        PaletteIndex leaf = new PaletteIndex(data, index);
        String name = data.getString("Name");
        List<PaletteIndex> leaves = valueIndexedPalette.get(name);
        if (leaves == null) {
            leaves = new ArrayList<>(1);
            leaves.add(leaf);
            valueIndexedPalette.put(name, leaves);
        } else {
            for (PaletteIndex pal : leaves) {
                if (pal.data.equals(data)) {
                    return;
                }
            }
            leaves.add(leaf);
        }
    }

    PaletteIndex getValueIndexedPalette(CompoundTag data) {
        List<PaletteIndex> leaves = valueIndexedPalette.get(data.getString("Name"));
        if (leaves == null) {
            return null;
        }
        for (PaletteIndex leaf : leaves) {
            if (leaf.data.equals(data)) {
                return leaf;
            }
        }
        return null;
    }

    @Override
    public int compareTo(@NotNull Section o) {
        if (o == null) {
            return -1;
        }
        return Integer.compare(height, o.getHeight());
    }

    private static class PaletteIndex {

        CompoundTag data;
        int index;

        PaletteIndex(CompoundTag data, int index) {
            this.data = data;
            this.index = index;
        }
    }

    @Override
    public boolean isEmpty() {
        return data == null;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
    }

    public BlockState getBlockStateAt(int blockX, int blockY, int blockZ) {
        return getBlockStateAt(getBlockIndex(blockX, blockY, blockZ));
    }

    private BlockState getBlockStateAt(int index) {
        int paletteIndex = getPaletteIndex(index);
        return new BlockState(palette.get(paletteIndex));
    }

    public void setBlockStateAt(int blockX, int blockY, int blockZ, CompoundTag state, boolean cleanup) {
        int paletteSizeBefore = palette.size();
        int paletteIndex = addToPalette(state);
        if (paletteSizeBefore != palette.size() && (paletteIndex & (paletteIndex - 1)) == 0) {
            adjustBlockStateBits(null, blockStates);
            cleanup = true;
        }

        setPaletteIndex(getBlockIndex(blockX, blockY, blockZ), paletteIndex, blockStates);

        if (cleanup) {
            cleanupPaletteAndBlockStates();
        }
    }

    public int getPaletteIndex(int blockStateIndex) {
        int bits = blockStates.length >> 6;

        if (dataVersion < 2527) {
            double blockStatesIndex = blockStateIndex / (4096D / blockStates.length);
            int longIndex = (int) blockStatesIndex;
            int startBit = (int) ((blockStatesIndex - Math.floor(blockStatesIndex)) * 64D);
            if (startBit + bits > 64) {
                long prev = bitRange(blockStates[longIndex], startBit, 64);
                long next = bitRange(blockStates[longIndex + 1], 0, startBit + bits - 64);
                return (int) ((next << 64 - startBit) + prev);
            } else {
                return (int) bitRange(blockStates[longIndex], startBit, startBit + bits);
            }
        } else {
            int indicesPerLong = (int) (64D / bits);
            int blockStatesIndex = blockStateIndex / indicesPerLong;
            int startBit = (blockStateIndex % indicesPerLong) * bits;
            return (int) bitRange(blockStates[blockStatesIndex], startBit, startBit + bits);
        }
    }

    public void setPaletteIndex(int blockIndex, int paletteIndex, long[] blockStates) {
        int bits = blockStates.length >> 6;

        if (dataVersion < 2527) {
            double blockStatesIndex = blockIndex / (4096D / blockStates.length);
            int longIndex = (int) blockStatesIndex;
            int startBit = (int) ((blockStatesIndex - Math.floor(longIndex)) * 64D);
            if (startBit + bits > 64) {
                blockStates[longIndex] = updateBits(blockStates[longIndex], paletteIndex, startBit, 64);
                blockStates[longIndex + 1] = updateBits(blockStates[longIndex + 1], paletteIndex, startBit - 64, startBit + bits - 64);
            } else {
                blockStates[longIndex] = updateBits(blockStates[longIndex], paletteIndex, startBit, startBit + bits);
            }
        } else {
            int indicesPerLong = (int) (64D / bits);
            int blockStatesIndex = blockIndex / indicesPerLong;
            int startBit = (blockIndex % indicesPerLong) * bits;
            blockStates[blockStatesIndex] = updateBits(blockStates[blockStatesIndex], paletteIndex, startBit, startBit + bits);
        }
    }

    public ListTag<CompoundTag> getPalette() {
        return palette;
    }

    int addToPalette(CompoundTag data) {
        PaletteIndex index;
        if ((index = getValueIndexedPalette(data)) != null) {
            return index.index;
        }
        palette.add(data);
        putValueIndexedPalette(data, palette.size() - 1);
        return palette.size() - 1;
    }

    int getBlockIndex(int blockX, int blockY, int blockZ) {
        return (blockY & 0xF) * 256 + (blockZ & 0xF) * 16 + (blockX & 0xF);
    }

    static long updateBits(long n, long m, int i, int j) {
        long mShifted = i > 0 ? (m & ((1L << j - i) - 1)) << i : (m & ((1L << j - i) - 1)) >>> -i;
        return ((n & ((j > 63 ? 0 : (~0L << j)) | (i < 0 ? 0 : ((1L << i) - 1L)))) | mShifted);
    }

    static long bitRange(long value, int from, int to) {
        int waste = 64 - to;
        return (value << waste) >>> (waste + from);
    }

    public void cleanupPaletteAndBlockStates() {
        if (blockStates != null) {
            Map<Integer, Integer> oldToNewMapping = cleanupPalette();
            adjustBlockStateBits(oldToNewMapping, blockStates);
        }
    }

    private Map<Integer, Integer> cleanupPalette() {
        //create index - palette mapping
        Map<Integer, Integer> allIndices = new HashMap<>();
        for (int i = 0; i < 4096; i++) {
            int paletteIndex = getPaletteIndex(i);
            allIndices.put(paletteIndex, paletteIndex);
        }

        int index = 1;
        valueIndexedPalette = new HashMap<>(valueIndexedPalette.size());
        putValueIndexedPalette(palette.get(0), 0);
        for (int i = 1; i < palette.size(); i++) {
            if (!allIndices.containsKey(index)) {
                palette.remove(i);
                i--;
            } else {
                putValueIndexedPalette(palette.get(i), i);
                allIndices.put(index, i);
            }
            index++;
        }

        return allIndices;
    }

    void adjustBlockStateBits(Map<Integer, Integer> oldToNewMapping, long[] blockStates) {
        int newBits = 32 - Integer.numberOfLeadingZeros(palette.size() - 1);
        newBits = Math.max(newBits, 4);

        long[] newBlockStates;

        if (dataVersion < 2527) {
            newBlockStates = newBits == blockStates.length / 64 ? blockStates : new long[newBits * 64];
        } else {
            int newLength = (int) Math.ceil(4096D / (Math.floor(64D / newBits)));
            newBlockStates = newBits == blockStates.length / 64 ? blockStates : new long[newLength];
        }
        if (oldToNewMapping != null) {
            for (int i = 0; i < 4096; i++) {
                setPaletteIndex(i, oldToNewMapping.get(getPaletteIndex(i)), newBlockStates);
            }
        } else {
            for (int i = 0; i < 4096; i++) {
                setPaletteIndex(i, getPaletteIndex(i), newBlockStates);
            }
        }
        this.blockStates = newBlockStates;
    }

    public byte[] getBlockLight() {
        return blockLight;
    }

    public void setBlockLight(byte[] blockLight) {
        if (blockLight != null && blockLight.length != 2048) {
            throw new IllegalArgumentException("BlockLight array must have a length of 2048");
        }
        this.blockLight = blockLight;
    }

    public long[] getBlockStates() {
        return blockStates;
    }

    public void setBlockStates(long[] blockStates) {
        if (blockStates == null) {
            throw new NullPointerException("BlockStates cannot be null");
        } else if (blockStates.length % 64 != 0 || blockStates.length < 256 || blockStates.length > 4096) {
            throw new IllegalArgumentException("BlockStates must have a length > 255 and < 4097 and must be divisible by 64");
        }
        this.blockStates = blockStates;
    }

    public byte[] getSkyLight() {
        return skyLight;
    }

    public void setSkyLight(byte[] skyLight) {
        if (skyLight != null && skyLight.length != 2048) {
            throw new IllegalArgumentException("SkyLight array must have a length of 2048");
        }
        this.skyLight = skyLight;
    }

    public static KebabSection newSection() {
        KebabSection s = new KebabSection();
        s.blockStates = new long[256];
        s.palette = new ListTag<>(CompoundTag.class);
        CompoundTag air = new CompoundTag();
        air.putString("Name", "minecraft:air");
        s.palette.add(air);
        s.data = new CompoundTag();
        return s;
    }

    public CompoundTag updateHandle(int y) {
        data.putByte("Y", (byte) y);
        if (palette != null) {
            data.put("Palette", palette);
        }
        if (blockLight != null) {
            data.putByteArray("BlockLight", blockLight);
        }
        if (blockStates != null) {
            data.putLongArray("BlockStates", blockStates);
        }
        if (skyLight != null) {
            data.putByteArray("SkyLight", skyLight);
        }
        return data;
    }

    public CompoundTag updateHandle() {
        return updateHandle(height);
    }

    public Iterable<CompoundTag> blocksStates() {
        return new BlockIterator(this);
    }

    private static class BlockIterator implements Iterable<CompoundTag>, Iterator<CompoundTag> {

        private KebabSection section;
        private int currentIndex;

        public BlockIterator(KebabSection section) {
            this.section = section;
            currentIndex = 0;
        }

        @Override
        public boolean hasNext() {
            return currentIndex < 4096;
        }

        @Override
        public CompoundTag next() {
            CompoundTag blockState = section.getBlockStateAt(currentIndex);
            currentIndex++;
            return blockState;
        }

        @Override
        public Iterator<CompoundTag> iterator() {
            return this;
        }
    }
}
