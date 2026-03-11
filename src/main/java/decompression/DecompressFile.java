package decompression;

import compression.HuffmanNode;
import exception.*;
import io.ReadFile;

import java.io.*;

public class DecompressFile {

    private static final int MIN_FILE_SIZE = 5;
    private static final int GROUP_SIZE_BITS = 3;
    private static final int FILE_SIZE_BITS = 32;
    private static final int BUFFER_SIZE = 8192;

    public void decompress(String inputPath, String outputPath) {
        validateInputPath(inputPath);
        validateOutputPath(outputPath);

        try (ReadFile reader = new ReadFile(inputPath);
             BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(outputPath), BUFFER_SIZE)) {

            validateFileSize(reader);

            int groupSize = reader.readBits(GROUP_SIZE_BITS);
            validateGroupSize(groupSize);

            long originalFileSize = reader.readBitsLong(FILE_SIZE_BITS);

            if (originalFileSize == 0) {
                return;
            }

            HuffmanNode root = decodeHuffmanTree(reader, groupSize);
            if (root == null) {
                throw new CorruptedHuffmanTreeException("Failed to decode Huffman tree - tree is empty");
            }

            decodeData(reader, output, root, groupSize, originalFileSize);

        } catch (IOException e) {
            throw new DecompressionException("IO error during decompression: " + e.getMessage());
        }
    }

    private void validateInputPath(String path) {
        if (path == null || path.trim().isEmpty()) {
            throw new InvalidArgumentException("Input path cannot be null or empty");
        }

        File file = new File(path);

        if (!file.exists()) {
            throw new InvalidCompressedFileException("Input file does not exist: " + path);
        }

        if (!file.isFile()) {
            throw new InvalidCompressedFileException("Input path is not a file: " + path);
        }

        if (!file.canRead()) {
            throw new InvalidCompressedFileException("Cannot read input file: " + path);
        }
    }


    private void validateOutputPath(String path) {
        if (path == null || path.trim().isEmpty()) {
            throw new InvalidArgumentException("Output path cannot be null or empty");
        }

        File file = new File(path);
        File parentDir = file.getParentFile();

        if (parentDir != null && !parentDir.exists()) {
            throw new FileWriteException("Output directory does not exist: " + parentDir.getAbsolutePath());
        }

        if (parentDir != null && !parentDir.canWrite()) {
            throw new FileWriteException("Cannot write to output directory: " + parentDir.getAbsolutePath());
        }

        if (file.exists() && !file.canWrite()) {
            throw new FileWriteException("Cannot overwrite existing file: " + path);
        }
    }

    private void validateFileSize(ReadFile reader) {
        if (reader.getFileSize() < MIN_FILE_SIZE) {
            throw new InvalidCompressedFileException(
                    "File too small to be a valid compressed file. Size: " + reader.getFileSize() +
                            " bytes, minimum required: " + MIN_FILE_SIZE + " bytes"
            );
        }
    }

    private void validateGroupSize(int groupSize) {
        if (groupSize < 1 || groupSize > 4) {
            throw new InvalidCompressedFileException(
                    "Invalid group size value: " + groupSize + ". Must be between 1 and 4"
            );
        }
    }

    private HuffmanNode decodeHuffmanTree(ReadFile reader, int groupSize) throws IOException {
        if (!reader.hasMoreBits()) {
            throw new CorruptedHuffmanTreeException("Unexpected end of file while decoding Huffman tree");
        }

        int bit = reader.readBit();

        if (bit == 1) {
            int symbolBits = groupSize * 8;
            if (reader.getRemainingBits() < symbolBits) {
                throw new CorruptedHuffmanTreeException("Unexpected end of file while reading leaf symbol. " +
                        "Expected " + symbolBits + " bits, remaining: " + reader.getRemainingBits()
                );
            }

            long symbol = reader.readBitsLong(symbolBits);
            return new HuffmanNode(symbol, 0, groupSize);
        } else {
            HuffmanNode left = decodeHuffmanTree(reader, groupSize);
            HuffmanNode right = decodeHuffmanTree(reader, groupSize);
            return new HuffmanNode(0, left, right, groupSize);
        }
    }

    private void decodeData(ReadFile reader, OutputStream output, HuffmanNode root, int groupSize, long originalFileSize)
            throws IOException {

        if (originalFileSize == 0) {
            return;
        }

        byte[] writeBuffer = new byte[groupSize];

        if (root.isLeaf()) {
            decodeSingleSymbolData(output, root, groupSize, originalFileSize, writeBuffer);
            return;
        }

        HuffmanNode current = root;
        long bytesWritten = 0;

        while (bytesWritten < originalFileSize) {
            if (!reader.hasMoreBits()) {
                throw new UnexpectedEndOfFileException(
                        "Unexpected end of file while decoding data. " +
                                "Bytes written: " + bytesWritten + " of " + originalFileSize
                );
            }

            int bit = reader.readBit();

            if (bit == 0) {
                current = current.getLeft();
            } else {
                current = current.getRight();
            }

            if (current == null) {
                throw new CorruptedHuffmanTreeException(
                        "Invalid code path in Huffman tree"
                );
            }

            if (current.isLeaf()) {
                int bytesToWrite = (int) Math.min(groupSize, originalFileSize - bytesWritten);
                writeSymbol(output, current.getSymbol(), groupSize, bytesToWrite, writeBuffer);
                bytesWritten += bytesToWrite;
                current = root;
            }
        }
    }

    private void decodeSingleSymbolData(OutputStream output, HuffmanNode leaf, int groupSize,
                                        long originalFileSize, byte[] writeBuffer) throws IOException {
        long bytesWritten = 0;
        while (bytesWritten < originalFileSize) {
            int bytesToWrite = (int) Math.min(groupSize, originalFileSize - bytesWritten);
            writeSymbol(output, leaf.getSymbol(), groupSize, bytesToWrite, writeBuffer);
            bytesWritten += bytesToWrite;
        }
    }

    private void writeSymbol(OutputStream output, long symbol, int groupSize, int bytesToWrite, byte[] buffer)
            throws IOException {
        for (int i = groupSize - 1; i >= 0; i--) {
            buffer[i] = (byte) (symbol & 0xFF);
            symbol >>= 8;
        }
        output.write(buffer, 0, bytesToWrite);
    }

    public boolean isValidCompressedFile(String path) {
        try {
            validateInputPath(path);

            try (ReadFile reader = new ReadFile(path)) {
                if (reader.getFileSize() < MIN_FILE_SIZE) {
                    return false;
                }

                int groupSize = reader.readBits(GROUP_SIZE_BITS);
                if (groupSize < 1 || groupSize > 4) {
                    return false;
                }

                reader.readBits(FILE_SIZE_BITS);

                HuffmanNode root = decodeHuffmanTree(reader, groupSize);
                return root != null;

            }
        } catch (Exception e) {
            return false;
        }
    }
}