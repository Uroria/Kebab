package org.kebab.common.rust;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public final class LibraryImplementation {
    static {
        try {
            String nativeLibraryName = System.mapLibraryName("kebab_cargo_wrapper");
            File tempFile = File.createTempFile("extracted_", nativeLibraryName);

            InputStream libraryStream = LibraryImplementation.class.getClassLoader().getResourceAsStream(nativeLibraryName);

            if (libraryStream == null) throw new NullPointerException("Rust library missing in resources");

            try(ReadableByteChannel src = Channels.newChannel(libraryStream)) {
                FileOutputStream output = new FileOutputStream(tempFile);
                output.getChannel().transferFrom(src, 0, Long.MAX_VALUE);
                output.close();
            } catch (Exception exception) {
                throw new RuntimeException("Cannot load rust library", exception);
            }

            System.load(tempFile.getAbsolutePath());
        } catch (Exception exception) {
            exception.printStackTrace();
            System.exit(-1);
        }
    }

    public native static void initializeRust();
}
