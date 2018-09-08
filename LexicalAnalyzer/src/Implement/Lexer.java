package Implement;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sound.midi.SysexMessage;

/*
 * �ʷ����������
 */
public class Lexer {
    /*��¼�к�*/
    public static int line = 1;
    /*������¶�����ַ�*/
    char character = ' ';

    /*������*/
    Hashtable<String, KeyWord> keywords = new Hashtable<String, KeyWord>();
    /*token����*/
    private ArrayList<Token> tokens = new ArrayList<Token>();
    /*���ű�*/
    private ArrayList<Symbol> symtable = new ArrayList<Symbol>();
    /*������Ϣ*/
    private ArrayList<String> error = new ArrayList<String>();

    /*��ȡ�ļ�����*/
    BufferedReader reader = null;
    /*���浱ǰ�Ƿ��ȡ�����ļ��Ľ�β*/
    private Boolean isEnd = false;
    
    //������Ϣ�ļ�
    FileWriter writer = null;

    /* �Ƿ��ȡ���ļ��Ľ�β */
    public Boolean getReaderState() {
        return this.isEnd;
    }

    /*��ӡtokens����*/
    public void printToken() throws IOException {
        FileWriter writer = new FileWriter("G:\\lex.txt");
        System.out.println("�ʷ�����������£�");
        while (getReaderState() == false) {
            Token tok = scan();
            if(tok==null) {
            	break;
            }
            String str = "line " + tok.line + "\t(" + tok.tag + "," + tok.pos + ")\t\t"
                    + tok.name + ": " + tok.toString() + "\r\n";
            writer.write(str);
            System.out.print(str);
        }
        writer.flush();

    }

    /*��ӡ��ʶ��*/
    public void printSymbolsTable() throws IOException {
        FileWriter writer = new FileWriter("G:\\symtab1.txt");
        System.out.print("\r\n\r\n��ʶ��\r\n");
        System.out.print("���\t�к�\t����\r\n");
        writer.write("��ʶ��\r\n");
        writer.write("��� " + "\t�к� " + "\t���� \r\n");
        Iterator<Symbol> e = symtable.iterator();
        while (e.hasNext()) {
            Symbol symbol = e.next();
            String desc = symbol.pos + "\t" + symbol.line + "\t" + symbol.toString();
            System.out.print(desc + "\r\n");
            writer.write(desc + "\r\n");
        }

        writer.flush();
    }

    /*��ӡ����*/
    public void printError(Token tok) throws IOException {
        writer = new FileWriter("G:\\error.txt");
        System.out.print("\r\n\r\n����ʷ����£�\r\n");
        String str = "line " + tok.line + "\t(" + tok.tag + "," + tok.pos + ")\t\t"
                + tok.name + ": " + tok.toString() + "\r\n";
        System.out.println(str);
        writer.write("����ʷ����£�\r\n");
        writer.write(str);
        writer.write("\n");
        writer.flush();
    }

    /*��ӱ�����*/
    void reserve(KeyWord w) {
        keywords.put(w.lexme, w);
    }

    public Lexer() {
        /*��ʼ����ȡ�ļ�����*/
        try {
            reader = new BufferedReader(new FileReader("G:\\����.txt"));
        } catch (IOException e) {
            System.out.print(e);
        }

        /*��ӱ�����*/
        this.reserve(KeyWord.begin);
        this.reserve(KeyWord.end);
        this.reserve(KeyWord.integer);
        this.reserve(KeyWord.function);
        this.reserve(KeyWord.read);
        this.reserve(KeyWord.write);
        this.reserve(KeyWord.aIf);
        this.reserve(KeyWord.aThen);
        this.reserve(KeyWord.aElse);
    }

    /*���ַ���*/
    public void readch() throws IOException {
    	
        character = (char) reader.read();
        if ((int) character == 0xffff) {
            this.isEnd = true;
        }
    }

