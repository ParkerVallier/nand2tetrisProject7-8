import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Code {

    private int aJumpFlag;
    private BufferedWriter writer;

    public Code(File output) throws IOException {

	    writer = new BufferedWriter(new FileWriter(output));
	    aJumpFlag = 0;

    }
   
    public void writeArithmetic(String command) throws IOException{	// Write out the Arithmetic Code

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