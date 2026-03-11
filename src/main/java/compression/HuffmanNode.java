package compression;

public class HuffmanNode implements Comparable<HuffmanNode> {
    private final long symbol;
    private final long frequency;
    private final HuffmanNode left;
    private final HuffmanNode right;
    private final int groupSize;

    public HuffmanNode(long symbol, long frequency, int groupSize) {
        this.symbol = symbol;
        this.frequency = frequency;
        this.groupSize = groupSize;
        this.left = null;
        this.right = null;
    }

    public HuffmanNode(long frequency, HuffmanNode left, HuffmanNode right, int groupSize) {
        this.symbol = 0;
        this.frequency = frequency;
        this.groupSize = groupSize;
        this.left = left;
        this.right = right;
    }

    public boolean isLeaf() {
        return left == null && right == null;
    }

    @Override
    public int compareTo(HuffmanNode other) {
        return Long.compare(this.frequency, other.frequency);
    }

    public long getSymbol() { return symbol; }
    public long getFrequency() { return frequency; }
    public HuffmanNode getLeft() { return left; }
    public HuffmanNode getRight() { return right; }
    public int getGroupSize() { return groupSize; }
}