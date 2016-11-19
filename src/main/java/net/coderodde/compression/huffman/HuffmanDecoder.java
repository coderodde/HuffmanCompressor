package net.coderodde.compression.huffman;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is responsible for recovering the encoded text.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.61 (Nov 19, 2016)
 */
public final class HuffmanDecoder {

    /**
     * Recovers the text encoded by the bit string {@code bits} and the encoder
     * map {@code encoderMap}.
     * 
     * @param bits       the actual encoded text.
     * @param encoderMap the map mapping each character to its respective
     *                   code word.
     * @return the recovered text.
     */
    public byte[] decode(BitString bits,
                         Map<Byte, BitString> encoderMap) {
        Map<BitString, Byte> decoderMap = invertEncoderMap(encoderMap);
        ByteList byteList = new ByteList();
        BitString bitAccumulator = new BitString();
        int totalBits = bits.length();
        
        for (int bitIndex = 0; bitIndex != totalBits; ++bitIndex) {
            bitAccumulator.appendBit(bits.readBit(bitIndex));
            Byte currentByte = decoderMap.get(bitAccumulator);
            
            if (currentByte != null) {
                byteList.appendByte(currentByte);
                bitAccumulator.clear();
            }
        }
        
        return byteList.toByteArray();
    }
    
    private Map<BitString, Byte>
            invertEncoderMap(Map<Byte, BitString> encoderMap) {
        Map<BitString, Byte> map = new HashMap<>(encoderMap.size());
        
        for (Map.Entry<Byte, BitString> entry 
                : encoderMap.entrySet()) {
            map.put(entry.getValue(), entry.getKey());
        }
        
        return map;
    }
}
