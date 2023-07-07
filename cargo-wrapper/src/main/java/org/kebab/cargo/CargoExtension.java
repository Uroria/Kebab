package org.kebab.cargo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CargoExtension {
    public String cargoCommand = "cargo";

    public String toolchain = null;

    public String crate = null;

    public Map<String, String> outputs = new ConcurrentHashMap<>();

    public Map<String, String> targets = new ConcurrentHashMap<>();

    public String profile = "release";

    public List<String> arguments = new ArrayList<>();

    public Map<String, String> environment = new ConcurrentHashMap<>();
}
