package net.coderodde.compression.huffman;

import org.junit.Test;
import static org.junit.Assert.*;

public class BitStringTest {
    
    @Test
    public void testAppendBit() {
        BitString b = new BitString();
        
        assertEquals(0, b.length());
        
        for (int i = 0; i < 100; ++i) {
            assertEquals(i, b.length());
            b.appendBit(false);
        }
        
        for (int i = 0; i < 30; ++i) {
            assertEquals(100 + i, b.length());
            b.appendBit(true);
        }
        
        assertEquals(130, b.length());
        
        for (int i = 0; i < 100; ++i) {
            assertFalse(b.readBit(i));
        }
        
        for (int i = 100; i < 130; ++i) {
            assertTrue(b.readBit(i));
        }
    }

    @Test
    public void testAppendBitsFrom() {
        BitString b = new BitString();
        BitString c = new BitString();
        
        for (int i = 0; i < 200; ++i) {
            b.appendBit(false);
        }
        
        for (int i = 0; i < 100; ++i) {
            c.appendBit(true);
        }
        
        assertEquals(200, b.length());
        assertEquals(100, c.length());
        
        b.appendBitsFrom(c);
        
        assertEquals(300, b.length());
        assertEquals(100, c.length());
        
        for (int i = 0; i < 200; ++i) {
            assertFalse(b.readBit(i));
        }
        
        for (int i = 200; i < 300; ++i) {
            assertTrue(b.readBit(i));
        }
    }

    @Test
    public void testReadBit() {
        BitString b = new BitString();
        b.appendBit(true);
        b.appendBit(false);
        b.appendBit(false);
        b.appendBit(true);
        b.appendBit(false);
        
        assertEquals(5, b.length());
        
        assertTrue(b.readBit(0));
        assertFalse(b.readBit(1));
        assertFalse(b.readBit(2));
        assertTrue(b.readBit(3));
        assertFalse(b.readBit(4));
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void testReadBitNegativeIndexThrows() {
        BitString b = new BitString();
        b.appendBit(true);
        b.readBit(-1);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void testReadBitTooLargeIndexThrows() {
        BitString b = new BitString();
        b.appendBit(false);
        b.readBit(1);
    }
    
    @Test(expected = IllegalStateException.class) 
    public void testThrowsOnAccessToEmptyBuilder() {
        new BitString().readBit(2);
    }

    @Test
    public void testRemoveLastBit() {
        BitString b = new BitString();
        
        b.appendBit(true);
        b.appendBit(true);
        b.appendBit(false);
        b.appendBit(true);
        b.appendBit(false);
        
        assertTrue(b.readBit(0));
        assertTrue(b.readBit(1));
        assertFalse(b.readBit(2));
        assertTrue(b.readBit(3));
        assertFalse(b.readBit(4));
        
        for (int i = 5; i > 0; --i) {
            assertEquals(i, b.length());
            b.removeLastBit();
            assertEquals(i - 1, b.length());
        }
    }
    
    @Test(expected = IllegalStateException.class)
    public void testRemoveLastBitOnEmptyBuilderThrows() {
        new BitString().removeLastBit();
    }

    @Test
    public void testClear() {
        BitString b = new BitString();
        
        for (int i = 0; i < 1000; ++i) {
            assertEquals(i, b.length());
            b.appendBit(true);
            assertEquals(i + 1, b.length());
        }
        
        b.clear();
        
        assertEquals(0, b.length());
    }    
    
    @Test
    public void testNumberOfBytes() {
        BitString b = new BitString();
        assertEquals(0, b.getNumberOfBytesOccupied());
        
        for (int i = 0; i < 100; ++i) {
            assertEquals(i, b.getNumberOfBytesOccupied());
            
            for (int j = 0; j < 8; ++j) {
                b.appendBit(true);
                assertEquals(i + 1, b.getNumberOfBytesOccupied());
            }
            
            assertEquals(i + 1, b.getNumberOfBytesOccupied());
        }
    }
    
    @Test
    public void testToByteArray() {
        BitString b = new BitString();
        
        for (int i = 0; i < 40; ++i) {
            b.appendBit(i % 2 == 1);
        }
        
        for (int i = 0; i < 80; ++i) {
            b.appendBit(i % 2 == 0);
        }
        
        byte[] array = b.toByteArray();
        
        for (int i = 0; i < 5; ++i) {
            assertEquals((byte) 0b10101010, array[i]);
        }
        
        for (int i = 5; i < 15; ++i) {
            assertEquals((byte) 0b01010101, array[i]);
        }
    }
}
