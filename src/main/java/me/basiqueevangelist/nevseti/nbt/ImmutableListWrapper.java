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
        return 0;
    }

    @Override
    public int getInt(int i) {
        return 0;
    }

    @Override
    public int[] getIntArray(int index) {
        return new int[0];
    }

    @Override
    public double getDouble(int index) {
        return 0;
    }

    @Override
    public float getFloat(int index) {
        return 0;
    }

    @Override
    public String getString(int index) {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public ListTag copy() {
        return null;
    }
}
