package com.uroria.kebab.api.player;

import com.uroria.kebab.api.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public abstract class PlayerEvent extends Event {
    private @Getter final Player player;
}
