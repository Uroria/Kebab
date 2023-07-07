package org.kebab.server.world;

import org.kebab.api.world.Chunk;
import org.kebab.api.world.Section;
import org.kebab.api.block.BlockPosition;
import org.kebab.api.block.BlockState;
import org.kebab.protocol.nbt.NBTDeserializer;
import org.kebab.protocol.nbt.NBTSerializer;
import org.kebab.protocol.tag.CompoundTag;
import org.kebab.protocol.tag.ListTag;
import org.kebab.protocol.tag.NamedTag;
import org.kebab.protocol.utils.CompressionType;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public final class KebabChunk implements Chunk {

    public static final int DEFAULT_DATA_VERSION = 2567;

    private boolean partial;
    private boolean raw;

    private int lastMCAUpdate;

    private CompoundTag data;

    private final Map<Integer, KebabSection> sections = new TreeMap<>();
    private int dataVersion;
    private long lastUpdate;
    private long inhabitedTime;
    private int[] biomes;
    private CompoundTag heightMaps;
    private CompoundTag carvingMasks;
    private ListTag<CompoundTag> entities;
    private ListTag<CompoundTag> tileEntities;
    private ListTag<CompoundTag> tileTicks;
    private ListTag<CompoundTag> liquidTicks;
    private ListTag<ListTag<?>> lights;
    private ListTag<ListTag<?>> liquidsToBeTicked;
    private ListTag<ListTag<?>> toBeTicked;
    private ListTag<ListTag<?>> postProcessing;
    private String status;
    private CompoundTag structures;

    KebabChunk(int lastMCAUpdate) {
        this.lastMCAUpdate = lastMCAUpdate;
    }

    public KebabChunk(CompoundTag data) {
        this.data = data;
        initReferences(LoadFlags.ALL_DATA);
    }

    private void initReferences(long loadFlags) {
        if (data == null) {
            throw new NullPointerException("data cannot be null");
        }

        if ((loadFlags != LoadFlags.ALL_DATA) && (loadFlags & LoadFlags.RAW) != 0) {
            raw = true;
            return;
        }

        CompoundTag level;
        if ((level = data.getCompoundTag("Level")) == null) {
            throw new IllegalArgumentException("data does not contain \"Level\" tag");
        }
        dataVersion = data.getInt("DataVersion");
        inhabitedTime = level.getLong("InhabitedTime");
        lastUpdate = level.getLong("LastUpdate");
        if ((loadFlags & LoadFlags.BIOMES) != 0) {
            biomes = level.getIntArray("Biomes");
        }
        if ((loadFlags & LoadFlags.HEIGHTMAPS) != 0) {
            heightMaps = level.getCompoundTag("Heightmaps");
        }
        if ((loadFlags & LoadFlags.CARVING_MASKS) != 0) {
            carvingMasks = level.getCompoundTag("CarvingMasks");
        }
        if ((loadFlags & LoadFlags.ENTITIES) != 0) {
            entities = level.containsKey("Entities") ? level.getListTag("Entities").asCompoundTagList() : null;
        }
        if ((loadFlags & LoadFlags.TILE_ENTITIES) != 0) {
            tileEntities = level.containsKey("TileEntities") ? level.getListTag("TileEntities").asCompoundTagList() : null;
        }
        if ((loadFlags & LoadFlags.TILE_TICKS) != 0) {
            tileTicks = level.containsKey("TileTicks") ? level.getListTag("TileTicks").asCompoundTagList() : null;
        }
        if ((loadFlags & LoadFlags.LIQUID_TICKS) != 0) {
            liquidTicks = level.containsKey("LiquidTicks") ? level.getListTag("LiquidTicks").asCompoundTagList() : null;
        }
        if ((loadFlags & LoadFlags.LIGHTS) != 0) {
            lights = level.containsKey("Lights") ? level.getListTag("Lights").asListTagList() : null;
        }
        if ((loadFlags & LoadFlags.LIQUIDS_TO_BE_TICKED) != 0) {
            liquidsToBeTicked = level.containsKey("LiquidsToBeTicked") ? level.getListTag("LiquidsToBeTicked").asListTagList() : null;
        }
        if ((loadFlags & LoadFlags.TO_BE_TICKED) != 0) {
            toBeTicked = level.containsKey("ToBeTicked") ? level.getListTag("ToBeTicked").asListTagList() : null;
        }
        if ((loadFlags & LoadFlags.POST_PROCESSING) != 0) {
            postProcessing = level.containsKey("PostProcessing") ? level.getListTag("PostProcessing").asListTagList() : null;
        }
        status = level.getString("Status");
        if ((loadFlags & LoadFlags.STRUCTURES) != 0) {
            structures = level.getCompoundTag("Structures");
        }
        if ((loadFlags & (LoadFlags.BLOCK_LIGHTS| LoadFlags.BLOCK_STATES| LoadFlags.SKY_LIGHT)) != 0 && level.containsKey("Sections")) {
            for (CompoundTag section : level.getListTag("Sections").asCompoundTagList()) {
                int sectionIndex = section.getNumber("Y").byteValue();
                KebabSection newSection = new KebabSection(section, dataVersion, loadFlags);
                sections.put(sectionIndex, newSection);
            }
        }

        if (loadFlags != LoadFlags.ALL_DATA) {
            data = null;
            partial = true;
        }
    }

    public int serialize(RandomAccessFile raf, int xPos, int zPos) throws IOException {
        if (partial) {
            throw new UnsupportedOperationException("Partially loaded chunks cannot be serialized");
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream(4096);
        try (BufferedOutputStream nbtOut = new BufferedOutputStream(CompressionType.ZLIB.compress(baos))) {
            new NBTSerializer(false).toStream(new NamedTag(null, updateHandle(xPos, zPos)), nbtOut);
        }
        byte[] rawData = baos.toByteArray();
        raf.writeInt(rawData.length + 1);
        raf.writeByte(CompressionType.ZLIB.getID());
        raf.write(rawData);
        return rawData.length + 5;
    }

    public void deserialize(RandomAccessFile raf) throws IOException {
        deserialize(raf, LoadFlags.ALL_DATA);
    }

    public void deserialize(RandomAccessFile raf, long loadFlags) throws IOException {
        byte compressionTypeByte = raf.readByte();
        CompressionType compressionType = CompressionType.getFromID(compressionTypeByte);
        if (compressionType == null) {
            throw new IOException("invalid compression type " + compressionTypeByte);
        }
        BufferedInputStream dis = new BufferedInputStream(compressionType.decompress(new FileInputStream(raf.getFD())));
        NamedTag tag = new NBTDeserializer(false).fromStream(dis);
        if (tag != null && tag.getTag() instanceof CompoundTag) {
            data = (CompoundTag) tag.getTag();
            initReferences(loadFlags);
        } else {
            throw new IOException("invalid data tag: " + (tag == null ? "null" : tag.getClass().getName()));
        }
    }

    public int getBiomeAt(int blockX, int blockY, int blockZ) {
        if (dataVersion < 2202) {
            if (biomes == null || biomes.length != 256) {
                return -1;
            }
            return biomes[getBlockIndex(blockX, blockZ)];
        } else {
            if (biomes == null || biomes.length != 1024) {
                return -1;
            }
            int biomeX = (blockX & 0xF) >> 2;
            int biomeY = (blockY & 0xF) >> 2;
            int biomeZ = (blockZ & 0xF) >> 2;

            return biomes[getBiomeIndex(biomeX, biomeY, biomeZ)];
        }
    }

    @Deprecated
    public void setBiomeAt(int blockX, int blockZ, int biomeID) {
        checkRaw();
        if (dataVersion < 2202) {
            if (biomes == null || biomes.length != 256) {
                biomes = new int[256];
                Arrays.fill(biomes, -1);
            }
            biomes[getBlockIndex(blockX, blockZ)] = biomeID;
        } else {
            if (biomes == null || biomes.length != 1024) {
                biomes = new int[1024];
                Arrays.fill(biomes, -1);
            }

            int biomeX = (blockX & 0xF) >> 2;
            int biomeZ = (blockZ & 0xF) >> 2;

            for (int y = 0; y < 64; y++) {
                biomes[getBiomeIndex(biomeX, y, biomeZ)] = biomeID;
            }
        }
    }

    public void setBiomeAt(int blockX, int blockY, int blockZ, int biomeID) {
        checkRaw();
        if (dataVersion < 2202) {
            if (biomes == null || biomes.length != 256) {
                biomes = new int[256];
                Arrays.fill(biomes, -1);
            }
            biomes[getBlockIndex(blockX, blockZ)] = biomeID;
        } else {
            if (biomes == null || biomes.length != 1024) {
                biomes = new int[1024];
                Arrays.fill(biomes, -1);
            }

            int biomeX = (blockX & 0xF) >> 2;
            int biomeZ = (blockZ & 0xF) >> 2;

            biomes[getBiomeIndex(biomeX, blockY, biomeZ)] = biomeID;
        }
    }

    int getBiomeIndex(int biomeX, int biomeY, int biomeZ) {
        return biomeY * 16 + biomeZ * 4 + biomeX;
    }

    @Override
    public BlockState getBlockStateAt(int blockX, int blockY, int blockZ) {
        KebabSection section = sections.get(MCAUtil.blockToChunk(blockY));
        if (section == null) {
            return null;
        }
        return section.getBlockStateAt(blockX, blockY, blockZ);
    }

    @Override
    public BlockState getBlockStateAt(BlockPosition position) {
        return getBlockStateAt(position.getX(), position.getY(), position.getZ());
    }

    @Override
    public void setBlockStateAt(int blockX, int blockY, int blockZ, BlockState state) {
        setBlockStateAt(blockX, blockY, blockZ, state, false);
    }

    @Override
    public void setBlockStateAt(BlockPosition position, BlockState state) {
        setBlockStateAt(position, state, false);
    }

    @Override
    public void setBlockStateAt(BlockPosition position, BlockState state, boolean cleanup) {
        setBlockStateAt(position.getX(), position.getY(), position.getZ(), state, cleanup);
    }

    @Override
    public void setBlockStateAt(int blockX, int blockY, int blockZ, BlockState state, boolean cleanup) {
        checkRaw();
        int sectionIndex = MCAUtil.blockToChunk(blockY);
        KebabSection section = sections.get(sectionIndex);
        if (section == null) {
            sections.put(sectionIndex, section = KebabSection.newSection());
        }
        section.setBlockStateAt(blockX, blockY, blockZ, state.toCompoundTag(), cleanup);
    }

    @Override
    public int getDataVersion() {
        return dataVersion;
    }

    public void setDataVersion(int dataVersion) {
        checkRaw();
        this.dataVersion = dataVersion;
        for (KebabSection section : sections.values()) {
            if (section != null) {
                section.dataVersion = dataVersion;
            }
        }
    }

    public int getLastMCAUpdate() {
        return lastMCAUpdate;
    }

    public void setLastMCAUpdate(int lastMCAUpdate) {
        checkRaw();
        this.lastMCAUpdate = lastMCAUpdate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        checkRaw();
        this.status = status;
    }

    public KebabSection getSection(int sectionY) {
        return sections.get(sectionY);
    }

    public void setSection(int sectionY, KebabSection section) {
        checkRaw();
        sections.put(sectionY, section);
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        checkRaw();
        this.lastUpdate = lastUpdate;
    }

    public long getInhabitedTime() {
        return inhabitedTime;
    }

    public void setInhabitedTime(long inhabitedTime) {
        checkRaw();
        this.inhabitedTime = inhabitedTime;
    }

    public int[] getBiomes() {
        return biomes;
    }

    public void setBiomes(int[] biomes) {
        checkRaw();
        if (biomes != null) {
            if (dataVersion < 2202 && biomes.length != 256 || dataVersion >= 2202 && biomes.length != 1024) {
                throw new IllegalArgumentException("biomes array must have a length of " + (dataVersion < 2202 ? "256" : "1024"));
            }
        }
        this.biomes = biomes;
    }

    public CompoundTag getHeightMaps() {
        return heightMaps;
    }

    public void setHeightMaps(CompoundTag heightMaps) {
        checkRaw();
        this.heightMaps = heightMaps;
    }

    public CompoundTag getCarvingMasks() {
        return carvingMasks;
    }

    public void setCarvingMasks(CompoundTag carvingMasks) {
        checkRaw();
        this.carvingMasks = carvingMasks;
    }

    public ListTag<CompoundTag> getEntities() {
        return entities;
    }

    public void setEntities(ListTag<CompoundTag> entities) {
        checkRaw();
        this.entities = entities;
    }

    public ListTag<CompoundTag> getTileEntities() {
        return tileEntities;
    }

    public void setTileEntities(ListTag<CompoundTag> tileEntities) {
        checkRaw();
        this.tileEntities = tileEntities;
    }

    public ListTag<CompoundTag> getTileTicks() {
        return tileTicks;
    }

    public void setTileTicks(ListTag<CompoundTag> tileTicks) {
        checkRaw();
        this.tileTicks = tileTicks;
    }

    public ListTag<CompoundTag> getLiquidTicks() {
        return liquidTicks;
    }

    public void setLiquidTicks(ListTag<CompoundTag> liquidTicks) {
        checkRaw();
        this.liquidTicks = liquidTicks;
    }

    public ListTag<ListTag<?>> getLights() {
        return lights;
    }

    public void setLights(ListTag<ListTag<?>> lights) {
        checkRaw();
        this.lights = lights;
    }

    public ListTag<ListTag<?>> getLiquidsToBeTicked() {
        return liquidsToBeTicked;
    }

    public void setLiquidsToBeTicked(ListTag<ListTag<?>> liquidsToBeTicked) {
        checkRaw();
        this.liquidsToBeTicked = liquidsToBeTicked;
    }

    public ListTag<ListTag<?>> getToBeTicked() {
        return toBeTicked;
    }

    public void setToBeTicked(ListTag<ListTag<?>> toBeTicked) {
        checkRaw();
        this.toBeTicked = toBeTicked;
    }

    public ListTag<ListTag<?>> getPostProcessing() {
        return postProcessing;
    }

    public void setPostProcessing(ListTag<ListTag<?>> postProcessing) {
        checkRaw();
        this.postProcessing = postProcessing;
    }

    public CompoundTag getStructures() {
        return structures;
    }

    public void setStructures(CompoundTag structures) {
        checkRaw();
        this.structures = structures;
    }

    int getBlockIndex(int blockX, int blockZ) {
        return (blockZ & 0xF) * 16 + (blockX & 0xF);
    }

    public void cleanupPalettesAndBlockStates() {
        checkRaw();
        for (KebabSection section : sections.values()) {
            if (section != null) {
                section.cleanupPaletteAndBlockStates();
            }
        }
    }

    private void checkRaw() {
        if (raw) {
            throw new UnsupportedOperationException("cannot update field when working with raw data");
        }
    }

    public static KebabChunk newChunk() {
        return newChunk(DEFAULT_DATA_VERSION);
    }

    public static KebabChunk newChunk(int dataVersion) {
        KebabChunk c = new KebabChunk(0);
        c.dataVersion = dataVersion;
        c.data = new CompoundTag();
        c.data.put("Level", new CompoundTag());
        c.status = "mobs_spawned";
        return c;
    }

    public CompoundTag getHandle() {
        return data;
    }

    public CompoundTag updateHandle(int xPos, int zPos) {
        if (raw) {
            return data;
        }

        data.putInt("DataVersion", dataVersion);
        CompoundTag level = data.getCompoundTag("Level");
        level.putInt("xPos", xPos);
        level.putInt("zPos", zPos);
        level.putLong("LastUpdate", lastUpdate);
        level.putLong("InhabitedTime", inhabitedTime);
        if (dataVersion < 2202) {
            if (biomes != null && biomes.length == 256) {
                level.putIntArray("Biomes", biomes);
            }
        } else {
            if (biomes != null && biomes.length == 1024) {
                level.putIntArray("Biomes", biomes);
            }
        }
        if (heightMaps != null) {
            level.put("Heightmaps", heightMaps);
        }
        if (carvingMasks != null) {
            level.put("CarvingMasks", carvingMasks);
        }
        if (entities != null) {
            level.put("Entities", entities);
        }
        if (tileEntities != null) {
            level.put("TileEntities", tileEntities);
        }
        if (tileTicks != null) {
            level.put("TileTicks", tileTicks);
        }
        if (liquidTicks != null) {
            level.put("LiquidTicks", liquidTicks);
        }
        if (lights != null) {
            level.put("Lights", lights);
        }
        if (liquidsToBeTicked != null) {
            level.put("LiquidsToBeTicked", liquidsToBeTicked);
        }
        if (toBeTicked != null) {
            level.put("ToBeTicked", toBeTicked);
        }
        if (postProcessing != null) {
            level.put("PostProcessing", postProcessing);
        }
        level.putString("Status", status);
        if (structures != null) {
            level.put("Structures", structures);
        }
        ListTag<CompoundTag> sections = new ListTag<>(CompoundTag.class);
        for (KebabSection section : this.sections.values()) {
            if (section != null) {
                sections.add(section.updateHandle());
            }
        }
        level.put("Sections", sections);
        return data;
    }

    @Override
    public @NotNull Iterator<Section> iterator() {
        return sections.values().stream().map(section -> (Section) section).iterator();
    }
}
