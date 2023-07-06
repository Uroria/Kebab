package com.uroria.kebab.api.player;

import com.uroria.kebab.api.event.Cancellable;
import net.kyori.adventure.text.Component;

public final class PlayerLoginEvent extends PlayerEvent implements Cancellable {
    private Component cancelledMessage;

    public PlayerLoginEvent(Player player) {
        super(player);
    }

    @Override
    public boolean isCancelled() {
        return cancelledMessage != null;
    }

    @Override
    public void setCancelled(boolean value) {
        if (!value) {
            this.cancelledMessage = null;
            return;
        }
        this.cancelledMessage = Component.empty();
    }

    public void setCancelled(Component reason) {
        if (reason == null) reason = Component.empty();
        this.cancelledMessage = reason;
    }
}
