import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Code {

    private int aJumpFlag;
    private BufferedWriter writer;
    private static final Pattern labelReg = Pattern.compile("^[^0-9][0-9A-Za-z\\_\\:\\.\\$]+");
    private static int labelCnt = 0;

    public Code(File output) throws IOException {

	    writer = new BufferedWriter(new FileWriter(output));
	    aJumpFlag = 0;

    }
   
    public void writeArith(String command) throws IOException{	// Write out the Arithmetic Code

        if (command.equals("add")){

            this.writer.write(aTemp1() + "M=M+D/n");

        }else if (command.equals("sub")){

            this.writer.write(aTemp1() + "M=M-D/n");

        }else if (command.equals("and")){

            this.writer.write(aTemp1() + "M=M&D/n");
            this.writer.newLine();

        }else if (command.equals("or")){

            this.writer.write(aTemp1() + "M=M|D/n");

        }else if (command.equals("gt")){

            this.writer.write(aTemp2("JLE"));
            aJumpFlag++;

        }else if (command.equals("lt")){

            this.writer.write(aTemp2("JGE"));
            aJumpFlag++;

        }else if (command.equals("eq")){

            this.writer.write(aTemp2("JNE"));
            aJumpFlag++;

        }else if (command.equals("not")){

            this.writer.write("@SP\nA=M-1\nM=!M\n");
  


        }else if (command.equals("neg")){

            this.writer.write("D=0\n@SP\nA=M-1\nM=D-M\n");
        }
    }

    public void writePushPop(int command, String segment, int index) throws IOException{

        if (command == Parser.PUSH){

            if (segment.equals("constant")){

                this.writer.write("@" + index + "\n" + "D=A\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");

            }else if (segment.equals("local")){

                this.writer.write(pushTemp("LCL",index,false));

            }else if (segment.equals("argument")){

                this.writer.write(pushTemp("ARG",index,false));

            }else if (segment.equals("this")){

                this.writer.write(pushTemp("THIS",index,false));

            }else if (segment.equals("that")){

                this.writer.write(pushTemp("THAT",index,false));

            }else if (segment.equals("temp")){

                this.writer.write(pushTemp("R5", index + 5,false));

            }else if (segment.equals("pointer") && index == 0){

                this.writer.write(pushTemp("THIS",index,true));

            }else if (segment.equals("pointer") && index == 1){

                this.writer.write(pushTemp("THAT",index,true));

            }else if (segment.equals("static")){

                this.writer.write(pushTemp(String.valueOf(16 + index),index,true));

            }

        }else if(command == Parser.POP){

            if (segment.equals("local")){

                this.writer.write(popTemp("LCL",index,false));

            }else if (segment.equals("argument")){

                this.writer.write(popTemp("ARG",index,false));
            }else if (segment.equals("this")){

                this.writer.write(popTemp("THIS",index,false));

            }else if (segment.equals("that")){

                this.writer.write(popTemp("THAT",index,false));

            }else if (segment.equals("temp")){

                this.writer.write(popTemp("R5", index + 5,false));

            }else if (segment.equals("pointer") && index == 0){

                this.writer.write(popTemp("THIS",index,true));

            }else if (segment.equals("pointer") && index == 1){

                this.writer.write(popTemp("THAT",index,true));

            }else if (segment.equals("static")){

                this.writer.write(popTemp(String.valueOf(16 + index),index,true));

            }

        }

    }
    
    public void writeLabel(String label) throws IOException{

        Matcher match = labelReg.matcher(label);

        if (match.find()){

            this.writer.write("(" + label +")\n");

        }

    }
    
    public void writeGo(String label) throws IOException{

        Matcher m = labelReg.matcher(label);

        if (m.find()){

            this.writer.write("@" + label +"\n0;JMP\n");

        }

    }
    
    public void writeIf(String label) throws IOException{

        Matcher m = labelReg.matcher(label);

        if (m.find()){

            this.writer.write(aTemp1() + "@" + label +"\nD;JNE\n");

        }

    }
    
    public void writeInit() throws IOException{

        this.writer.write("@256\n" + "D=A\n" + "@SP\n" + "M=D\n");
        writeCall("Sys.init",0);

    }
    
    public void writeCall(String functionName, int numArgs) throws IOException{

        String newLabel = "RETURN_LABEL" + (labelCnt++);
        this.writer.write("@" + newLabel + "\n" + "D=A\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
        this.writer.write(pushTemp("LCL",0,true));
        this.writer.write(pushTemp("ARG",0,true));
        this.writer.write(pushTemp("THIS",0,true));
        this.writer.write(pushTemp("THAT",0,true));

        this.writer.write("@SP\n" +
                        "D=M\n" +
                        "@5\n" +
                        "D=D-A\n" +
                        "@" + numArgs + "\n" +
                        "D=D-A\n" +
                        "@ARG\n" +
                        "M=D\n" +
                        "@SP\n" +
                        "D=M\n" +
                        "@LCL\n" +
                        "M=D\n" +
                        "@" + functionName + "\n" +
                        "0;JMP\n" +
                        "(" + newLabel + ")\n"
                        );

    }
    
    public void writeReturn() throws IOException{

    	this.writer.write(returnTemp());

    }
    
    public void writeFunction(String functionName, int numLocals) throws IOException{

    	this.writer.write("(" + functionName +")\n");

        for (int i = 0; i < numLocals; i++){

            writePushPop(Parser.PUSH,"constant",0);

        }

    }
    
    public String preFrameTemp(String position){

        return "@R11\n" +
                "D=M-1\n" +
                "AM=D\n" +
                "D=M\n" +
                "@" + position + "\n" +
                "M=D\n";

    }
    
    public String returnTemp(){

        return "@LCL\n" +
                "D=M\n" +
                "@R11\n" +
                "M=D\n" +
                "@5\n" +
                "A=D-A\n" +
                "D=M\n" +
                "@R12\n" +
                "M=D\n" +
                popTemp("ARG",0,false) +
                "@ARG\n" +
                "D=M\n" +
                "@SP\n" +
                "M=D+1\n" +
                preFrameTemp("THAT") +
                preFrameTemp("THIS") +
                preFrameTemp("ARG") +
                preFrameTemp("LCL") +
                "@R12\n" +
                "A=M\n" +
                "0;JMP\n";
    }

    public void close() throws IOException{	// Close the writer

        writer.close();

    }

 
    private String aTemp1(){	// Template 1

        return "@SP\n" + "AM=M-1\n" + "D=M\n" + "A=A-1\n";

    }

    private String aTemp2(String type){ // Template 2

        return "@SP\n" +
                "AM=M-1\n" +
                "D=M\n" +
                "A=A-1\n" +
                "D=M-D\n" +
                "@FALSE" + aJumpFlag + "\n" +
                "D;" + type + "\n" +
                "@SP\n" +
                "A=M-1\n" +
                "M=-1\n" +
                "@CONTINUE" + aJumpFlag + "\n" +
                "0;JMP\n" +
                "(FALSE" + aJumpFlag + ")\n" +
                "@SP\n" +
                "A=M-1\n" +
                "M=0\n" +
                "(CONTINUE" + aJumpFlag + ")\n";

    }

    private String pushTemp(String segment, int index, boolean isD){	// Push Template

        String noPointerCode = (isD)? "" : "@" + index + "\n" + "A=D+A\nD=M\n";

        return "@" + segment + "\n" +
                "D=M\n"+
                noPointerCode +
                "@SP\n" +
                "A=M\n" +
                "M=D\n" +
                "@SP\n" +
                "M=M+1\n";

    }

    private String popTemp(String segment, int index, boolean isDirect){	// Pop Template

        String noPointerCode = (isDirect)? "D=A\n" : "D=M\n@" + index + "\nD=D+A\n";

        return "@" + segment + "\n" +
                noPointerCode +
                "@R13\n" +
                "M=D\n" +
                "@SP\n" +
                "AM=M-1\n" +
                "D=M\n" +
                "@R13\n" +
                "A=M\n" +
                "M=D\n";

    }

}