    /*�ж��Ƿ�ƥ��*/
    public Boolean readch(char ch) throws IOException {
    	readch();
        if (this.character != ch ) {
            return false;
        }
        this.character = ' ';
        return true;
    }

    /*���ֵ�ʶ��*/
    public Boolean isDigit() throws IOException {
        if (Character.isDigit(character)) {
            int value = 0;
            while (Character.isDigit(character)) {
                value = 10 * value + Character.digit(character, 10);
                readch();
            }

            Num n = new Num(value);
            n.line = line;
            tokens.add(n);
            if(n.value>65535) {
              FileWriter writer = new FileWriter("G:\\numberError.txt");
              String str = "���ֳ������ֵ";
              error.add(str);
              System.out.println(str +": " + n.value);
              writer.write("����ʷ����£�\r\n");
              writer.write(str);
              writer.flush();
            }
            return true;
        } 	
        else
        	return false;
    }
    
    public void isOutOfMaxValue() throws IOException {
//        int value = 0;
//        while (Character.isDigit(character)) {
//            value = 10 * value + Character.digit(character, 10);
//            System.out.println(value);
//            readch();
//        }
//        Num n = new Num(value);
//        if(n.value > 65535) {
////		    n.line = line;
////		    tokens.add(n);
//            FileWriter writer = new FileWriter("G:\\error.txt");
//            System.out.print("\r\n\r\n����ʷ����£�\r\n");
////            String str = "line " + tok.line + "\t(" + tok.tag + "," + tok.pos + ")\t\t"
////                    + tok.name + ": " + tok.toString() + "\r\n";
//            
//            String str = "���ֳ������ֵ";
//            System.out.println(str);
//            writer.write("����ʷ����£�\r\n");
//            writer.write(str);
//            writer.flush();
//            return;
//            
////		    n.line = line;
////		    tokens.add(n);
////			printError(tok);
//		}
        return;
        
    }    	


    /*�����֡���ʶ����ʶ��*/
    public Boolean isLetter() throws IOException {
        if (Character.isLetter(character)) {
            StringBuffer sb = new StringBuffer();

            /*���ȵõ�������һ���ָ�*/            
            while (Character.isLetterOrDigit(character)) {
                sb.append(character);
                readch();
            }

            /*�ж��Ǳ����ֻ��Ǳ�ʶ��*/
            String s = sb.toString();
            KeyWord w = keywords.get(s);

            /*����Ǳ����ֵĻ���w��Ӧ���ǿյ�*/
            if (w != null) {
                w.line = line;
                tokens.add(w);
            } else {
                /*������Ǳ�ʶ�����˴������¼��ʶ����ŵ����*/
                Symbol sy = new Symbol(s);
                String str = sy.toString();                             
                Symbol mark = sy;           //���ڱ���Ѵ��ڱ�ʶ��       
                Boolean isRepeat = false;
                sy.line = line;
                for (Symbol i : symtable) {
                    if (str.equals(i.toString())) {
                        mark = i;
                        isRepeat = true;
                    }
                }
                if (!isRepeat) {
                    sy.pos = symtable.size() + 1;
                    symtable.add(sy);
                } else if (isRepeat) {
                    sy.pos = mark.pos;
                }
                tokens.add(sy);
            }
            return true;
        } 
        else if(Character.isDigit(character)) {
            StringBuffer stB = new StringBuffer();
            stB.append(character);
        	readch();
        	
        	isDigit();
        	return true;
        }
        else
            return false;
    }

