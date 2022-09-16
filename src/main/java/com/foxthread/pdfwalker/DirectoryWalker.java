package com.foxthread.pdfwalker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import com.foxthread.pdf.mypdf.MyPdfErrorException;
import com.foxthread.pdf.mypdf.MyPdfFile;


public class DirectoryWalker {
    
    private String inPath;
    private String outPath;

    public DirectoryWalker(String inPath,String outPath){
        this.inPath=inPath;
        this.outPath=outPath;
    }

    public DirectoryWalker(){
        inPath="e:\\pdf\\in";
        outPath="e:\\pdf\\out";
    }

    
    public String getPathStringForRegularExpression(String pathString){
        String res="";
        for(char c:pathString.toCharArray()){
            res=res+c;
            if(c=='\\'){
                res=res+'\\';
            }

        }
        return res;

    }

    public static void main(String [] argc){
        DirectoryWalker dw=new DirectoryWalker();
        
        System.out.println(dw.getPathStringForRegularExpression("e:\\pdf\\in"));
    }
    
    
    private Path getOutPath(Path path){
        RegParser regParser=new RegParser();
        
        
        regParser.parseString("(?U)("+getPathStringForRegularExpression(inPath)+")(.*)",
        path.toString());
        
        String filePath=regParser.getGroup(2);
        if(filePath !=null){
            return(Paths.get(outPath+filePath));
        } else {
            return null;
        }

    }
    
    private void processFile(Path path){
        try{
            MyPdfFile pdf=new MyPdfFile(path.toString());
            pdf.compress(0);
            pdf.save(getOutPath(path).toString());
       

        } catch(IOException e){
            System.out.println("File  saving error:"+path.toString());
        } catch(MyPdfErrorException e){
            System.out.println("File compressing error:"+path.toString());
          
        }
       
       
    }


    
    
    
    
    public void walk() throws IOException{
       
        Path in=Paths.get(inPath);
       
          
        
        try ( Stream<Path> files=Files.walk(in) ){
           files.forEach((Path path)-> {
            
            if(Files.isDirectory(path) && (path.compareTo(in)!=0)){
                try{
                   Files.createDirectory(getOutPath(path));
                } catch(Exception e){
                    System.out.println("Error creating directory"+path.toString());
                }                 
            } else 

            if(Files.isRegularFile(path)){
                processFile(path);


            }
        });

        }

    }
   




    


}
