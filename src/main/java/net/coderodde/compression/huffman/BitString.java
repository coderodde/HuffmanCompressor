package net.coderodde.compression.huffman;

import java.util.Arrays;

/**
 * This class implements a builder for creating bit strings.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.618 (Nov 14, 2016)
 */
public final class BitString {

    /**
     * The length of a {@code long} array storing the bits.
     */
    private static final int DEFAULT_NUMBER_OF_LONGS = 2;

    /**
     * Used for more efficient modulo arithmetics when indexing a particular
     * bit in a {@code long} value.
     */
    private static final int MODULO_MASK = 0b111111;

    /**
     * Number of bits per a {@code long} value;
     */
    private static final int BITS_PER_LONG = Long.BYTES * Byte.SIZE;

    /**
     * This field holds the actual array of long values for storing the bits.
     */
    private long[] storageLongs;

    /**
     * Current maximum of bits this builder can store.
     */
    private int storageCapacity;

    /**
     * Stores the number of bits this bit string builder actually represents.
     * We have an invariant {@code size <= storageCapacity}.
     */
    private int size;

    /**
     * Constructs an empty bit string builder.
     */
    public BitString() {
        this.storageLongs = new long[DEFAULT_NUMBER_OF_LONGS];
        this.storageCapacity = this.storageLongs.length * BITS_PER_LONG;
    }

    /**
     * Constructs a distinct bit string builder with the same content as in
     * {@code toCopy}. 
     * 
     * @param toCopy the bit string builder whose content to copy.
     */
    public BitString(BitString toCopy) {
        this.size = toCopy.size;
        this.storageCapacity = 
                Math.max(size, DEFAULT_NUMBER_OF_LONGS * BITS_PER_LONG);

        int numberOfLongs = storageCapacity / BITS_PER_LONG +
                         (((storageCapacity & MODULO_MASK) == 0) ? 0 : 1);

        this.storageLongs = Arrays.copyOf(toCopy.storageLongs, numberOfLongs);
    }

    public void appendBit(boolean bit) {
        checkBitArrayCapacity(size + 1);
        writeBitImpl(size, bit);
        ++size;
    }

    /**
     * Returns number of bits stored in this builder.
     * 
     * @return number of bits.
     */
    public int length() {
        return size;
    }

    /**
     * Appends all the bits in {@code bitStringBuilder} to the end of this 
     * builder.
     * 
     * @param bitStringBuilder the bit string builder whose bits to append.
     */
    public void appendBitsFrom(BitString bitStringBuilder) {
        checkBitArrayCapacity(size + bitStringBuilder.size);
        int otherSize = bitStringBuilder.size;

        for (int i = 0; i != otherSize; ++i) {
            appendBit(bitStringBuilder.readBitImpl(i));
        }
    }

    /**
     * Reads a specific bit.
     * 
     * @param index the index of the target bit.
     * @return {@code true} if the target bit is on, {@code false} otherwise.
     */
    public boolean readBit(int index) {
        checkAccessIndex(index);
        return readBitImpl(index);
    }

    /**
     * Removes the very last bit from this bit string builder.
     */
    public void removeLastBit() {
        if (size == 0) {
            throw new IllegalStateException(
            "Removing the last bit from an bit string builder.");
        }

        --size;
    }

    /**
     * Clears the entire bit string builder.
     */
    public void clear() {
        this.size = 0;
    }

    /**
     * Returns the number of bytes occupied by bits.
     * 
     * @return number of bytes occupied. 
     */
    public int getNumberOfBytesOccupied() {
        return size / 8 + ((size % 8 == 0) ? 0 : 1);
    }

    public byte[] toByteArray() {
        int numberOfBytes = (size / Byte.SIZE) +
                           ((size % Byte.SIZE == 0) ? 0 : 1);

        byte[] byteArray = new byte[numberOfBytes];

        for (int i = 0; i != numberOfBytes; ++i) {
            byteArray[i] = 
                    (byte)
                    ((storageLongs[i / Long.BYTES] 
                    >>> Byte.SIZE * (i % Long.BYTES)) & 0xff);
        }

        return byteArray;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(size);

        for (int i = 0; i != size; ++i) {
            sb.append(readBitImpl(i) ? '1': '0');
        }

        return sb.toString();
    }

    private void checkAccessIndex(int index) {
        if (size == 0) {
            throw new IllegalStateException("The bit string is empty.");
        }

        if (index < 0) {
            throw new IndexOutOfBoundsException(
            "The index is negative: " + index + ".");
        }

        if (index >= size) {
            throw new IndexOutOfBoundsException(
            "The bit index is too large (" + index + "). Must be at most " +
            (size - 1) + ".");
        }
    }

    private boolean readBitImpl(int index) {
        int longIndex = index / BITS_PER_LONG;
        int bitIndex  = index & MODULO_MASK;
        long mask = 1L << bitIndex;
        return (storageLongs[longIndex] & mask) != 0;
    }

    private void writeBitImpl(int index, boolean bit) {
        int longIndex = index / BITS_PER_LONG;
        int bitIndex  = index & MODULO_MASK;

        if (bit) {
            long mask = 1L << bitIndex;
            storageLongs[longIndex] |= mask;
        } else {
            long mask = ~(1L << bitIndex);
            storageLongs[longIndex] &= mask;
        }
    }

    private void checkBitArrayCapacity(int requestedCapacity) {
        if (requestedCapacity > storageCapacity) {
            int requestedWords1 =
                    requestedCapacity / BITS_PER_LONG + 
                 (((requestedCapacity & MODULO_MASK) == 0) ? 0 : 1);

            int requestedWords2 = (3 * storageCapacity / 2) / BITS_PER_LONG;

            int selectedRequestedWords = Math.max(requestedWords1,
                                                  requestedWords2);

            this.storageLongs = Arrays.copyOf(storageLongs, 
                                              selectedRequestedWords);

            this.storageCapacity = this.storageLongs.length * BITS_PER_LONG;
        }
    } 
}
