package me.basiqueevangelist.nevseti.nbt;

import net.minecraft.nbt.ListTag;

public interface ListTagView {
    static ListTagView take(ListTag tag) {
        return new ImmutableListWrapper(tag);
    }

    byte getElementType();

    boolean isEmpty();

    CompoundTagView getCompound(int index);

    ListTagView getList(int index);

    short getShort(int index);

    int getInt(int i);

    int[] getIntArray(int index);

    double getDouble(int index);

    float getFloat(int index);

    String getString(int index);

    int size();

    ListTag copy();
}
