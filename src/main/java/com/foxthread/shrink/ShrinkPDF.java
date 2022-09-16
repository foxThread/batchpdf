/**
 *
 * @author Benjamin Nanes
 */

package com.foxthread.shrink;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

/**
 * App for shrinking PDF files by applying jpeg compression
 * 
 * @author Benjamin Nanes, bnanes@emory.edu
 */
public final class ShrinkPDF {
    
     // -- Fields --
     
     
     private float compQual = -1;
     private boolean tiff = false;
   
     
    
         
     
    
    
     
     
     /**
      * Set the compression quality parameter.
      * @param compQual Number between 0 (low quality, small file size) 
      *                 and 1 (high quality, large file size)
      * @throws ShrinkerException if {@code compQual} is out of bounds
      */
     public void setCompQual(final float compQual) {
       
         this.compQual = compQual;
     }
     
    
     /**
      * Set {@code true} to embed images as uncompressed TIFFs.
      */
     public void setTiff(final boolean tiff) {
         this.tiff = tiff;
     }
     
    
     
     /**
      * Shrink a PDF
      * @param f {@code File} pointing to the PDF to shrink
      * @param compQual Compression quality parameter. 0 is
      *                 smallest file, 1 is highest quality.
      * @return The compressed {@code PDDocument}
      * @throws FileNotFoundException
      * @throws IOException 
      */
     public PDDocument shrinkMe(PDDocument doc) throws FileNotFoundException, IOException 
              {
         

        
          final PDPageTree pages = doc.getPages();
          final ImageWriter imgWriter;
          final ImageWriteParam iwp;
          if(tiff) {
              final Iterator<ImageWriter> tiffWriters =
                    ImageIO.getImageWritersBySuffix("png");
              imgWriter = tiffWriters.next();
              iwp = imgWriter.getDefaultWriteParam();
              //iwp.setCompressionMode(ImageWriteParam.MODE_DISABLED);
          } else {
              final Iterator<ImageWriter> jpgWriters = 
                    ImageIO.getImageWritersByFormatName("jpeg");
              imgWriter = jpgWriters.next();
              iwp = imgWriter.getDefaultWriteParam();
              iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
              iwp.setCompressionQuality(compQual);
          }
          for(PDPage p : pages) {
               scanResources(p.getResources(), doc, imgWriter, iwp);
          }
          return doc;
     }
     
     private void scanResources(
           final PDResources rList,
           final PDDocument doc,
           final ImageWriter imgWriter,
           final ImageWriteParam iwp)
           throws FileNotFoundException, IOException {
         Iterable<COSName> xNames = rList.getXObjectNames();
         for(COSName xName : xNames) {
            final PDXObject xObj = rList.getXObject(xName);
            if(xObj instanceof PDFormXObject)
                scanResources(((PDFormXObject)xObj).getResources(), doc, imgWriter, iwp);
            if(!(xObj instanceof PDImageXObject))
                 continue;
            final PDImageXObject img = (PDImageXObject)xObj;
            System.out.println("Compressing image: " + xName.getName());
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imgWriter.setOutput(ImageIO.createImageOutputStream(baos));
            BufferedImage bi = img.getImage();
            IIOImage iioi;
            if(bi.getTransparency() == BufferedImage.OPAQUE) {
                iioi = new IIOImage(bi, null, null);  
            } else if(bi.getTransparency() == BufferedImage.TRANSLUCENT) {                
                iioi = new IIOImage(img.getOpaqueImage(), null, null);
            } else {
                iioi = new IIOImage(img.getOpaqueImage(), null, null); 
            }
            imgWriter.write(null, iioi, iwp);
            final ByteArrayInputStream bais = 
                    new ByteArrayInputStream(baos.toByteArray());   
            final PDImageXObject imgNew;
            if(tiff)
                imgNew = LosslessFactory.createFromImage(doc, img.getImage());
            else
                imgNew = JPEGFactory.createFromStream(doc, bais);
            rList.put(xName, imgNew);
         }
     }
     
    

}
