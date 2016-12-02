package net.coderodde.compression.huffman;

import java.util.Map;
import java.util.TreeMap;

/**
 * This class provides a method for counting relative frequencies of characters
 * in any given corpus of text.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.618 (Nov 19, 2016)
 */
public final class ByteCountComputer {

    /**
     * Computes the map mapping each character in the text {@code text} to its
     * relative frequency.
     * 
     * @param text the text for which to compute the frequencies.
     * @return the map mapping each character to its respective frequency.
     */
    public Map<Byte, Integer> computeCharacterWeights(byte[] text) {
        Map<Byte, Integer> map = new TreeMap<>();
        int textLength = text.length;

        for (int i = 0; i != textLength; ++i) {
            byte currentByte = text[i];

            if (map.containsKey(currentByte)) {
                map.put(currentByte, map.get(currentByte) + 1);
            } else {
                map.put(currentByte, 1);
            }
        }

        return map;
    }
}

