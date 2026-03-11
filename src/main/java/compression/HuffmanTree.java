package compression;

import dataStructure.HashMap;
import dataStructure.PriorityQueue;
import io.WriteFile;

import java.io.IOException;

public class HuffmanTree {

    public HuffmanNode buildHuffmanTree(HashMap<Long, Integer> frequencies, int groupSize) {
        PriorityQueue<HuffmanNode> pq = new PriorityQueue<>();

        for (HashMap.Entry<Long, Integer> entry : frequencies.entrySet()) {
            long symbol = entry.getKey();
            int frequency = entry.getValue();
            HuffmanNode node = new HuffmanNode(symbol, frequency, groupSize);
            pq.insert(node);
        }

        if (pq.size() == 0) {
            return null;
        }

        if (pq.size() == 1) {
            return pq.extractMin();
        }

        while (pq.size() > 1) {
            HuffmanNode left = pq.extractMin();
            HuffmanNode right = pq.extractMin();
            HuffmanNode parent = new HuffmanNode(left.getFrequency() + right.getFrequency(), left, right, groupSize);
            pq.insert(parent);
        }

        return pq.extractMin();
    }

    public void generateCodes(HuffmanNode node, HashMap<Long, Integer> codes, HashMap<Long, Integer> lengths, int currentCode, int currentLen) {
        if (node == null) {
            return;
        }

        if (node.isLeaf()) {
            if (currentLen == 0) {
                codes.put(node.getSymbol(), 0);
                lengths.put(node.getSymbol(), 1);
            } else {
                codes.put(node.getSymbol(), currentCode);
                lengths.put(node.getSymbol(), currentLen);
            }
            return;
        }
        if (node.getLeft() != null) {
            generateCodes(node.getLeft(), codes, lengths, currentCode << 1, currentLen + 1);
        }
        if (node.getRight() != null) {
            generateCodes(node.getRight(), codes, lengths, (currentCode << 1) | 1, currentLen + 1);
        }
    }

    public void serialize(HuffmanNode node, WriteFile writer) throws IOException {
        if (node.isLeaf()) {
            writer.writeBit(1);
            writer.writeBits(node.getSymbol(), node.getGroupSize() * 8);
        } else {
            writer.writeBit(0);
            serialize(node.getLeft(), writer);
            serialize(node.getRight(), writer);
        }
    }

    public void codeHuffmanTree(HuffmanNode node, WriteFile writer) throws IOException {
        serialize(node, writer);
    }
}