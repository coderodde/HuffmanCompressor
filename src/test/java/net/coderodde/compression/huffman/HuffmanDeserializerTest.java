//package net.coderodde.compression.huffman;
//
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.Map;
//import org.junit.Test;
//import static org.junit.Assert.*;
//
//public class HuffmanDeserializerTest {
//
//    @Test
//    public void testDeserialize() {
//        Map<Byte, BitString> encoderMap = new HashMap<>();
//        BitString bitStringA = new BitString();
//        
//        for (int i = 0; i < 4; ++i) {
//            bitStringA.appendBit(true);
//        }
//        
//        for (int i = 0; i < 3; ++i) {
//            bitStringA.appendBit(false);
//        }
//        
//        BitString bitStringB = new BitString();
//        
//        for (int i = 0; i < 1; ++i) {
//            bitStringB.appendBit(false);
//        }
//        
//        for (int i = 0; i < 5; ++i) {
//            bitStringB.appendBit(true);
//        }
//        
//        BitString bitStringC = new BitString();
//        
//        for (int i = 0; i < 2; ++i) {
//            bitStringC.appendBit(false);
//        }
//        
//        for (int i = 0; i < 6; ++i) {
//            bitStringC.appendBit(true);
//        }
//        
//        encoderMap.put((byte) 0, bitStringA);
//        encoderMap.put((byte) 1, bitStringB);
//        encoderMap.put((byte) 2, bitStringC);
//        
//        byte[] text = {(byte) 0, 
//                       (byte) 1, 
//                       (byte) 1, 
//                       (byte) 2, 
//                       (byte) 1, 
//                       (byte) 2, 
//                       (byte) 1,
//                       (byte) 2, 
//                       (byte) 2};
//        
//        BitString encodedText = new HuffmanEncoder().encode(encoderMap, text);
//        
//        byte[] data = new HuffmanSerializer().serialize(encoderMap, 
//                                                        encodedText);   
//        
//        HuffmanDeserializer.Result result = 
//                new HuffmanDeserializer().deserialize(data);
//        
//        assertEquals(encoderMap, result.getEncoderMap());
//    }    
//    
//    @Test(expected = InvalidFileFormatException.class) 
//    public void testBadSignature() {
//        byte[] data = new byte[100];
//        data[0] = HuffmanSerializer.MAGIC[0];
//        data[1] = HuffmanSerializer.MAGIC[1];
//        data[3] = HuffmanSerializer.MAGIC[3];
//        new HuffmanDeserializer().deserialize(data);
//    }
//    
//    @Test(expected = InvalidFileFormatException.class)
//    public void testTooShortText() {
//        byte[] text = { (byte) 1, (byte) 2, (byte) 3, (byte) 4,
//                        (byte) 5, (byte) 6, (byte) 7, (byte) 8,
//                        (byte) 9, (byte) 0, (byte) 11, (byte) 10 };
//        
//        Map<Byte, BitString> encoderMap = 
//                new HuffmanTree(
//                        new ByteWeightComputer()
//                                .computeCharacterWeights(text))
//                        .inferEncodingMap();
//        
//        BitString encodedData = new HuffmanEncoder().encode(encoderMap, text);
//        byte[] shit = new HuffmanSerializer().serialize(encoderMap, 
//                                                        encodedData);
//        shit = Arrays.copyOf(shit, shit.length - 2);
//        new HuffmanDeserializer().deserialize(shit);
//    }
//}
