# batchpdf
пакетное сжатие pdf

Возникла как то необходимость сжать большое количество pdf. К своему удивлению,бесплатного работающего решения я так и не нашел. Написал данный проект.
Использование:




      DirectoryWalker walker=new DirectoryWalker("e:\\in","e:\\out");
      try {
        walker.walk();
       } catch (IOException e) {
       
        e.printStackTrace();
       }
 В выходном каталоге создается та же самая структура папок, что и в исходном.
 Для сжатия использовал проект https://github.com/bnanes/shrink-pdf.git
 
 Замечания:
 -Качество сохранения pdf(картинок внутри pdf) задается в DirectoryWalker.java
 
       private void processFile(Path path){
        try{
            MyPdfFile pdf=new MyPdfFile(path.toString());
            pdf.compress(0); //Качество 0-100
            pdf.save(getOutPath(path).toString());
       

        } catch(IOException e){
            System.out.println("File  saving error:"+path.toString());
        } catch(MyPdfErrorException e){
            System.out.println("File compressing error:"+path.toString());
          
        }
       
      
    }
 
 
 




