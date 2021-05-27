package me.basiqueevangelist.nevseti.nbt;

import net.minecraft.nbt.NbtList;

public interface NbtListView {
    static NbtListView take(NbtList tag) {
        return new ImmutableListWrapper(tag);
    }

    byte getHeldType();

    boolean isEmpty();

    NbtCompoundView getCompound(int index);

    NbtListView getList(int index);

    short getShort(int index);

    int getInt(int i);

    int[] getIntArray(int index);

    double getDouble(int index);

    float getFloat(int index);

    String getString(int index);

    int size();

    NbtList copy();
}
