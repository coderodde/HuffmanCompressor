package net.coderodde.compression.huffman;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is responsible for deserializing the text from a raw byte data.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.61 (Nov 19, 2016)
 */
public class HuffmanDeserializer {

    public static final class Result {
        
        private final BitString encodedText;
        private final Map<Byte, BitString> encoderMap;
        
        Result(BitString encodedText, 
               Map<Byte, BitString> encoderMap) {
            this.encodedText = encodedText;
            this.encoderMap  = encoderMap;
        }
        
        public BitString getEncodedText() {
            return encodedText;
        }
        
        public Map<Byte, BitString> getEncoderMap() {
            return encoderMap;
        }
    }
    
    /**
     * Deserialises and returns the data structures need for decoding the text.
     * 
     * @param data the raw byte data previously serialised.
     * @return the data structures needed for decoding the text.
     */
    public Result deserialize(byte[] data) {
        checkSignature(data);
        int numberOfCodeWords = extractNumberOfCodeWords(data);
        int numberOfBits = extractNumberOfEncodedTextBits(data);
        
        Map<Byte, BitString> encoderMap = extractEncoderMap(data, 
                                                            numberOfCodeWords);
        BitString encodedText = extractEncodedText(data, 
                                                   encoderMap, 
                                                   numberOfBits);
        return new Result(encodedText, encoderMap);
    }
    
    private void checkSignature(byte[] data) {
        if (data.length < 4) {
            throw new InvalidFileFormatException(
            "No file type signature. The file is too short: " + data.length);
        }
        
        for (int i = 0; i != HuffmanSerializer.MAGIC.length; ++i) {
            if (data[i] != HuffmanSerializer.MAGIC[i]) {
                throw new InvalidFileFormatException(
                "Bad file type signature.");
            }
        }
    }
    
    private int extractNumberOfCodeWords(byte[] data) {
        if (data.length < 8) {
            throw new InvalidFileFormatException(
            "No number of code words. The file is too short: " + data.length);
        }
        
        int numberOfCodeWords = 0;
        
        numberOfCodeWords |= (Byte.toUnsignedInt(data[7]) << 24);
        numberOfCodeWords |= (Byte.toUnsignedInt(data[6]) << 16);
        numberOfCodeWords |= (Byte.toUnsignedInt(data[5]) << 8);
        numberOfCodeWords |= (Byte.toUnsignedInt(data[4]));
        
        return numberOfCodeWords;
    }
    
    private Map<Byte, BitString> extractEncoderMap(byte[] data,
                                                   int numberOfCodeWords) {
        Map<Byte, BitString> encoderMap = new HashMap<>();
        
        try {
            int dataByteIndex =
                    HuffmanSerializer.MAGIC.length +
                    HuffmanSerializer.BYTES_PER_BIT_COUNT_ENTRY +
                    HuffmanSerializer.BYTES_PER_CODE_WORD_COUNT_ENTRY;

            for (int i = 0; i != numberOfCodeWords; ++i) {
                byte character = data[dataByteIndex++];
                int codeWordLength = data[dataByteIndex++];
                int bitIndex = 0;
                BitString codeWordBits = new BitString();
                
                for (int codeWordBitIndex = 0;
                        codeWordBitIndex != codeWordLength;
                        codeWordBitIndex++) {
                    byte currentByte = data[dataByteIndex];
                    boolean bit = (currentByte & (1 << bitIndex)) != 0;
                    codeWordBits.appendBit(bit);
                    
                    if (++bitIndex == Byte.SIZE) {
                        bitIndex = 0;
                        dataByteIndex++;
                    }
                }
                
                encoderMap.put(character, codeWordBits);
                
                if (bitIndex != 0) {
                    dataByteIndex++;
                }
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            throw new InvalidFileFormatException("Invalid file format.");
        }
        
        return encoderMap;
    }
    
    private BitString extractEncodedText(byte[] data,
                                         Map<Byte, BitString> encoderMap,
                                         int numberOfEncodedTextBits) {
        int omittedBytes = HuffmanSerializer.MAGIC.length +
                           HuffmanSerializer.BYTES_PER_BIT_COUNT_ENTRY +
                           HuffmanSerializer.BYTES_PER_CODE_WORD_COUNT_ENTRY;
        
        for (Map.Entry<Byte, BitString> entry : encoderMap.entrySet()) {
            omittedBytes += 2 + entry.getValue().getNumberOfBytesOccupied();
        }
        
        BitString encodedText = new BitString();
        int currentByteIndex = omittedBytes;
        int currentBitIndex = 0;
        
        try {
            for (int bitIndex = 0; 
                    bitIndex != numberOfEncodedTextBits; 
                    bitIndex++) {
                boolean bit = 
                        (data[currentByteIndex] & (1 << currentBitIndex)) != 0;

                encodedText.appendBit(bit);

                if (++currentBitIndex == Byte.SIZE) {
                    currentBitIndex = 0;
                    currentByteIndex++;
                }
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            throw new InvalidFileFormatException("Invalid file format.");
        }
        
        return encodedText;
    }

    private int extractNumberOfEncodedTextBits(byte[] data) {
        if (data.length < 12) {
            throw new InvalidFileFormatException(
            "No number of encoded text bits. The file is too short: " + 
                    data.length);
        }
        
        int numberOfEncodedTextBits = 0;
        
        numberOfEncodedTextBits |= (Byte.toUnsignedInt(data[11]) << 24);
        numberOfEncodedTextBits |= (Byte.toUnsignedInt(data[10]) << 16);
        numberOfEncodedTextBits |= (Byte.toUnsignedInt(data[9] ) << 8);
        numberOfEncodedTextBits |= (Byte.toUnsignedInt(data[8]));
        
        return numberOfEncodedTextBits;
    }
}
