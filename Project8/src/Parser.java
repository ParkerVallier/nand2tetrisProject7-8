import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Parser {
	
    private BufferedReader reader;	// Reader and lines
	private String currentLine;		// ^^
	private String nextLine;		// ^^
    
    public static final int ARITHMETIC = 0;	// Type
    public static final int PUSH = 1;		// ^^
    public static final int POP = 2;		// ^^
    public static final int LABEL = 3;		// ^^
    public static final int GOTO = 4;		// ^^
    public static final int IF = 5;			// ^^
    public static final int FUNCTION = 6;	// ^^
    public static final int RETURN = 7;		// ^^
    public static final int CALL = 8;		// ^^
    
    public static final ArrayList<String> aCmds = new ArrayList<String>();
    private int aType;
    private String arg1;
    private int arg2;

    static {

        aCmds.add("add");	// Arithmetic Commands
        aCmds.add("sub");	// ^^
        aCmds.add("neg");	// ^^
        aCmds.add("eq");	// ^^
        aCmds.add("gt");	// ^^
        aCmds.add("lt");	// ^^
        aCmds.add("and");	// ^^
        aCmds.add("or");	// ^^
        aCmds.add("not");	// ^^

    }

    public Parser(File source) throws IOException {		// Alot of initialization

        aType = -1;
        arg1 = "";
        arg2 = -1;
       
        this.reader = new BufferedReader(new FileReader(source));
        this.currentLine = null;										
        this.nextLine = this.getNextLine();
        
    }
    private String getNextLine() throws IOException {
		String nextLine;	
			
		nextLine = this.reader.readLine();	// next line in the reader
			
		if (nextLine == null) {		// Line is empty
			return null;
		}
		
		return noComments(nextLine);	// Return the next line
	}

    public boolean hasNext(){	// Has next??
    	return (this.nextLine != null);
    }


    public void advance() throws IOException{
    	
        currentLine = nextLine;
        nextLine = this.getNextLine();
        while (noSpaces(currentLine) == "") {
            currentLine = nextLine;
            nextLine = this.getNextLine();
        }
        arg1 = "";		// Initialize arg1
        arg2 = -1;		// Initialize arg2

        String[] parts = currentLine.split(" ");

        if (aCmds.contains(parts[0])){

            aType = ARITHMETIC;
            arg1 = parts[0];

        }else if (parts[0].equals("return")) {

            aType = RETURN;
            arg1 = parts[0];

        }else {

            arg1 = parts[1];	// First part

            if(parts[0].equals("push")){ aType = PUSH;}			// Type of first part

            else if(parts[0].equals("pop")){ aType = POP;}				// ^^

            else if(parts[0].equals("label")){ aType = LABEL;}			// ^^

            else if(parts[0].equals("if-goto")){ aType = IF;}				// ^^

            else if (parts[0].equals("goto")){ aType = GOTO;}			// ^^

            else if (parts[0].equals("function")){ aType = FUNCTION;}	// ^^

            else if (parts[0].equals("call")){ aType = CALL; }			// ^^
            
            
            if (aType == PUSH || aType == POP || aType == FUNCTION || aType == CALL){  	// If these types get second part

            	parts[2] = parts[2].replaceAll("\\s", "");
            	arg2 = Integer.parseInt(parts[2]);		// Second Part   
             
            }
        }
    }


    public int commandType(){

        if (aType != -1) {	// Checks to see if there is a type

            return aType;

        }else {

            throw new IllegalStateException("No Command");	// No type then cant return a type

        }

    }


    public String arg1(){

        if (commandType() != RETURN){	// Makes sure there is an arg1

            return arg1;

        }else {

            throw new IllegalStateException("No arg1 in Return types");	// Can't return something that isn't there

        }

    }

 
    public int arg2(){	// Makes sure there is an arg2

        if (commandType() == POP || commandType() == FUNCTION || commandType() == CALL || commandType() == PUSH){

            return arg2;

        }else {

            throw new IllegalStateException("No arg2 :(");	// Can't return something that isn't there

        }

    }


    public static String noComments(String input){

        int pos = input.indexOf("//");	// Finds comments

        if (pos != -1){

            input = input.substring(0, pos);	// Only takes stuff that are not comments

        }

        return input;
    }


    public static String noSpaces(String input){	// Finds spaces and shoots em down
        String output = "";

        if (input.length() != 0){

            String[] segs = input.split(" ");

            for (String s: segs){
                output += s;
            }
        }

        return output;
    }
}