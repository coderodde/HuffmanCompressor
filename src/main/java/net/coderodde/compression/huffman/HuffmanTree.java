package net.coderodde.compression.huffman;

import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.TreeMap;

/**
 * This class implements a Huffman tree for building a prefix code.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.61 (Nov 19, 2016)
 */
public final class HuffmanTree {

    static final class IntHolder {
        int value;
    }

    private static final class HuffmanTreeNode 
            implements Comparable<HuffmanTreeNode> {

        byte character;
        int  frequency;
        boolean isLeaf;
        HuffmanTreeNode left;
        HuffmanTreeNode right;

        HuffmanTreeNode(byte character, int frequency, boolean isLeaf) {
            this.frequency = checkFrequency(frequency);
            this.isLeaf = isLeaf;

            if (isLeaf) {
                this.character = character;
            }
        }

        @Override
        public int compareTo(HuffmanTreeNode o) {
            int cmp = Integer.compare(frequency, o.frequency);

            if (cmp != 0) {
                return cmp;
            }

            // If reached here, equal weights so order by the character value:
            return Byte.compare(character, o.character);
        }

        static HuffmanTreeNode merge(HuffmanTreeNode node1, 
                                     HuffmanTreeNode node2) {
            HuffmanTreeNode newNode = 
                    new HuffmanTreeNode((byte) 0,
                                        node1.frequency + node2.frequency,
                                        false);

            if (node1.frequency < node2.frequency) {
                newNode.left  = node1;
                newNode.right = node2;
            } else {
                newNode.left  = node2;
                newNode.right = node1;
            }

            return newNode;
        }

        private int checkFrequency(int frequency) {
            if (frequency <= 0) {
                throw new IllegalArgumentException(
                "The input byte frequency must be positive. Received " +
                        frequency + ".");
            }

            return frequency;
        }
    }

    private HuffmanTreeNode root;

    /**
     * Constructs a Huffman tree from the character frequencies 
     * {@code weightMap}.
     * 
     * @param frequencyMap the map mapping each byte to its frequency.
     */
    public HuffmanTree(Map<Byte, Integer> frequencyMap) {
        if (frequencyMap.isEmpty()) {
            throw new IllegalArgumentException(
                    "Compressor requires a non-empty text.");
        }

        Queue<HuffmanTreeNode> queue = new PriorityQueue<>();

        for (Map.Entry<Byte, Integer> entry : frequencyMap.entrySet()) {
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

    public byte decodeBitString(IntHolder index, BitString bitString) {
        if (root.isLeaf) {
            // Ugly special case: the encoded text contains only one distinct
            // byte value. Return it and increment the index holder. If we would
            // not handle this special case. The below while loop would become
            // infinite.
            index.value++;
            return root.character;
        }

        HuffmanTreeNode currentNode = root;

        while (currentNode.isLeaf == false) {
            boolean bit = bitString.readBit(index.value++);
            currentNode = (bit ? currentNode.right : currentNode.left);
        }

        return currentNode.character;
    }

    /**
     * Construct the encoder map from this tree.
     * 
     * @return the encoder map.
     */
    public Map<Byte, BitString> inferEncodingMap() {
        Map<Byte, BitString> map = new TreeMap<>();

        if (root.isLeaf) {
            // Corner case. Only one byte value in the text.
            root.isLeaf = false;
            root.left = new HuffmanTreeNode(root.character, 
                                            1, 
                                            true);
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
