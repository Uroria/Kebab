package org.kebab.api.player;

import lombok.Getter;

public final class PlayerSkin {
    private @Getter final String value, signature;

    public PlayerSkin(String value, String signature) {
        this.value = value;
        this.signature = signature;
    }
}
