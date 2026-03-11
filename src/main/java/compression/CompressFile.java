package compression;

import dataStructure.HashMap;
import dataStructure.PriorityQueue;
import io.WriteFile;
import util.Utils;

import java.io.*;

public class CompressFile {

    private static final int BUFFER_SIZE = 8192;

    public void compress(String inputPath, String outputPath, int groupSize) {
        try {
            long originalFileSize = new java.io.File(inputPath).length();
            HashMap<Long, Long> frequencies = Utils.countFrequencies(inputPath, groupSize);

            if (frequencies.size() == 0) {
                writeEmptyFile(outputPath, groupSize);
                return;
            }

            PriorityQueue<HuffmanNode> pq = new PriorityQueue<>();
            for (HashMap.Entry<Long, Long> entry : frequencies.entrySet()) {
                pq.insert(new HuffmanNode(entry.key, entry.value, groupSize));
            }

            while (pq.size() > 1) {
                HuffmanNode left = pq.extractMin();
                HuffmanNode right = pq.extractMin();
                pq.insert(new HuffmanNode(left.getFrequency() + right.getFrequency(), left, right, groupSize));
            }

            HuffmanNode root = pq.extractMin();
            HuffmanTree tree = new HuffmanTree();
            HashMap<Long, Integer> codes = new HashMap<>(frequencies.size() * 2);
            HashMap<Long, Integer> lengths = new HashMap<>(frequencies.size() * 2);
            tree.generateCodes(root, codes, lengths, 0, 0);

            writeCompressedFile(inputPath, outputPath, root, codes, lengths, groupSize, originalFileSize);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void writeEmptyFile(String outputPath, int groupSize) throws IOException {
        try (WriteFile writer = new WriteFile(outputPath)) {
            writer.writeBits(groupSize, 3);
            writer.writeBits(0L, 32);
        }
    }

    private void writeCompressedFile(String inputPath, String outputPath, HuffmanNode root,
                                     HashMap<Long, Integer> codes, HashMap<Long, Integer> lengths,
                                     int groupSize, long fileSize) throws IOException {
        try (InputStream is = new BufferedInputStream(new FileInputStream(inputPath), BUFFER_SIZE);
             WriteFile writer = new WriteFile(outputPath)) {

            writer.writeBits(groupSize, 3);
            writer.writeBits(fileSize, 32);

            new HuffmanTree().serialize(root, writer);

            byte[] buffer = new byte[BUFFER_SIZE];
            byte[] symbolBuffer = new byte[groupSize];
            int symbolPos = 0;
            int bytesRead;

            while ((bytesRead = is.read(buffer)) != -1) {
                for (int i = 0; i < bytesRead; i++) {
                    symbolBuffer[symbolPos++] = buffer[i];

                    if (symbolPos == groupSize) {
                        long symbol = Utils.bytesToSymbol(symbolBuffer, groupSize);
                        writer.writeBits(codes.get(symbol), lengths.get(symbol));
                        symbolPos = 0;
                    }
                }
            }

            if (symbolPos > 0) {
                long symbol = Utils.bytesToSymbolPadded(symbolBuffer, symbolPos, groupSize);
                writer.writeBits(codes.get(symbol), lengths.get(symbol));
            }
        }
    }

}