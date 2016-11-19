package net.coderodde.compression.huffman;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

public class HuffmanDeserializerTest {

    @Test
    public void testDeserialize() {
        Map<Byte, BitString> encoderMap = new HashMap<>();
        BitString bitStringA = new BitString();
        
        for (int i = 0; i < 4; ++i) {
            bitStringA.appendBit(true);
        }
        
        for (int i = 0; i < 3; ++i) {
            bitStringA.appendBit(false);
        }
        
        BitString bitStringB = new BitString();
        
        for (int i = 0; i < 1; ++i) {
            bitStringB.appendBit(false);
        }
        
        for (int i = 0; i < 5; ++i) {
            bitStringB.appendBit(true);
        }
        
        BitString bitStringC = new BitString();
        
        for (int i = 0; i < 2; ++i) {
            bitStringC.appendBit(false);
        }
        
        for (int i = 0; i < 6; ++i) {
            bitStringC.appendBit(true);
        }
        
        encoderMap.put((byte) 0, bitStringA);
        encoderMap.put((byte) 1, bitStringB);
        encoderMap.put((byte) 2, bitStringC);
        
        byte[] text = {(byte) 0, 
                       (byte) 1, 
                       (byte) 1, 
                       (byte) 2, 
                       (byte) 1, 
                       (byte) 2, 
                       (byte) 1,
                       (byte) 2, 
                       (byte) 2};
        
        BitString encodedText = new HuffmanEncoder().encode(encoderMap, text);
        
        byte[] data = new HuffmanSerializer().serialize(encoderMap, 
                                                        encodedText);   
        
        HuffmanDeserializer.Result result = 
                new HuffmanDeserializer().deserialize(data);
        
        assertEquals(encoderMap, result.getEncoderMap());
    }    
}
