package Implement;

/**
 * ��β��
 */
public class LineEnd extends Token {
    public String lexme = "";

    public LineEnd(String s) {
        super(Tag.LINE_END);
        this.lexme = s;
        this.name = "��β��";
    }

    public String toString() {
        return this.lexme;
    }

    public static final LineEnd lineEnd = new LineEnd("\n");
}

