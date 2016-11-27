package net.coderodde.compression.huffman;

import java.util.Map;

/**
 * This class is responsible for converting the encoded text and the encoder map
 * into a raw byte array.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.61 (Nov 19, 2016)
 */
public final class HuffmanSerializer {

    /**
     * The magic file signature for recognizing the file type.
     */
    static final byte[] MAGIC = new byte[]{ (byte) 0xC0,
                                            (byte) 0xDE,
                                            (byte) 0x0D,
                                            (byte) 0xDE };

    /**
     * The number of bytes it takes to serialize one mapping from a character
     * to its code word.
     */
    static final int BYTES_PER_WEIGHT_MAP_ENTRY = 5;

    /**
     * The number of bytes it takes to serialize the number of code words.
     */
    static final int BYTES_PER_CODE_WORD_COUNT_ENTRY = 4;

    /**
     * The number of bytes it takes to serialize the number of bits in the 
     * actual encoded text.
     */
    static final int BYTES_PER_BIT_COUNT_ENTRY = 4;

    /**
     * Produces a byte array holding the compressed text along with its 
     * encoder map.
     * 
     * @param frequencyMap the encoder map used for encoding the text.
     * @param encodedText  the encoded text.
     * @return an array of byte.
     */
    public byte[] serialize(Map<Byte, Integer> frequencyMap,
                            BitString encodedText) {
        ByteList byteList = new ByteList(computeByteListSize(frequencyMap, 
                                                             encodedText));
        // Emit the magic number:
        for (byte b : MAGIC) {
            byteList.appendByte(b);
        }

        int numberOfCodeWords = frequencyMap.size();
        int numberOfBits = encodedText.length();

        // Emit the number of code words.
        byteList.appendByte((byte) (numberOfCodeWords & 0xff));
        byteList.appendByte((byte)((numberOfCodeWords >>= 8) & 0xff));
        byteList.appendByte((byte)((numberOfCodeWords >>= 8) & 0xff));
        byteList.appendByte((byte)((numberOfCodeWords >>= 8) & 0xff));

        // Emit the number of bits in the encoded text.
        byteList.appendByte((byte) (numberOfBits & 0xff));
        byteList.appendByte((byte)((numberOfBits >>= 8) & 0xff));
        byteList.appendByte((byte)((numberOfBits >>= 8) & 0xff));
        byteList.appendByte((byte)((numberOfBits >>= 8) & 0xff));

        // Emit the code words:
        for (Map.Entry<Byte, Integer> entry : frequencyMap.entrySet()) {
            byte character = entry.getKey();
            int frequency = entry.getValue();

            // Emit the character:
            byteList.appendByte(character);

            // Emit the bytes of the weight value:
            byteList.appendByte((byte) (frequency & 0xff));
            byteList.appendByte((byte)((frequency >>= 8) & 0xff));
            byteList.appendByte((byte)((frequency >>= 8) & 0xff));
            byteList.appendByte((byte)((frequency >>= 8) & 0xff));
        }

        byte[] encodedTextBytes = encodedText.toByteArray();

        // Emit the encoded text:
        for (byte b : encodedTextBytes) {
            byteList.appendByte(b);
        }

        return byteList.toByteArray();
    }

    private int computeByteListSize(Map<Byte, Integer> frequencyMap,
                                    BitString encodedText) {
        return MAGIC.length + BYTES_PER_CODE_WORD_COUNT_ENTRY
                            + BYTES_PER_BIT_COUNT_ENTRY
                            + frequencyMap.size() * BYTES_PER_WEIGHT_MAP_ENTRY 
                            + encodedText.getNumberOfBytesOccupied();
    }
}
