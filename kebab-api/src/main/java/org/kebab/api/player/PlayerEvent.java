package org.kebab.api.player;

import org.kebab.api.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public abstract class PlayerEvent extends Event {
    private @Getter final Player player;
}
