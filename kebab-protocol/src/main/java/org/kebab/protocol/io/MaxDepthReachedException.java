package org.kebab.protocol.io;

public final class MaxDepthReachedException extends RuntimeException {

    public MaxDepthReachedException(String message) {
        super(message);
    }
}
