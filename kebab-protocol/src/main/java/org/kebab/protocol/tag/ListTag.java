package org.kebab.protocol.tag;

import org.kebab.protocol.io.MaxDepthIO;
import org.kebab.protocol.tag.array.ByteArrayTag;
import org.kebab.protocol.tag.array.IntArrayTag;
import org.kebab.protocol.tag.array.LongArrayTag;
import com.uroria.kebab.protocol.tag.primitives.*;
import org.kebab.protocol.tag.primitives.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class ListTag<T extends Tag<?>> extends Tag<List<T>> implements Iterable<T>, Comparable<ListTag<T>>, MaxDepthIO {

    public static final byte ID = 9;

    private Class<?> typeClass = null;

    private ListTag(int initialCapacity) {
        super(createEmptyValue(initialCapacity));
    }

    @Override
    public byte getID() {
        return ID;
    }

    public static ListTag<?> createUnchecked(Class<?> typeClass) {
        return createUnchecked(typeClass, 3);
    }

    public static ListTag<?> createUnchecked(Class<?> typeClass, int initialCapacity) {
        ListTag<?> list = new ListTag<>(initialCapacity);
        list.typeClass = typeClass;
        return list;
    }

    private static <T> List<T> createEmptyValue(int initialCapacity) {
        return new ArrayList<>(initialCapacity);
    }

    public ListTag(Class<? super T> typeClass) throws IllegalArgumentException, NullPointerException {
        this(typeClass, 3);
    }

    public ListTag(Class<? super T> typeClass, int initialCapacity) throws IllegalArgumentException, NullPointerException {
        super(createEmptyValue(initialCapacity));
        if (typeClass == EndTag.class) {
            throw new IllegalArgumentException("cannot create ListTag with EndTag elements");
        }
        this.typeClass = Objects.requireNonNull(typeClass);
    }

    public Class<?> getTypeClass() {
        return typeClass == null ? EndTag.class : typeClass;
    }

    public int size() {
        return getValue().size();
    }

    public T remove(int index) {
        return getValue().remove(index);
    }

    public void clear() {
        getValue().clear();
    }

    public boolean contains(T t) {
        return getValue().contains(t);
    }

    public boolean containsAll(Collection<Tag<?>> tags) {
        return getValue().containsAll(tags);
    }

    public void sort(Comparator<T> comparator) {
        getValue().sort(comparator);
    }

    @Override
    public Iterator<T> iterator() {
        return getValue().iterator();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        getValue().forEach(action);
    }

    public T set(int index, T t) {
        return getValue().set(index, Objects.requireNonNull(t));
    }

    /**
     * Adds a Tag to this ListTag after the last index.
     *
     * @param t The element to be added.
     */
    public void add(T t) {
        add(size(), t);
    }

    public void add(int index, T t) {
        Objects.requireNonNull(t);
        if (getTypeClass() == EndTag.class) {
            typeClass = t.getClass();
        } else if (typeClass != t.getClass()) {
            throw new ClassCastException(
                    String.format("cannot add %s to ListTag<%s>",
                            t.getClass().getSimpleName(),
                            typeClass.getSimpleName()));
        }
        getValue().add(index, t);
    }

    public void addAll(Collection<T> t) {
        for (T tt : t) {
            add(tt);
        }
    }

    public void addAll(int index, Collection<T> t) {
        int i = 0;
        for (T tt : t) {
            add(index + i, tt);
            i++;
        }
    }

    public void addBoolean(boolean value) {
        addUnchecked(new ByteTag(value));
    }

    public void addByte(byte value) {
        addUnchecked(new ByteTag(value));
    }

    public void addShort(short value) {
        addUnchecked(new ShortTag(value));
    }

    public void addInt(int value) {
        addUnchecked(new IntTag(value));
    }

    public void addLong(long value) {
        addUnchecked(new LongTag(value));
    }

    public void addFloat(float value) {
        addUnchecked(new FloatTag(value));
    }

    public void addDouble(double value) {
        addUnchecked(new DoubleTag(value));
    }

    public void addString(String value) {
        addUnchecked(new StringTag(value));
    }

    public void addByteArray(byte[] value) {
        addUnchecked(new ByteArrayTag(value));
    }

    public void addIntArray(int[] value) {
        addUnchecked(new IntArrayTag(value));
    }

    public void addLongArray(long[] value) {
        addUnchecked(new LongArrayTag(value));
    }

    public T get(int index) {
        return getValue().get(index);
    }

    public int indexOf(T t) {
        return getValue().indexOf(t);
    }

    @SuppressWarnings("unchecked")
    public <L extends Tag<?>> ListTag<L> asTypedList(Class<L> type) {
        checkTypeClass(type);
        return (ListTag<L>) this;
    }

    public ListTag<ByteTag> asByteTagList() {
        return asTypedList(ByteTag.class);
    }

    public ListTag<ShortTag> asShortTagList() {
        return asTypedList(ShortTag.class);
    }

    public ListTag<IntTag> asIntTagList() {
        return asTypedList(IntTag.class);
    }

    public ListTag<LongTag> asLongTagList() {
        return asTypedList(LongTag.class);
    }

    public ListTag<FloatTag> asFloatTagList() {
        return asTypedList(FloatTag.class);
    }

    public ListTag<DoubleTag> asDoubleTagList() {
        return asTypedList(DoubleTag.class);
    }

    public ListTag<StringTag> asStringTagList() {
        return asTypedList(StringTag.class);
    }

    public ListTag<ByteArrayTag> asByteArrayTagList() {
        return asTypedList(ByteArrayTag.class);
    }

    public ListTag<IntArrayTag> asIntArrayTagList() {
        return asTypedList(IntArrayTag.class);
    }

    public ListTag<LongArrayTag> asLongArrayTagList() {
        return asTypedList(LongArrayTag.class);
    }

    @SuppressWarnings("unchecked")
    public ListTag<ListTag<?>> asListTagList() {
        checkTypeClass(ListTag.class);
        typeClass = ListTag.class;
        return (ListTag<ListTag<?>>) this;
    }

    public ListTag<CompoundTag> asCompoundTagList() {
        return asTypedList(CompoundTag.class);
    }

    @Override
    public String valueToString(int maxDepth) {
        StringBuilder sb = new StringBuilder("{\"type\":\"").append(getTypeClass().getSimpleName()).append("\",\"list\":[");
        for (int i = 0; i < size(); i++) {
            sb.append(i > 0 ? "," : "").append(get(i).valueToString(decrementMaxDepth(maxDepth)));
        }
        sb.append("]}");
        return sb.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!super.equals(other) || size() != ((ListTag<?>) other).size() || getTypeClass() != ((ListTag<?>) other)
                .getTypeClass()) {
            return false;
        }
        for (int i = 0; i < size(); i++) {
            if (!get(i).equals(((ListTag<?>) other).get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTypeClass().hashCode(), getValue().hashCode());
    }

    @Override
    public int compareTo(ListTag<T> o) {
        return Integer.compare(size(), o.getValue().size());
    }

    @SuppressWarnings("unchecked")
    @Override
    public ListTag<T> clone() {
        ListTag<T> copy = new ListTag<>(this.size());
        // assure type safety for clone
        copy.typeClass = typeClass;
        for (T t : getValue()) {
            copy.add((T) t.clone());
        }
        return copy;
    }

    //TODO: make private
    @SuppressWarnings("unchecked")
    public void addUnchecked(Tag<?> tag) {
        if (getTypeClass() != EndTag.class && typeClass != tag.getClass()) {
            throw new IllegalArgumentException(String.format(
                    "cannot add %s to ListTag<%s>",
                    tag.getClass().getSimpleName(), typeClass.getSimpleName()));
        }
        add(size(), (T) tag);
    }

    private void checkTypeClass(Class<?> clazz) {
        if (getTypeClass() != EndTag.class && typeClass != clazz) {
            throw new ClassCastException(String.format(
                    "cannot cast ListTag<%s> to ListTag<%s>",
                    typeClass.getSimpleName(), clazz.getSimpleName()));
        }
    }
}
