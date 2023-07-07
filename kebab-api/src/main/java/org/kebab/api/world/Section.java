package org.kebab.api.world;

public interface Section extends Comparable<Section> {

    boolean isEmpty();

    int getHeight();

    void setHeight(int height);
}
