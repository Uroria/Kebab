package org.kebab.api.player;

import org.kebab.api.ServerAPI;
import org.kebab.api.position.Location;
import org.kebab.common.ValidUtils;
import lombok.NonNull;

public final class PlayerJoinEvent extends PlayerEvent {
    private Location location;

    public PlayerJoinEvent(Player player) {
        super(player);
        this.location = new Location(ServerAPI.getDefaultWorld(), 0, 0, 0);
    }

    public void setSpawnLocation(@NonNull Location location) {
        ValidUtils.notNull(location, "Location cannot be null");
        this.location = location;
    }

    public Location getSpawnLocation() {
        return this.location;
    }
}
