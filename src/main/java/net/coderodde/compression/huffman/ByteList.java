package net.coderodde.compression.huffman;

import java.util.Arrays;

/**
 * This class implements a simple, non-generic list of bytes.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Nov 17, 2016)
 */
final class ByteList {

    private byte[] data;
    private int size;

    public ByteList() {
        this.data = new byte[8];
    }

    public ByteList(int capacity) {
        this.data = new byte[capacity];
    }

    public void appendByte(byte b) {
        ensureCapacity(size + 1);
        data[size++] = b;
    }

    public byte[] toByteArray() {
        return Arrays.copyOfRange(data, 0, size);
    }

    private void ensureCapacity(int requestedCapacity) {
        if (requestedCapacity > data.length) {
            int selectedCapacity = Math.max(requestedCapacity,
                                            data.length * 2);

            data = Arrays.copyOf(data, selectedCapacity);
        }
    }
}
