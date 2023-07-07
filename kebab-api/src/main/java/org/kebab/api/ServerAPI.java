package org.kebab.api;

import org.kebab.api.world.World;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ServerAPI {
    private @Getter @Setter Server server;

    public World getDefaultWorld() {
        return server.getWorld("world");
    }
}
