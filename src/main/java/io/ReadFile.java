package io;

import exception.FileReadException;
import exception.InvalidArgumentException;
import exception.PathNotExistException;
import exception.UnexpectedEndOfFileException;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ReadFile implements AutoCloseable {
    private final BufferedInputStream inputStream;
    private final String filePath;

    private int currentByte;
    private int bitsRemaining;
    private long totalBitsRead;
    private final long fileSize;
    private boolean endOfFile;

    public ReadFile(String path) throws IOException {
        this.filePath = path;

        java.io.File file = new java.io.File(path);
        if (!file.exists()) {
            throw new PathNotExistException();
        }
        if (!file.isFile()) {
            throw new FileReadException("Path is not a file: " + path, null);
        }
        if (!file.canRead()) {
            throw new FileReadException("Cannot read file: " + path, null);
        }

        this.fileSize = file.length();
        this.inputStream = new BufferedInputStream(new FileInputStream(path), 65536);
        this.currentByte = 0;
        this.bitsRemaining = 0;
        this.totalBitsRead = 0;
        this.endOfFile = false;
    }

    public InputStream openStream() throws FileReadException {
        try {
            return new BufferedInputStream(new FileInputStream(filePath), 65536);
        } catch (IOException e) {
            throw new FileReadException("Error opening file: " + filePath, e);
        }
    }

    public int readBit() throws IOException {
        if (bitsRemaining == 0) {
            if (!loadNextByte()) {
                throw new UnexpectedEndOfFileException(
                        "Unexpected end of file while reading bits at position " + totalBitsRead
                );
            }
        }

        bitsRemaining--;
        totalBitsRead++;
        return (currentByte >> bitsRemaining) & 1;
    }

    public int readBits(int numBits) throws IOException {
        if (numBits < 0 || numBits > 32) {
            throw new InvalidArgumentException("Number of bits must be between 0 and 32, got: " + numBits);
        }
        return (int) readBitsLong(numBits);
    }

    public long readBitsLong(int numBits) throws IOException {
        if (numBits < 0 || numBits > 64) {
            throw new InvalidArgumentException("Number of bits must be between 0 and 64, got: " + numBits);
        }
        if (numBits == 0) return 0L;

        long result = 0L;
        int remaining = numBits;

        if (bitsRemaining > 0) {
            int take = Math.min(bitsRemaining, remaining);
            int mask = (1 << take) - 1;
            result = (currentByte >> (bitsRemaining - take)) & mask;
            bitsRemaining -= take;
            totalBitsRead += take;
            remaining -= take;
        }

        while (remaining >= 8) {
            if (!loadNextByte()) {
                throw new UnexpectedEndOfFileException(
                        "Unexpected end of file while reading bits at position " + totalBitsRead
                );
            }
            result = (result << 8) | currentByte;
            bitsRemaining = 0;
            totalBitsRead += 8;
            remaining -= 8;
        }

        if (remaining > 0) {
            if (!loadNextByte()) {
                throw new UnexpectedEndOfFileException(
                        "Unexpected end of file while reading bits at position " + totalBitsRead
                );
            }
            int mask = (1 << remaining) - 1;
            result = (result << remaining) | ((currentByte >> (8 - remaining)) & mask);
            bitsRemaining = 8 - remaining;
            totalBitsRead += remaining;
        }

        return result;
    }

    public byte readByte() throws IOException {
        return (byte) readBits(8);
    }

    public boolean hasMoreBits() throws IOException {
        if (bitsRemaining > 0) {
            return true;
        }

        if (endOfFile) {
            return false;
        }

        return loadNextByte();
    }

    public long getTotalBitsRead() {
        return totalBitsRead;
    }

    public long getFileSize() {
        return fileSize;
    }

    public long getTotalBitsInFile() {
        return fileSize * 8;
    }

    public long getRemainingBits() {
        return getTotalBitsInFile() - totalBitsRead;
    }

    private boolean loadNextByte() throws IOException {
        int nextByte = inputStream.read();
        if (nextByte == -1) {
            endOfFile = true;
            return false;
        }

        currentByte = nextByte;
        bitsRemaining = 8;
        return true;
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }
}