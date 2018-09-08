package Implement;

public class Delimiter extends Token {
    public String lexme = "";

    public Delimiter(String s, int t) {
        super(t);
        this.lexme = s;
        this.name = "½ç·û";
    }

    public String toString() {
        return this.lexme;
    }

    public static final Delimiter
            lpar = new Delimiter("(", Tag.LPAR),
            rpar = new Delimiter(")", Tag.RPAR),
            sem = new Delimiter(";", Tag.SEM);
}

