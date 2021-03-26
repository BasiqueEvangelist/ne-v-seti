package me.basiqueevangelist.nevseti.nbt;

import net.minecraft.nbt.CompoundTag;

public interface CompoundTagView {
    static CompoundTagView take(CompoundTag tag) {
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

    CompoundTagView getCompound(String key);

    ListTagView getList(String key, int type);

    boolean getBoolean(String key);

    CompoundTag copy();
}
