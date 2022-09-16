
package com.foxthread.pdf.mypdf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;

import com.foxthread.pdf.shrink.ShrinkPDF;


public class MyPdfFile{

    final private String fullPath;
    final private PDDocument pdfDocument;

    
    
    public MyPdfFile(String fullPath) throws IOException{
        this.fullPath=fullPath;
        pdfDocument=Loader.loadPDF(new File(fullPath));
      
    }

    //quality 0-100
    public void compress(int quality) throws MyPdfErrorException{ 
        ShrinkPDF shrinker=new ShrinkPDF();
      
        if(quality <0 || quality>100){
            throw new  MyPdfErrorException("Error in quality factor");
        }
        
        shrinker.setCompQual(quality/100);
      

        try{
            shrinker.shrinkMe(pdfDocument);
        } catch(FileNotFoundException e){
            throw new
              MyPdfErrorException("Error compressing Pdf File,action error");

        } catch(IOException e){
            throw new 
             MyPdfErrorException("Error compressing Pdf File,action error");
        }
        
    }


    public void save(String fullPath) throws IOException{
        pdfDocument.save(new File(fullPath));
    }




}