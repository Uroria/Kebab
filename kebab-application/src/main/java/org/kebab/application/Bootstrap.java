package org.kebab.application;

import org.kebab.server.KebabConfiguration;
import org.kebab.server.KebabServer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public final class Bootstrap {

    static {
        try {
            String nativeLibraryName = System.mapLibraryName("kebab_server");
            File tempFile = File.createTempFile("extracted_", nativeLibraryName);

            InputStream libraryStream = KebabServer.class.getClassLoader().getResourceAsStream(nativeLibraryName);

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

    public static void main(String... args) {
        KebabConfiguration.reload();
        KebabServer server = new KebabServer();
        Runtime.getRuntime().addShutdownHook(new Thread(server::terminate));
        server.start();
    }
}
