package Implement;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sound.midi.SysexMessage;

/*
 * 词法分析并输出
 */
public class Lexer {
    /*记录行号*/
    public static int line = 1;
    /*存放最新读入的字符*/
    char character = ' ';

    /*保留字*/
    Hashtable<String, KeyWord> keywords = new Hashtable<String, KeyWord>();
    /*token序列*/
    private ArrayList<Token> tokens = new ArrayList<Token>();
    /*符号表*/
    private ArrayList<Symbol> symtable = new ArrayList<Symbol>();
    /*错误信息*/
    private ArrayList<String> error = new ArrayList<String>();

    /*读取文件变量*/
    BufferedReader reader = null;
    /*保存当前是否读取到了文件的结尾*/
    private Boolean isEnd = false;
    
    //错误信息文件
    FileWriter writer = null;

    /* 是否读取到文件的结尾 */
    public Boolean getReaderState() {
        return this.isEnd;
    }

    /*打印tokens序列*/
    public void printToken() throws IOException {
        FileWriter writer = new FileWriter("G:\\lex.txt");
        System.out.println("词法分析结果如下：");
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

    /*打印标识符*/
    public void printSymbolsTable() throws IOException {
        FileWriter writer = new FileWriter("G:\\symtab1.txt");
        System.out.print("\r\n\r\n标识符\r\n");
        System.out.print("编号\t行号\t名称\r\n");
        writer.write("标识符\r\n");
        writer.write("编号 " + "\t行号 " + "\t名称 \r\n");
        Iterator<Symbol> e = symtable.iterator();
        while (e.hasNext()) {
            Symbol symbol = e.next();
            String desc = symbol.pos + "\t" + symbol.line + "\t" + symbol.toString();
            System.out.print(desc + "\r\n");
            writer.write(desc + "\r\n");
        }

        writer.flush();
    }

    /*打印错误*/
    public void printError(Token tok) throws IOException {
        writer = new FileWriter("G:\\error.txt");
        System.out.print("\r\n\r\n错误词法如下：\r\n");
        String str = "line " + tok.line + "\t(" + tok.tag + "," + tok.pos + ")\t\t"
                + tok.name + ": " + tok.toString() + "\r\n";
        System.out.println(str);
        writer.write("错误词法如下：\r\n");
        writer.write(str);
        writer.write("\n");
        writer.flush();
    }

    /*添加保留字*/
    void reserve(KeyWord w) {
        keywords.put(w.lexme, w);
    }

    public Lexer() {
        /*初始化读取文件变量*/
        try {
            reader = new BufferedReader(new FileReader("G:\\输入.txt"));
        } catch (IOException e) {
            System.out.print(e);
        }

        /*添加保留字*/
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

    /*按字符读*/
    public void readch() throws IOException {
    	
        character = (char) reader.read();
        if ((int) character == 0xffff) {
            this.isEnd = true;
        }
    }

    /*判断是否匹配*/
    public Boolean readch(char ch) throws IOException {
    	readch();
        if (this.character != ch ) {
            return false;
        }
        this.character = ' ';
        return true;
    }

    /*数字的识别*/
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
              String str = "数字超出最大值";
              error.add(str);
              System.out.println(str +": " + n.value);
              writer.write("错误词法如下：\r\n");
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
//            System.out.print("\r\n\r\n错误词法如下：\r\n");
////            String str = "line " + tok.line + "\t(" + tok.tag + "," + tok.pos + ")\t\t"
////                    + tok.name + ": " + tok.toString() + "\r\n";
//            
//            String str = "数字超出最大值";
//            System.out.println(str);
//            writer.write("错误词法如下：\r\n");
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


    /*保留字、标识符的识别*/
    public Boolean isLetter() throws IOException {
        if (Character.isLetter(character)) {
            StringBuffer sb = new StringBuffer();

            /*首先得到整个的一个分割*/            
            while (Character.isLetterOrDigit(character)) {
                sb.append(character);
                readch();
            }

            /*判断是保留字还是标识符*/
            String s = sb.toString();
            KeyWord w = keywords.get(s);

            /*如果是保留字的话，w不应该是空的*/
            if (w != null) {
                w.line = line;
                tokens.add(w);
            } else {
                /*否则就是标识符，此处多出记录标识符编号的语句*/
                Symbol sy = new Symbol(s);
                String str = sy.toString();                             
                Symbol mark = sy;           //用于标记已存在标识符       
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

    /*符号的识别*/
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
    


    /*下面开始分割关键字，标识符等信息*/
    public Token scan() throws IOException {
        Token tok = null; 
        while (character == ' ')
        readch(); 
    	/*去掉注释的内容*/
        if(character == '/') {
	        readch();
	        if(character=='/') {
	        	character = '~';
	        }
	        // 读到/* 
	        else if(character == '*') 
	        {
	        	readch();
	        	while(character != '*') 
	        	{
	        		readch();
	        		//     读到* 
	        		if(character == '*') 
	        		{	   
	        			readch();	        			
	        			if(character == '/') 
	        			{
	        				readch('\n');
	        				break;
	        			}
	        			else {
	        				String str = "语法错误,注释不完整.....";
	        				System.out.println(str);
	        		        writer = new FileWriter("G:\\annotationError.txt");
	        		        writer.write("错误词法如下：\r\n");
	        		        writer.write(str);
	        		        writer.write("\n");
	        		        writer.flush();
        		        	break;
	        			}
	        		}
    				else if(character == '#') {
        				String str = "语法错误,注释不完整.....";
        				System.out.println(str);
        	            error.add(str);
        		        writer = new FileWriter("G:\\annotationError.txt");
        		        writer.write("错误词法如下：\r\n");
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
	        /* 读到其它字符 */
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

