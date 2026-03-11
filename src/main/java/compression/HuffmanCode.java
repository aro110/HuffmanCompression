package compression;

public class HuffmanCode {
    private final long bits;
    private final int length;

    public HuffmanCode(String code) {
        this.length = code.length();
        this.bits = code.isEmpty() ? 0 : Long.parseLong(code, 2);
    }

    public long getBits() {
        return bits;
    }

    public int getLength() {
        return length;
    }
}