    /*���ŵ�ʶ��*/
    public Boolean isSign() throws IOException {
    	
        switch (character) {
            case '#':
                readch();
                AllEnd.allEnd.line = line;
                tokens.add(AllEnd.allEnd);
                return true;                                
            case '\r':
                if (readch('\n')) {
                    readch();
                    LineEnd.lineEnd.line = line;
                    tokens.add(LineEnd.lineEnd);
                    line++;
                    return true;
                }
            case '~':
                readch();
                while(character != '\n') {
                	readch();
                }
                readch();
                LineEnd.lineEnd.line = line;
                tokens.add(LineEnd.lineEnd);
                line++;
                return true;
            case '(':
                readch();
                Delimiter.lpar.line = line;
                tokens.add(Delimiter.lpar);
                return true;
            case ')':
                readch();
                Delimiter.rpar.line = line;
                tokens.add(Delimiter.rpar);
                return true;
            case ';':
                readch();
                Delimiter.sem.line = line;
                tokens.add(Delimiter.sem);
                return true;               
            case '+':
                readch();
                CalcWord.add.line = line;
                tokens.add(CalcWord.add);
                return true;
            case '-':
                readch();
                CalcWord.sub.line = line;
                tokens.add(CalcWord.sub);
                return true;
            case '*':
                readch();
                CalcWord.mul.line = line;
                tokens.add(CalcWord.mul);
                return true;
            case '/':
                readch();
                character = ' ';
                CalcWord.div.line = line;
                tokens.add(CalcWord.div);
                return true;
            case ':':
                if (readch('=')) {
                    readch();
                    CalcWord.assign.line = line;
                    tokens.add(CalcWord.assign);
                    return true;
                }
                break;
            case '>':
                if (readch('=')) {
                    readch();
                    CalcWord.ge.line = line;
                    tokens.add(CalcWord.ge);
                    return true;
                }
                break;
            case '<':
                if (readch('=')) {
                    readch();
                    CalcWord.le.line = line;
                    tokens.add(CalcWord.le);
                    return true;
                }
                break;
            case '!':
                if (readch('=')) {
                    readch();
                    CalcWord.ne.line = line;
                    tokens.add(CalcWord.ne);
                    return true;
                }
                break;
        }
        return false;
    }
    
    public Boolean isSign(char ch) throws IOException {
    	
//        switch (character) {
//        case '/':
            CalcWord.div.line = line;
            tokens.add(CalcWord.div);
            return true;

    }
    


    /*���濪ʼ�ָ�ؼ��֣���ʶ������Ϣ*/
    public Token scan() throws IOException {
        Token tok = null; 
        while (character == ' ')
        readch(); 
    	/*ȥ��ע�͵�����*/
        if(character == '/') {
	        readch();
	        if(character=='/') {
	        	character = '~';
	        }
	        // ����/* 
	        else if(character == '*') 
	        {
	        	readch();
	        	while(character != '*') 
	        	{
	        		readch();
	        		//     ����* 
	        		if(character == '*') 
	        		{	   
	        			readch();	        			
	        			if(character == '/') 
	        			{
	        				readch('\n');
	        				break;
	        			}
	        			else {
	        				String str = "�﷨����,ע�Ͳ�����.....";
	        				System.out.println(str);
	        		        writer = new FileWriter("G:\\annotationError.txt");
	        		        writer.write("����ʷ����£�\r\n");
	        		        writer.write(str);
	        		        writer.write("\n");
	        		        writer.flush();
        		        	break;
	        			}
	        		}
    				else if(character == '#') {
        				String str = "�﷨����,ע�Ͳ�����.....";
        				System.out.println(str);
        	            error.add(str);
        		        writer = new FileWriter("G:\\annotationError.txt");
        		        writer.write("����ʷ����£�\r\n");
        		        writer.write(str);
        		        writer.write("\n");
        		        writer.flush();        		        
//        		        Iterator<String> list = error.iterator();
//        		        while(list.hasNext()) {
//        		        	System.out.println(list.next());
//        		        }
    		        	break;
    				}
	        	}
	        }
	        /* ���������ַ� */
	        else {
	        	character = '/';	
	        }
        }      
    	if (isDigit() || isLetter() || isSign()) {
    		int index = tokens.size() - 1;
	    	tok = tokens.get(index);
        }
		return tok;
    }
}

