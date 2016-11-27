package net.coderodde.compression.huffman;

import java.util.Map;
import java.util.TreeMap;

/**
 * This class is responsible for deserializing the text from a raw byte data.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.61 (Nov 19, 2016)
 */
public class HuffmanDeserializer {

    public static final class Result {

        private final BitString encodedText;
        private final Map<Byte, Integer> frequencyMap;

        Result(BitString encodedText, 
               Map<Byte, Integer> frequencyMap) {
            this.encodedText  = encodedText;
            this.frequencyMap = frequencyMap;
        }

        public BitString getEncodedText() {
            return encodedText;
        }

        public Map<Byte, Integer> getEncoderMap() {
            return frequencyMap;
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

        Map<Byte, Integer> frequencyMap =
                extractFrequencyMap(data, 
                                    numberOfCodeWords);

        BitString encodedText = extractEncodedText(data, 
                                                   frequencyMap,
                                                   numberOfBits);
        return new Result(encodedText, frequencyMap);
    }

    private void checkSignature(byte[] data) {
        if (data.length < 4) {
            throw new InvalidFormatException(
            "No file type signature. The file is too short: " + data.length);
        }

        for (int i = 0; i != HuffmanSerializer.MAGIC.length; ++i) {
            if (data[i] != HuffmanSerializer.MAGIC[i]) {
                throw new InvalidFormatException(
                "Bad file type signature.");
            }
        }
    }

    private int extractNumberOfCodeWords(byte[] data) {
        if (data.length < 8) {
            throw new InvalidFormatException(
            "No number of code words. The file is too short: " + data.length);
        }

        int numberOfCodeWords = 0;

        numberOfCodeWords |= (Byte.toUnsignedInt(data[7]) << 24);
        numberOfCodeWords |= (Byte.toUnsignedInt(data[6]) << 16);
        numberOfCodeWords |= (Byte.toUnsignedInt(data[5]) << 8);
        numberOfCodeWords |= (Byte.toUnsignedInt(data[4]));

        return numberOfCodeWords;
    }

    private Map<Byte, Integer> extractFrequencyMap(byte[] data,
                                                 int numberOfCodeWords) {
        Map<Byte, Integer> frequencyMap = new TreeMap<>();

        try {
            int dataByteIndex =
                    HuffmanSerializer.MAGIC.length +
                    HuffmanSerializer.BYTES_PER_BIT_COUNT_ENTRY +
                    HuffmanSerializer.BYTES_PER_CODE_WORD_COUNT_ENTRY;

            for (int i = 0; i != numberOfCodeWords; ++i) {
                byte character = data[dataByteIndex++];
                byte frequencyByte1 = data[dataByteIndex++];
                byte frequencyByte2 = data[dataByteIndex++];
                byte frequencyByte3 = data[dataByteIndex++];
                byte frequencyByte4 = data[dataByteIndex++];

                int frequency = Byte.toUnsignedInt(frequencyByte1);
                frequency |= (Byte.toUnsignedInt(frequencyByte2) << 8);
                frequency |= (Byte.toUnsignedInt(frequencyByte3) << 16);
                frequency |= (Byte.toUnsignedInt(frequencyByte4) << 24);

                frequencyMap.put(character, frequency);
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            throw new InvalidFormatException("Invalid format.");
        }

        return frequencyMap;
    }

    private BitString extractEncodedText(byte[] data,
                                         Map<Byte, Integer> frequencyMap,
                                         int numberOfEncodedTextBits) {
        int omittedBytes = HuffmanSerializer.MAGIC.length +
                           HuffmanSerializer.BYTES_PER_BIT_COUNT_ENTRY +
                           HuffmanSerializer.BYTES_PER_CODE_WORD_COUNT_ENTRY;

        omittedBytes += frequencyMap.size() * 
                        HuffmanSerializer.BYTES_PER_WEIGHT_MAP_ENTRY;

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
            throw new InvalidFormatException("Invalid file format.");
        }

        return encodedText;
    }

    private int extractNumberOfEncodedTextBits(byte[] data) {
        if (data.length < 12) {
            throw new InvalidFormatException(
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
