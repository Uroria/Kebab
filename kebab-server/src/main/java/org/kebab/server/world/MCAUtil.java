package org.kebab.server.world;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MCAUtil {

    private MCAUtil() {}

    public static MCAFile read(String file) throws IOException {
        return read(new File(file), LoadFlags.ALL_DATA);
    }

    public static MCAFile read(File file) throws IOException {
        return read(file, LoadFlags.ALL_DATA);
    }

    public static MCAFile read(String file, long loadFlags) throws IOException {
        return read(new File(file), loadFlags);
    }

    public static MCAFile read(File file, long loadFlags) throws IOException {
        MCAFile mcaFile = newMCAFile(file);
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            mcaFile.deserialize(raf, loadFlags);
            return mcaFile;
        }
    }

    public static int write(MCAFile mcaFile, String file) throws IOException {
        return write(mcaFile, new File(file), false);
    }

    public static int write(MCAFile mcaFile, File file) throws IOException {
        return write(mcaFile, file, false);
    }

    public static int write(MCAFile mcaFile, String file, boolean changeLastUpdate) throws IOException {
        return write(mcaFile, new File(file), changeLastUpdate);
    }

    public static int write(MCAFile mcaFile, File file, boolean changeLastUpdate) throws IOException {
        File to = file;
        if (file.exists()) {
            to = File.createTempFile(to.getName(), null);
        }
        int chunks;
        try (RandomAccessFile raf = new RandomAccessFile(to, "rw")) {
            chunks = mcaFile.serialize(raf, changeLastUpdate);
        }

        if (chunks > 0 && to != file) {
            Files.move(to.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        return chunks;
    }

    public static String createNameFromChunkLocation(int chunkX, int chunkZ) {
        return createNameFromRegionLocation( chunkToRegion(chunkX), chunkToRegion(chunkZ));
    }

    public static String createNameFromBlockLocation(int blockX, int blockZ) {
        return createNameFromRegionLocation(blockToRegion(blockX), blockToRegion(blockZ));
    }

    public static String createNameFromRegionLocation(int regionX, int regionZ) {
        return "r." + regionX + "." + regionZ + ".mca";
    }

    public static int blockToChunk(int block) {
        return block >> 4;
    }

    public static int blockToRegion(int block) {
        return block >> 9;
    }

    public static int chunkToRegion(int chunk) {
        return chunk >> 5;
    }

    public static int regionToChunk(int region) {
        return region << 5;
    }

    public static int regionToBlock(int region) {
        return region << 9;
    }

    public static int chunkToBlock(int chunk) {
        return chunk << 4;
    }

    private static final Pattern mcaFilePattern = Pattern.compile("^.*r\\.(?<regionX>-?\\d+)\\.(?<regionZ>-?\\d+)\\.mca$");

    public static MCAFile newMCAFile(File file) {
        Matcher m = mcaFilePattern.matcher(file.getName());
        if (m.find()) {
            return new MCAFile(Integer.parseInt(m.group("regionX")), Integer.parseInt(m.group("regionZ")));
        }
        throw new IllegalArgumentException("invalid mca file name: " + file.getName());
    }
}
