package Implement;

public class Num extends Token {
    public final int value;

    public Num(int v) {
        super(Tag.CONSTANT);
        this.value = v;
        this.name = "����";
    }

    public String toString() {
        return "" + value;
    }
}

 