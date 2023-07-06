package com.uroria.kebab.api.event;

public interface Cancellable {
    boolean isCancelled();

    void setCancelled(boolean value);
}
