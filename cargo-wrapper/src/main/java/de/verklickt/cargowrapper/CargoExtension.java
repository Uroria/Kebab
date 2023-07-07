package de.verklickt.cargowrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CargoExtension {
    public static final String EXTENSION_NAME = "cargo";

    public static final String BUILD_TASK_NAME = "cargoBuild";

    public static final String CONFIGURATION_NAME = "cargo-wrapper";

    public String cargoCommand = "cargo";

    public String toolchain = null;

    public String crate = null;

    public final Map<String, String> outputs = new ConcurrentHashMap<>();

    public String profile = "release";

    public List<String> commandArguments = new ArrayList<>();

    public final Map<String, String> environment = new ConcurrentHashMap<>();
}
