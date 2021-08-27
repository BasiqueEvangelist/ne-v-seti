package me.basiqueevangelist.nevseti.nbt;

import net.minecraft.nbt.NbtCompound;

@Deprecated(forRemoval = true)
public interface NbtCompoundView {
    static NbtCompoundView take(NbtCompound tag) {
        return new ImmutableCompoundWrapper(tag);
    }

    byte getType(String key);

    boolean contains(String key);

    boolean contains(String key, int type);

    byte getByte(String key);

    short getShort(String key);

    int getInt(String key);

    long getLong(String key);

    float getFloat(String key);

    double getDouble(String key);

    String getString(String key);

    byte[] getByteArray(String key);

    int[] getIntArray(String key);

    long[] getLongArray(String key);

    NbtCompoundView getCompound(String key);

    NbtListView getList(String key, int type);

    boolean getBoolean(String key);

    NbtCompound copy();
}
