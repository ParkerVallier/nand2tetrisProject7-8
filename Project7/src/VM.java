import java.io.File;
import java.io.IOException;

public class VM {

    public static void main(String[] args) throws IOException {
    	 
    	
       // if(args.length != 0) {

            //File input = new File(args[0]);			// Initialize input and output
    		File input = new File("SimpleAdd.vm");
            String outputPath = "";					// ^^

            outputPath = input.getAbsolutePath().substring(0, input.getAbsolutePath().lastIndexOf(".")) + ".asm";	//output path
            
            File output = new File(outputPath);		// More initialization 
            Code writer = new Code(output);			// ^^
            Parser parser = new Parser(input);		// ^^

            int cType = -1;	

            while (parser.hasNext()) {

	            parser.advance();
	
	            cType = parser.commandType();	// stores what type
	
	            if (cType == Parser.ARITHMETIC) {	// Checks type, and does correct action
	
	            	writer.writeArithmetic(parser.arg1());
	
	            } 
	            else if (cType == Parser.POP || cType == Parser.PUSH) { // Checks type, and does correct action
	
	            	writer.writePushPop(cType, parser.arg1(), parser.arg2());
	
	            }
            }
            writer.close();	// Closes the writer
        //}
    }

}