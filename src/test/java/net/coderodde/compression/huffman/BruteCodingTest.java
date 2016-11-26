package net.coderodde.compression.huffman;

import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

public final class BruteCodingTest {

    private static final int ITERATIONS = 1;
    private static final int MAX_STRING_LENGTH = 10;
    
    @Test
    public void testBrute() {
        long seed = System.currentTimeMillis();
        Random random = new Random(seed);
        
        System.out.println("Seed = " + seed);
        
        for (int iteration = 0; iteration < ITERATIONS; ++iteration) {
            byte[] text = randomBytes(1 + random.nextInt(MAX_STRING_LENGTH),
                                      random);
            Map<Byte, Float> weightMap = 
                    new ByteWeightComputer().computeCharacterWeights(text);
            
            HuffmanTree tree = new HuffmanTree(weightMap);
            
            Map<Byte, BitString> encoderMap = tree.inferEncodingMap();
            
            HuffmanEncoder encoder = new HuffmanEncoder();
            
            BitString encodedText = encoder.encode(encoderMap, text);
            
            HuffmanSerializer serializer = new HuffmanSerializer();
            byte[] encodedData = serializer.serialize(weightMap, encodedText);
            
            HuffmanDeserializer deserializer = new HuffmanDeserializer();
            HuffmanDeserializer.Result result = 
                    deserializer.deserialize(encodedData);
            
            HuffmanTree decoderTree = new HuffmanTree(result.getEncoderMap());
            HuffmanDecoder decoder = new HuffmanDecoder();
            byte[] recoveredText = decoder.decode(decoderTree, 
                                                  result.getEncodedText());
            
            assertEquals(text.length, recoveredText.length);
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
