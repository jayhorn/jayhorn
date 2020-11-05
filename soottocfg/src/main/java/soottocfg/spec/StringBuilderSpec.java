package soottocfg.spec;

public class StringBuilderSpec {

    String s = "";

    public StringBuilderSpec() {
    }

    public StringBuilderSpec(CharSequence seq) {
        (new java.util.Random()).nextInt(42);   // over-approximate
    }

    public StringBuilderSpec(int capacity) {
        (new java.util.Random()).nextInt(42);   // over-approximate
    }

    public StringBuilderSpec(String str) {
        this.s = str;
    }

    public StringBuilderSpec append(boolean b) {
        this.s = this.s.concat(String.valueOf(b));
        return this;
    }

    public StringBuilderSpec append(char c) {
        this.s = this.s.concat(String.valueOf(c));
        return this;
    }

    public StringBuilderSpec append(int i) {
        this.s = this.s.concat(String.valueOf(i));
        return this;
    }

    public StringBuilderSpec append(long lng) {
        this.s = this.s.concat(String.valueOf(lng));
        return this;
    }

    public StringBuilderSpec append(float f) {
        this.s = this.s.concat(String.valueOf(f));
        return this;
    }

    public StringBuilderSpec append(double d) {
        this.s = this.s.concat(String.valueOf(d));
        return this;
    }

    public StringBuilderSpec append(Object obj) {
        this.s = this.s.concat(obj.toString());
        return this;
    }

    public StringBuilderSpec append(String s) {
        this.s = this.s.concat(s);
        return this;
    }

    public StringBuilderSpec append(StringBuilderSpec sb) {
        this.s = this.s.concat(sb.toString());
        return this;
    }

    public StringBuilderSpec appendCodePoint(int codePoint) {
        (new java.util.Random()).nextInt(42);   // over-approximate
        return this;
    }

    public StringBuilderSpec delete(int start, int end) {
        (new java.util.Random()).nextInt(42);   // over-approximate
        return this;
    }

    public StringBuilderSpec deleteCharAt(int index) {
        (new java.util.Random()).nextInt(42);   // over-approximate
        return this;
    }

    public StringBuilderSpec replace(int start, int end, String str) {
        (new java.util.Random()).nextInt(42);   // over-approximate
        return this;
    }

    public StringBuilderSpec insert(int index,
                                    char[] str,
                                    int offset,
                                    int len) {
        (new java.util.Random()).nextInt(42);   // over-approximate
        return this;
    }

    public StringBuilderSpec insert(int offset,
                                    Object obj) {
        (new java.util.Random()).nextInt(42);   // over-approximate
        return this;
    }

    public StringBuilderSpec insert(int offset,
                                    String str) {
        (new java.util.Random()).nextInt(42);   // over-approximate
        return this;
    }

    public StringBuilderSpec insert(int offset,
                                    char[] str) {
        (new java.util.Random()).nextInt(42);   // over-approximate
        return this;
    }

    public StringBuilderSpec insert(int dstOffset,
                                    CharSequence s) {
        (new java.util.Random()).nextInt(42);   // over-approximate
        return this;
    }

    public StringBuilderSpec insert(int dstOffset,
                                    CharSequence s,
                                    int start,
                                    int end) {
        (new java.util.Random()).nextInt(42);   // over-approximate
        return this;
    }

    public StringBuilderSpec insert(int offset,
                                    boolean b) {
        (new java.util.Random()).nextInt(42);   // over-approximate
        return this;
    }

    public StringBuilderSpec insert(int offset,
                                    char c) {
        (new java.util.Random()).nextInt(42);   // over-approximate
        return this;
    }

    public StringBuilderSpec insert(int offset,
                                    int i) {
        (new java.util.Random()).nextInt(42);   // over-approximate
        return this;
    }

    public StringBuilderSpec insert(int offset,
                                    long l) {
        (new java.util.Random()).nextInt(42);   // over-approximate
        return this;
    }

    public StringBuilderSpec insert(int offset,
                                    float f) {
        (new java.util.Random()).nextInt(42);   // over-approximate
        return this;
    }

    public StringBuilderSpec insert(int offset,
                                    double d) {
        (new java.util.Random()).nextInt(42);   // over-approximate
        return this;
    }

    public int indexOf(String str) {
        return (new java.util.Random()).nextInt(42);   // over-approximate
    }

    public int indexOf(String str,
                       int fromIndex) {
        return (new java.util.Random()).nextInt(42);   // over-approximate
    }

    public int lastIndexOf(String str) {
        return (new java.util.Random()).nextInt(42);   // over-approximate
    }

    public int lastIndexOf(String str,
                           int fromIndex) {
        return (new java.util.Random()).nextInt(42);   // over-approximate
    }

    public StringBuilderSpec reverse() {
        (new java.util.Random()).nextInt(42);   // over-approximate
        return this;
    }

    public String toString() {
        return this.s;
    }

    public int length() {
        return this.s.length();
    }

    public int capacity() {
        return (new java.util.Random()).nextInt(42);   // over-approximate
    }

    public void ensureCapacity(int minimumCapacity) {
        (new java.util.Random()).nextInt(42);   // over-approximate
    }

    public void trimToSize() {
        (new java.util.Random()).nextInt(42);   // over-approximate
    }

    public void setLength(int newLength) {
        (new java.util.Random()).nextInt(42);   // over-approximate
    }

    public char charAt(int index) {
        return this.s.charAt(index);
    }

    public int codePointAt(int index) {
        return this.s.codePointAt(index);
    }

    public int codePointBefore(int index) {
        return this.s.codePointBefore(index);
    }

    public int codePointCount(int beginIndex,
                              int endIndex) {
        return this.s.codePointCount(beginIndex, endIndex);
    }

    public int offsetByCodePoints(int index,
                                  int codePointOffset) {
        return this.s.offsetByCodePoints(index, codePointOffset);
    }

    public void getChars(int srcBegin,
                         int srcEnd,
                         char[] dst,
                         int dstBegin) {
        (new java.util.Random()).nextInt(42);   // over-approximate
    }

    public void setCharAt(int index,
                          char ch) {
        (new java.util.Random()).nextInt(42);   // over-approximate
    }

    public String substring(int start) {
        return this.s.substring(start);
    }

    public CharSequence subSequence(int start,
                                    int end) {
        (new java.util.Random()).nextInt(42);   // over-approximate
        throw new RuntimeException("not specified");
    }

    public String substring(int start,
                            int end) {
        return this.s.substring(start, end);
    }

}
