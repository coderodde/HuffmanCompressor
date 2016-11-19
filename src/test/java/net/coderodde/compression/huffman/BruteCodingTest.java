package net.coderodde.compression.huffman;

import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

public final class BruteCodingTest {

    private static final int ITERATIONS = 10;
    private static final int MAX_STRING_LENGTH = 1000;
    
    @Test
    public void testBrute() {
        long seed = 10214L; System.currentTimeMillis();
        Random random = new Random(seed);
        
        System.out.println("Seed = " + seed);
        
        for (int iteration = 0; iteration < ITERATIONS; ++iteration) {
            System.out.println("Iteration " + iteration);
            byte[] text = randomBytes(1 + random.nextInt(MAX_STRING_LENGTH),
                                      random);
            Map<Byte, Float> weightMap = 
                    new CharacterWeightComputer().computeCharacterWeights(text);
            
            Map<Byte, BitString> encoderMap =
                    new HuffmanTree(weightMap).inferEncodingMap();
            
            BitString encodedText = new HuffmanEncoder().encode(encoderMap, 
                                                                text);
            
            byte[] encodedData = new HuffmanSerializer().serialize(encoderMap,
                                                                   encodedText);
            // Correct until here.
            HuffmanDeserializer.Result deserializationResult =
                    new HuffmanDeserializer().deserialize(encodedData);
            
            if (iteration == 1) {
                System.out.println("Checking...");
                Map<Byte, BitString> deser = deserializationResult.getEncoderMap();
                for (Map.Entry<Byte, BitString> e : encoderMap.entrySet()) {
                    if (!deser.get(e.getKey()).equals(e.getValue())) {
                        System.out.println(e.getKey() + ": " + e.getValue() + " vs " + deser.get(e.getKey()));
                    }
                }
            }
            
            assertEquals(deserializationResult.getEncoderMap(),
                         encoderMap);
            
            assertEquals(encodedText, deserializationResult.getEncodedText());
            
            byte[] recoveredText =
                    new HuffmanDecoder()
                            .decode(deserializationResult.getEncodedText(),
                                    deserializationResult.getEncoderMap());
            
            assertTrue(Arrays.equals(text, recoveredText));
        }
    }
    
    private byte[] randomBytes(int length, Random random) {
        byte[] bytes = new byte[length];
        
        for (int i = 0; i < length; ++i) {
            bytes[i] = (byte) random.nextInt();
        }
        
        return bytes;
    }
}
