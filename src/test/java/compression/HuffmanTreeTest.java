package compression;

import dataStructure.HashMap;
import io.WriteFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class HuffmanTreeTest {

    private HuffmanTree tree;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        tree = new HuffmanTree();
    }

    @Test
    void testBuildHuffmanTreeWithValidFrequenciesGroupSize1() {
        HashMap<Long, Integer> frequencies = new HashMap<>(16);
        frequencies.put(97L, 5);
        frequencies.put(98L, 9);
        frequencies.put(99L, 12);

        HuffmanNode root = tree.buildHuffmanTree(frequencies, 1);

        assertNotNull(root);
        assertEquals(26, root.getFrequency());
        assertFalse(root.isLeaf());
    }

    @Test
    void testBuildHuffmanTreeWithSingleSymbol() {
        HashMap<Long, Integer> frequencies = new HashMap<>(16);
        frequencies.put(120L, 42);

        HuffmanNode root = tree.buildHuffmanTree(frequencies, 1);

        assertNotNull(root);
        assertEquals(42, root.getFrequency());
        assertTrue(root.isLeaf());
        assertEquals(120L, root.getSymbol());
    }

    @Test
    void testBuildHuffmanTreeWithEmptyFrequencies() {
        HashMap<Long, Integer> frequencies = new HashMap<>(16);

        HuffmanNode root = tree.buildHuffmanTree(frequencies, 1);

        assertNull(root);
    }

    @Test
    void testBuildHuffmanTreeGroupSize2() {
        HashMap<Long, Integer> frequencies = new HashMap<>(16);
        frequencies.put(0x4142L, 5);
        frequencies.put(0x4344L, 10);

        HuffmanNode root = tree.buildHuffmanTree(frequencies, 2);

        assertNotNull(root);
        assertEquals(15, root.getFrequency());
        assertEquals(2, root.getGroupSize());
    }

    @Test
    void testBuildHuffmanTreeGroupSize3() {
        HashMap<Long, Integer> frequencies = new HashMap<>(16);
        frequencies.put(0x414243L, 3);
        frequencies.put(0x444546L, 7);

        HuffmanNode root = tree.buildHuffmanTree(frequencies, 3);

        assertNotNull(root);
        assertEquals(10, root.getFrequency());
        assertEquals(3, root.getGroupSize());
    }

    @Test
    void testBuildHuffmanTreeGroupSize4() {
        HashMap<Long, Integer> frequencies = new HashMap<>(16);
        frequencies.put(0x41424344L, 2);
        frequencies.put(0x45464748L, 8);

        HuffmanNode root = tree.buildHuffmanTree(frequencies, 4);

        assertNotNull(root);
        assertEquals(10, root.getFrequency());
        assertEquals(4, root.getGroupSize());
    }

    @Test
    void testBuildHuffmanTreeWithTwoSymbols() {
        HashMap<Long, Integer> frequencies = new HashMap<>(16);
        frequencies.put(65L, 3);
        frequencies.put(66L, 7);

        HuffmanNode root = tree.buildHuffmanTree(frequencies, 1);

        assertNotNull(root);
        assertEquals(10, root.getFrequency());
        assertFalse(root.isLeaf());
        assertNotNull(root.getLeft());
        assertNotNull(root.getRight());
        assertTrue(root.getLeft().isLeaf());
        assertTrue(root.getRight().isLeaf());
    }

    @Test
    void testGenerateCodesWithValidTree() {
        HashMap<Long, Integer> frequencies = new HashMap<>(16);
        frequencies.put(97L, 5);
        frequencies.put(98L, 9);
        frequencies.put(99L, 12);

        HuffmanNode root = tree.buildHuffmanTree(frequencies, 1);
        HashMap<Long, Integer> codes = new HashMap<>(16);
        HashMap<Long, Integer> lengths = new HashMap<>(16);
        tree.generateCodes(root, codes, lengths, 0, 0);

        assertNotNull(codes.get(97L));
        assertNotNull(codes.get(98L));
        assertNotNull(codes.get(99L));
        assertEquals(3, codes.size());
    }

    @Test
    void testGenerateCodesLengthsArePositive() {
        HashMap<Long, Integer> frequencies = new HashMap<>(16);
        frequencies.put(120L, 5);
        frequencies.put(121L, 10);
        frequencies.put(122L, 15);

        HuffmanNode root = tree.buildHuffmanTree(frequencies, 1);
        HashMap<Long, Integer> codes = new HashMap<>(16);
        HashMap<Long, Integer> lengths = new HashMap<>(16);
        tree.generateCodes(root, codes, lengths, 0, 0);

        for (HashMap.Entry<Long, Integer> entry : lengths.entrySet()) {
            assertTrue(entry.getValue() > 0);
        }
    }

    @Test
    void testGenerateCodesNoPrefixProperty() {
        HashMap<Long, Integer> frequencies = new HashMap<>(16);
        frequencies.put(1L, 1);
        frequencies.put(2L, 2);
        frequencies.put(3L, 4);
        frequencies.put(4L, 8);

        HuffmanNode root = tree.buildHuffmanTree(frequencies, 1);
        HashMap<Long, Integer> codes = new HashMap<>(16);
        HashMap<Long, Integer> lengths = new HashMap<>(16);
        tree.generateCodes(root, codes, lengths, 0, 0);

        Set<String> codeStrings = new HashSet<>();
        for (HashMap.Entry<Long, Integer> entry : codes.entrySet()) {
            int code = entry.getValue();
            int len = lengths.get(entry.getKey());
            String codeStr = toBinaryString(code, len);
            codeStrings.add(codeStr);
        }

        for (String code1 : codeStrings) {
            for (String code2 : codeStrings) {
                if (!code1.equals(code2)) {
                    assertFalse(code2.startsWith(code1));
                }
            }
        }
    }

    @Test
    void testGenerateCodesWithSingleSymbol() {
        HashMap<Long, Integer> frequencies = new HashMap<>(16);
        frequencies.put(122L, 100);

        HuffmanNode root = tree.buildHuffmanTree(frequencies, 1);
        HashMap<Long, Integer> codes = new HashMap<>(16);
        HashMap<Long, Integer> lengths = new HashMap<>(16);
        tree.generateCodes(root, codes, lengths, 0, 0);

        assertNotNull(codes.get(122L));
        assertNotNull(lengths.get(122L));
    }

    @Test
    void testGenerateCodesGroupSize2() {
        HashMap<Long, Integer> frequencies = new HashMap<>(16);
        frequencies.put(0x4142L, 5);
        frequencies.put(0x4344L, 10);

        HuffmanNode root = tree.buildHuffmanTree(frequencies, 2);
        HashMap<Long, Integer> codes = new HashMap<>(16);
        HashMap<Long, Integer> lengths = new HashMap<>(16);
        tree.generateCodes(root, codes, lengths, 0, 0);

        assertNotNull(codes.get(0x4142L));
        assertNotNull(codes.get(0x4344L));
    }

    @Test
    void testSerializeLeafNode() throws IOException {
        Path tempFile = tempDir.resolve("tree.bin");

        HashMap<Long, Integer> frequencies = new HashMap<>(16);
        frequencies.put(65L, 10);

        HuffmanNode root = tree.buildHuffmanTree(frequencies, 1);

        try (WriteFile writer = new WriteFile(tempFile.toString())) {
            tree.serialize(root, writer);
        }

        byte[] data = Files.readAllBytes(tempFile);
        assertTrue(data.length > 0);
    }

    @Test
    void testSerializeInternalNode() throws IOException {
        Path tempFile = tempDir.resolve("tree.bin");

        HashMap<Long, Integer> frequencies = new HashMap<>(16);
        frequencies.put(65L, 5);
        frequencies.put(66L, 10);

        HuffmanNode root = tree.buildHuffmanTree(frequencies, 1);

        try (WriteFile writer = new WriteFile(tempFile.toString())) {
            tree.serialize(root, writer);
        }

        byte[] data = Files.readAllBytes(tempFile);
        assertTrue(data.length > 0);
    }

    @Test
    void testSerializeGroupSize2() throws IOException {
        Path tempFile = tempDir.resolve("tree.bin");

        HashMap<Long, Integer> frequencies = new HashMap<>(16);
        frequencies.put(0x4142L, 5);

        HuffmanNode root = tree.buildHuffmanTree(frequencies, 2);

        try (WriteFile writer = new WriteFile(tempFile.toString())) {
            tree.serialize(root, writer);
        }

        byte[] data = Files.readAllBytes(tempFile);
        assertTrue(data.length >= 3);
    }

    @Test
    void testSerializeGroupSize3() throws IOException {
        Path tempFile = tempDir.resolve("tree.bin");

        HashMap<Long, Integer> frequencies = new HashMap<>(16);
        frequencies.put(0x414243L, 5);

        HuffmanNode root = tree.buildHuffmanTree(frequencies, 3);

        try (WriteFile writer = new WriteFile(tempFile.toString())) {
            tree.serialize(root, writer);
        }

        byte[] data = Files.readAllBytes(tempFile);
        assertTrue(data.length >= 4);
    }

    @Test
    void testSerializeGroupSize4() throws IOException {
        Path tempFile = tempDir.resolve("tree.bin");

        HashMap<Long, Integer> frequencies = new HashMap<>(16);
        frequencies.put(0x41424344L, 5);

        HuffmanNode root = tree.buildHuffmanTree(frequencies, 4);

        try (WriteFile writer = new WriteFile(tempFile.toString())) {
            tree.serialize(root, writer);
        }

        byte[] data = Files.readAllBytes(tempFile);
        assertTrue(data.length >= 5);
    }

    @Test
    void testCodeHuffmanTreeTwoSymbols() throws IOException {
        Path tempFile = tempDir.resolve("tree.bin");

        HashMap<Long, Integer> frequencies = new HashMap<>(16);
        frequencies.put(65L, 3);
        frequencies.put(66L, 7);

        HuffmanNode root = tree.buildHuffmanTree(frequencies, 1);

        try (WriteFile writer = new WriteFile(tempFile.toString())) {
            tree.codeHuffmanTree(root, writer);
        }

        byte[] data = Files.readAllBytes(tempFile);
        assertTrue(data.length > 0);
    }

    @Test
    void testFrequentSymbolsShorterCodes() {
        HashMap<Long, Integer> frequencies = new HashMap<>(16);
        frequencies.put(1L, 1);
        frequencies.put(2L, 10);
        frequencies.put(3L, 100);

        HuffmanNode root = tree.buildHuffmanTree(frequencies, 1);
        HashMap<Long, Integer> codes = new HashMap<>(16);
        HashMap<Long, Integer> lengths = new HashMap<>(16);
        tree.generateCodes(root, codes, lengths, 0, 0);

        assertTrue(lengths.get(3L) <= lengths.get(2L));
        assertTrue(lengths.get(2L) <= lengths.get(1L));
    }

    @Test
    void testMultipleGroupSizes() {
        for (int groupSize = 1; groupSize <= 4; groupSize++) {
            HashMap<Long, Integer> frequencies = new HashMap<>(16);
            frequencies.put(1L, 5);
            frequencies.put(2L, 10);

            HuffmanNode root = tree.buildHuffmanTree(frequencies, groupSize);

            assertNotNull(root);
            assertEquals(groupSize, root.getGroupSize());
            assertEquals(15, root.getFrequency());
        }
    }

    @Test
    void testBuildTreeWithManySymbols() {
        HashMap<Long, Integer> frequencies = new HashMap<>(512);
        for (long i = 0; i < 256; i++) {
            frequencies.put(i, (int) (i + 1));
        }

        HuffmanNode root = tree.buildHuffmanTree(frequencies, 1);

        assertNotNull(root);
        assertFalse(root.isLeaf());
    }

    @Test
    void testGenerateCodesUniqueness() {
        HashMap<Long, Integer> frequencies = new HashMap<>(16);
        frequencies.put(65L, 5);
        frequencies.put(66L, 9);
        frequencies.put(67L, 12);
        frequencies.put(68L, 13);

        HuffmanNode root = tree.buildHuffmanTree(frequencies, 1);
        HashMap<Long, Integer> codes = new HashMap<>(16);
        HashMap<Long, Integer> lengths = new HashMap<>(16);
        tree.generateCodes(root, codes, lengths, 0, 0);

        Set<String> uniqueCodes = new HashSet<>();
        for (HashMap.Entry<Long, Integer> entry : codes.entrySet()) {
            int code = entry.getValue();
            int len = lengths.get(entry.getKey());
            String codeStr = toBinaryString(code, len);
            assertTrue(uniqueCodes.add(codeStr));
        }
    }

    private String toBinaryString(int code, int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = length - 1; i >= 0; i--) {
            sb.append((code >> i) & 1);
        }
        return sb.toString();
    }
}