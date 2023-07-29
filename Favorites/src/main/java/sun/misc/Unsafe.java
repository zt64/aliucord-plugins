package sun.misc;

import java.lang.reflect.Field;

public class Unsafe {
    public <T> Object allocateInstance(Class<T> clazz) throws InstantiationException {
        return null;
    }

    public long objectFieldOffset(Field field) {
        return 0;
    }

    public int arrayBaseOffset(Class<?> clazz) {
        return 0;
    }

    public int arrayIndexScale(Class<?> clazz) {
        return 0;
    }

    public int getInt(Object target, long offset) {
        return 0;
    }

    public void putInt(Object target, long offset, int value) {

    }

    public long getLong(Object target, long offset) {
        return 0;
    }

    public void putLong(Object target, long offset, long value) {

    }

    public Object getObject(Object target, long offset) {
        return null;
    }

    public void putObject(Object target, long offset, Object value) {

    }

    public Object staticFieldBase(Field field) {
        return null;
    }

    public long staticFieldOffset(Field field) {
        return 0;
    }

    public byte getByte(Object target, long offset) {
        return 0;
    }

    public void putByte(Object target, long offset, byte value) {

    }

    public boolean getBoolean(Object target, long offset) {
        return false;
    }

    public void putBoolean(Object target, long offset, boolean value) {
    }

    public float getFloat(Object target, long offset) {
        return 0;
    }

    public void putFloat(Object target, long offset, float value) {
    }

    public double getDouble(Object target, long offset) {
        return 0;
    }

    public void putDouble(Object target, long offset, double value) {
    }

    public byte getByte(long address) {
        return 0;
    }

    public void putByte(long address, byte value) {
    }

    public int getInt(long address) {
        return 0;
    }

    public void putInt(long address, int value) {
    }

    public void copyMemory(Object o, long srcOffset, byte[] target, long l, long length) {
    }

    public void putLong(long address, long value) {
    }

    public long getLong(long address) {
        return 0;
    }
}