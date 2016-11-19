package net.coderodde.compression.huffman;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

/**
 * This class implements a Huffman tree for building a prefix code.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.61 (Nov 19, 2016)
 */
public final class HuffmanTree {
    
    private static final class HuffmanTreeNode 
            implements Comparable<HuffmanTreeNode> {

        byte character;
        float weight;
        boolean isLeaf;
        HuffmanTreeNode left;
        HuffmanTreeNode right;
        
        HuffmanTreeNode(byte character, float weight, boolean isLeaf) {
            checkWeight(weight);
            this.weight = weight;
            this.isLeaf = isLeaf;
            
            if (isLeaf) {
                this.character = character;
            }
        }
        
        @Override
        public int compareTo(HuffmanTreeNode o) {
            return Float.compare(weight, o.weight);
        }
        
        static HuffmanTreeNode merge(HuffmanTreeNode node1, 
                                     HuffmanTreeNode node2) {
            HuffmanTreeNode newNode = 
                    new HuffmanTreeNode((byte) 0,
                                        node1.weight + node2.weight,
                                        false);
            
            if (node1.weight < node2.weight) {
                newNode.left  = node1;
                newNode.right = node2;
            } else {
                newNode.left  = node2;
                newNode.right = node1;
            }
            
            return newNode;
        }
        
        private float checkWeight(float weight) {
            if (Double.isNaN(weight)) {
                throw new IllegalArgumentException("The input weight is NaN.");
            }
            
            if (weight <= 0.0f) {
                throw new IllegalArgumentException(
                "The input weight is not strictly positive: " + weight);
            }
            
            return weight;
        }
    }
    
    private HuffmanTreeNode root;
    
    public HuffmanTree(Map<Byte, Float> weightMap) {
        if (weightMap.isEmpty()) {
            throw new IllegalArgumentException(
                    "Compressor requires a non-empty text.");
        }
        
        Queue<HuffmanTreeNode> queue = new PriorityQueue<>();
        
        for (Map.Entry<Byte, Float> entry : weightMap.entrySet()) {
            queue.add(new HuffmanTreeNode(entry.getKey(),
                                          entry.getValue(),
                                          true));
        }
        
        while (queue.size() > 1) {
            HuffmanTreeNode node1 = queue.remove();
            HuffmanTreeNode node2 = queue.remove();
            queue.add(HuffmanTreeNode.merge(node1, node2));
        }
        
        root = queue.peek();
    }
    
    public Map<Byte, BitString> inferEncodingMap() {
        Map<Byte, BitString> map = new HashMap<>();
        
        if (root.isLeaf) {
            // Corner case. Only one byte value in the text.
            BitString bs = new BitString();
            bs.appendBit(false);
            map.put(root.character, bs);
            return map;
        }
        
        BitString bitStringBuilder = new BitString();
        inferEncodingMapImpl(bitStringBuilder, root, map);
        return map;
    }
    
    private void inferEncodingMapImpl(BitString currentCodeWord,
                                      HuffmanTreeNode currentTreeNode,
                                      Map<Byte, BitString> map) {
        if (currentTreeNode.isLeaf) {
            map.put(currentTreeNode.character, 
                    new BitString(currentCodeWord));
            return;
        }
        
        currentCodeWord.appendBit(false);
        inferEncodingMapImpl(currentCodeWord,
                             currentTreeNode.left,
                             map);
        currentCodeWord.removeLastBit();
        
        currentCodeWord.appendBit(true);
        inferEncodingMapImpl(currentCodeWord, 
                             currentTreeNode.right,
                             map);
        currentCodeWord.removeLastBit();
    }
}
