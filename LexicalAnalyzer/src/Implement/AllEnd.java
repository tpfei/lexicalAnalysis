package Implement;

/**
 * ��β��
 */
public class AllEnd extends Token {
    public String lexme = "";

    public AllEnd(String s) {
        super(Tag.ALL_END);
        this.lexme = s;
        this.name = "��β��";
    }

    public String toString() {
        return this.lexme;
    }

    public static final AllEnd allEnd = new AllEnd("#");
}
