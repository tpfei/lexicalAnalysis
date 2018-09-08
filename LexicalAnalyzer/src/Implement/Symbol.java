package Implement;

public class Symbol extends Token {
    public String lexme = "";

    public Symbol(String s) {
        super(Tag.SYMBOL);
        if(s.length()>8) {
        	this.lexme = s.substring(0,8);
        }else {
            this.lexme = s;
        }
        this.name = "±êÊ¶·û";
    }

    public String toString() {
        return this.lexme;
    }
}
