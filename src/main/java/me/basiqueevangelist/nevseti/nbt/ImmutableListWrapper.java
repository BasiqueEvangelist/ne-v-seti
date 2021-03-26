package me.basiqueevangelist.nevseti.nbt;

import net.minecraft.nbt.ListTag;

class ImmutableListWrapper implements ListTagView {
    private final ListTag tag;

    public ImmutableListWrapper(ListTag tag) {
        this.tag = tag;
    }

    @Override
    public byte getElementType() {
        return tag.getElementType();
    }

    @Override
    public boolean isEmpty() {
        return tag.isEmpty();
    }

    @Override
    public CompoundTagView getCompound(int index) {
        return CompoundTagView.take(tag.getCompound(index));
    }

    @Override
    public ListTagView getList(int index) {
        return ListTagView.take(tag.getList(index));
    }

    @Override
    public short getShort(int index) {
        return tag.getShort(index);
    }

    @Override
    public int getInt(int i) {
        return tag.getInt(i);
    }

    @Override
    public int[] getIntArray(int index) {
        return tag.getIntArray(index);
    }

    @Override
    public double getDouble(int index) {
        return tag.getDouble(index);
    }

    @Override
    public float getFloat(int index) {
        return tag.getFloat(index);
    }

    @Override
    public String getString(int index) {
        return tag.getString(index);
    }

    @Override
    public int size() {
        return tag.size();
    }

    @Override
    public ListTag copy() {
        return tag.copy();
    }
}
