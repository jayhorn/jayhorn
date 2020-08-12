package soottocfg.spec;

public class StringBuilderSpec {    // TODO: should extend StringBuilder or AbstractStringBuilder ?

    String s = "";

    public StringBuilderSpec() {
    }

    public StringBuilderSpec(CharSequence seq) {
        throw new RuntimeException("not specified");
    }

    public StringBuilderSpec(int capacity) {
    }

    public StringBuilderSpec(String str) {
        this.s = str;
    }

    public StringBuilderSpec append(boolean b) {
        this.s = this.s.concat(Boolean.toString(b));
        return this;
    }

    public StringBuilderSpec append(char c) {
        this.s = this.s.concat(Character.toString(c));
        return this;
    }

    public StringBuilderSpec append(int i) {
        this.s = this.s.concat(Integer.toString(i));
        return this;
    }

    public StringBuilderSpec append(long lng) {
        this.s = this.s.concat(Long.toString(lng));
        return this;
    }

    public StringBuilderSpec append(float f) {
        this.s = this.s.concat(Float.toString(f));
        return this;
    }

    public StringBuilderSpec append(double d) {
        this.s = this.s.concat(Double.toString(d));
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
        throw new RuntimeException("not specified");
    }

    public StringBuilderSpec delete(int start, int end) {
        throw new RuntimeException("not specified");
    }

    public StringBuilderSpec deleteCharAt(int index) {
        throw new RuntimeException("not specified");
    }

    public StringBuilderSpec replace(int start, int end, String str) {
        throw new RuntimeException("not specified");
    }

    public StringBuilderSpec insert(int index,
                                char[] str,
                                int offset,
                                int len) {
        throw new RuntimeException("not specified");
    }

    public StringBuilderSpec insert(int offset,
                                Object obj) {
        throw new RuntimeException("not specified");
    }

    public StringBuilderSpec insert(int offset,
                                String str) {
        throw new RuntimeException("not specified");
    }

    public StringBuilderSpec insert(int offset,
                                char[] str) {
        throw new RuntimeException("not specified");
    }

    public StringBuilderSpec insert(int dstOffset,
                                CharSequence s) {
        throw new RuntimeException("not specified");
    }

    public StringBuilderSpec insert(int dstOffset,
                                CharSequence s,
                                int start,
                                int end) {
        throw new RuntimeException("not specified");
    }

    public StringBuilderSpec insert(int offset,
                                boolean b) {
        throw new RuntimeException("not specified");
    }

    public StringBuilderSpec insert(int offset,
                                char c) {
        throw new RuntimeException("not specified");
    }

    public StringBuilderSpec insert(int offset,
                                int i) {
        throw new RuntimeException("not specified");
    }

    public StringBuilderSpec insert(int offset,
                                long l) {
        throw new RuntimeException("not specified");
    }

    public StringBuilderSpec insert(int offset,
                                float f) {
        throw new RuntimeException("not specified");
    }

    public StringBuilderSpec insert(int offset,
                                double d) {
        throw new RuntimeException("not specified");
    }

    public int indexOf(String str) {
        throw new RuntimeException("not specified");
    }

    public int indexOf(String str,
                       int fromIndex) {
        throw new RuntimeException("not specified");
    }

    public int lastIndexOf(String str) {
        throw new RuntimeException("not specified");
    }

    public int lastIndexOf(String str,
                           int fromIndex) {
        throw new RuntimeException("not specified");
    }

    public StringBuilderSpec reverse() {
        throw new RuntimeException("not specified");
    }

    public String toString() {
        return this.s;
    }

    public int length() {
        return this.s.length();
    }

    public int capacity() {
        throw new RuntimeException("not specified");
    }

    public void ensureCapacity(int minimumCapacity) {
        throw new RuntimeException("not specified");
    }

    public void trimToSize() {
        throw new RuntimeException("not specified");
    }

    public void setLength(int newLength) {
        throw new RuntimeException("not specified");
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
        throw new RuntimeException("not specified");
    }

    public void setCharAt(int index,
                          char ch) {
        throw new RuntimeException("not specified");
    }

    public String substring(int start) {
        return this.s.substring(start);
    }

    public CharSequence subSequence(int start,
                                    int end) {
        throw new RuntimeException("not specified");
    }

    public String substring(int start,
                            int end) {
        return this.s.substring(start, end);
    }

}
