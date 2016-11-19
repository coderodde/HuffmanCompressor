package net.coderodde.app.huffman;

import java.util.Arrays;
import java.util.Map;
import net.coderodde.compression.huffman.BitString;
import net.coderodde.compression.huffman.CharacterWeightComputer;
import net.coderodde.compression.huffman.HuffmanDecoder;
import net.coderodde.compression.huffman.HuffmanDeserializer;
import net.coderodde.compression.huffman.HuffmanEncoder;
import net.coderodde.compression.huffman.HuffmanSerializer;
import net.coderodde.compression.huffman.HuffmanTree;

public class App {

    public static void main(String[] args) {
        byte[] bytes = new byte[256];
        
        for (int i = 0; i < 256; ++i) {
            bytes[i] = (byte) i;
        }
        
        Map<Byte, Float> weightMap = new CharacterWeightComputer()
                                        .computeCharacterWeights(bytes);
        
        Map<Byte, BitString> encoderMap = new HuffmanTree(weightMap)
                                              .inferEncodingMap();
        
        BitString encodedText = new HuffmanEncoder().encode(encoderMap, bytes);
        byte[] decodedText = new HuffmanDecoder().decode(encodedText, encoderMap);
        
        System.out.println(Arrays.equals(decodedText, bytes));
        
        byte[] serializedData = new HuffmanSerializer().serialize(encoderMap, encodedText);
        HuffmanDeserializer.Result result = new HuffmanDeserializer().deserialize(serializedData);
        
        byte[] recoveredText = new HuffmanDecoder().decode(result.getEncodedText(), result.getEncoderMap());
        
        System.out.println(Arrays.equals(bytes, recoveredText));
    }
}
