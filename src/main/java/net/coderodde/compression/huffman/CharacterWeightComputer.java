package net.coderodde.compression.huffman;

import java.util.HashMap;
import java.util.Map;

/**
 * This class provides a method for counting relative frequencies of characters
 * in any given corpus of text.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.618 (Nov 19, 2016)
 */
public final class CharacterWeightComputer {

    /**
     * Computes the map mapping each character in the text {@code text} to its
     * relative frequency.
     * 
     * @param text the text for which to compute the frequencies.
     * @return the map mapping each character to its respective frequency.
     */
    public Map<Byte, Float> computeCharacterWeights(byte[] text) {
        Map<Byte, Float> map = new HashMap<>();
        int textLength = text.length;
        
        for (int i = 0; i != textLength; ++i) {
            byte currentByte = text[i];
            
            if (map.containsKey(currentByte)) {
                map.put(currentByte, map.get(currentByte) + 1.0f);
            } else {
                map.put(currentByte, 1.0f);
            }
        }
        
        float textLengthFloat = textLength;
        
        for (Map.Entry<Byte, Float> entry : map.entrySet()) {
            entry.setValue(entry.getValue() / textLengthFloat);
        }
        
        return map;
    }
}
