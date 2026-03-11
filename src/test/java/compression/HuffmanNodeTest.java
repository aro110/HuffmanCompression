package compression;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HuffmanNodeTest {

    @Test
    void testLeafNodeCreation() {
        HuffmanNode leaf = new HuffmanNode(65L, 10, 1);

        assertEquals(65L, leaf.getSymbol());
        assertEquals(10, leaf.getFrequency());
        assertEquals(1, leaf.getGroupSize());
        assertTrue(leaf.isLeaf());
        assertNull(leaf.getLeft());
        assertNull(leaf.getRight());
    }

    @Test
    void testInternalNodeCreation() {
        HuffmanNode left = new HuffmanNode(65L, 5, 1);
        HuffmanNode right = new HuffmanNode(66L, 3, 1);
        HuffmanNode parent = new HuffmanNode(8, left, right, 1);

        assertEquals(8, parent.getFrequency());
        assertEquals(1, parent.getGroupSize());
        assertFalse(parent.isLeaf());
        assertSame(left, parent.getLeft());
        assertSame(right, parent.getRight());
    }

    @Test
    void testCompareToLowerFrequency() {
        HuffmanNode node1 = new HuffmanNode(65L, 5, 1);
        HuffmanNode node2 = new HuffmanNode(66L, 10, 1);

        assertTrue(node1.compareTo(node2) < 0);
    }

    @Test
    void testCompareToHigherFrequency() {
        HuffmanNode node1 = new HuffmanNode(65L, 10, 1);
        HuffmanNode node2 = new HuffmanNode(66L, 5, 1);

        assertTrue(node1.compareTo(node2) > 0);
    }

    @Test
    void testCompareToEqualFrequency() {
        HuffmanNode node1 = new HuffmanNode(65L, 5, 1);
        HuffmanNode node2 = new HuffmanNode(66L, 5, 1);

        assertEquals(0, node1.compareTo(node2));
    }

    @Test
    void testGroupSize2() {
        HuffmanNode node = new HuffmanNode(0x4142L, 10, 2);

        assertEquals(0x4142L, node.getSymbol());
        assertEquals(2, node.getGroupSize());
    }

    @Test
    void testGroupSize3() {
        HuffmanNode node = new HuffmanNode(0x414243L, 10, 3);

        assertEquals(0x414243L, node.getSymbol());
        assertEquals(3, node.getGroupSize());
    }

    @Test
    void testGroupSize4() {
        HuffmanNode node = new HuffmanNode(0x41424344L, 10, 4);

        assertEquals(0x41424344L, node.getSymbol());
        assertEquals(4, node.getGroupSize());
    }

    @Test
    void testInternalNodeWithDifferentGroupSizes() {
        HuffmanNode left = new HuffmanNode(65L, 5, 2);
        HuffmanNode right = new HuffmanNode(66L, 3, 2);
        HuffmanNode parent = new HuffmanNode(8, left, right, 2);

        assertEquals(2, parent.getGroupSize());
        assertEquals(2, left.getGroupSize());
        assertEquals(2, right.getGroupSize());
    }

    @Test
    void testZeroFrequency() {
        HuffmanNode node = new HuffmanNode(65L, 0, 1);
        assertEquals(0, node.getFrequency());
    }

    @Test
    void testLargeFrequency() {
        HuffmanNode node = new HuffmanNode(65L, Long.MAX_VALUE, 1);
        assertEquals(Long.MAX_VALUE, node.getFrequency());
    }

    @Test
    void testZeroSymbol() {
        HuffmanNode node = new HuffmanNode(0L, 10, 1);
        assertEquals(0L, node.getSymbol());
    }

    @Test
    void testMaxSymbolForGroupSize1() {
        HuffmanNode node = new HuffmanNode(0xFFL, 10, 1);
        assertEquals(0xFFL, node.getSymbol());
    }

    @Test
    void testMaxSymbolForGroupSize2() {
        HuffmanNode node = new HuffmanNode(0xFFFFL, 10, 2);
        assertEquals(0xFFFFL, node.getSymbol());
    }

    @Test
    void testMaxSymbolForGroupSize3() {
        HuffmanNode node = new HuffmanNode(0xFFFFFFL, 10, 3);
        assertEquals(0xFFFFFFL, node.getSymbol());
    }

    @Test
    void testMaxSymbolForGroupSize4() {
        HuffmanNode node = new HuffmanNode(0xFFFFFFFFL, 10, 4);
        assertEquals(0xFFFFFFFFL, node.getSymbol());
    }

    @Test
    void testDeepTree() {
        HuffmanNode leaf1 = new HuffmanNode(65L, 1, 1);
        HuffmanNode leaf2 = new HuffmanNode(66L, 2, 1);
        HuffmanNode leaf3 = new HuffmanNode(67L, 3, 1);

        HuffmanNode node1 = new HuffmanNode(3, leaf1, leaf2, 1);
        HuffmanNode root = new HuffmanNode(6, node1, leaf3, 1);

        assertFalse(root.isLeaf());
        assertSame(node1, root.getLeft());
        assertSame(leaf3, root.getRight());
        assertEquals(6, root.getFrequency());
    }

    @Test
    void testInternalNodeSymbolIsZero() {
        HuffmanNode left = new HuffmanNode(65L, 5, 1);
        HuffmanNode right = new HuffmanNode(66L, 3, 1);
        HuffmanNode parent = new HuffmanNode(8, left, right, 1);

        assertEquals(0, parent.getSymbol());
    }

    @Test
    void testLeafChildrenAreNull() {
        HuffmanNode leaf = new HuffmanNode(65L, 10, 1);

        assertNull(leaf.getLeft());
        assertNull(leaf.getRight());
    }

    @Test
    void testCompareWithLongFrequencies() {
        HuffmanNode node1 = new HuffmanNode(1L, 1000000000L, 1);
        HuffmanNode node2 = new HuffmanNode(2L, 2000000000L, 1);

        assertTrue(node1.compareTo(node2) < 0);
    }
}