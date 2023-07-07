package org.kebab.api.player;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum GameMode {
    SURVIVAL(0),
    CREATIVE(1),
    ADVENTURE(2),
    SPECTATOR(3);

    private @Getter
    final int identifier;
}
