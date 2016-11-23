package net.coderodde.compression.huffman;

import java.util.Map;

/**
 * This class provides a method for encoding the given text using a particular
 * encoder map.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.61 (Nov 19, 2016)
 */
public final class HuffmanEncoder {

    /**
     * Encodes the input text {@code text} using the encoder map 
     * {@code encoderMap}.
     * 
     * @param map  the encoder map.
     * @param text the text to encode.
     * @return a bit string representing the encoded text.
     */
    public BitString encode(Map<Byte, BitString> map, byte[] text) {
        BitString outputBitString = new BitString();
        int textLength = text.length;

        for (int index = 0; index != textLength; ++index) {
            byte currentByte = text[index];
            BitString codeWord = map.get(currentByte);
            outputBitString.appendBitsFrom(codeWord);
        }

        return outputBitString;
    }
}
