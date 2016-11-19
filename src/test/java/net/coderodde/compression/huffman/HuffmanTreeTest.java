package net.coderodde.compression.huffman;

import java.util.HashMap;
import org.junit.Test;

public class HuffmanTreeTest {

    @Test(expected = IllegalArgumentException.class)
    public void testThrowsOnEmptyText() {
        new HuffmanTree(new HashMap<>());
    }
}
