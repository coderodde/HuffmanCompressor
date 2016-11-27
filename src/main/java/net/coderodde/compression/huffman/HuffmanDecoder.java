package net.coderodde.compression.huffman;

import net.coderodde.compression.huffman.HuffmanTree.IntHolder;

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
     * @param tree the Huffman tree used for decoding.
     * @param bits the actual encoded text bits.
     * @return the recovered text.
     */
    public byte[] decode(HuffmanTree tree, BitString bits) {
        IntHolder index = new IntHolder();
        int bitStringLength = bits.length();
        ByteList byteList = new ByteList();
        
        while (index.value < bitStringLength) {
            byte character = tree.decodeBitString(index, bits);
            byteList.appendByte(character);
        }
        
        return byteList.toByteArray();
    }
}
