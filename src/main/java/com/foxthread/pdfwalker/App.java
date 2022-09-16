package com.foxthread.pdfwalker;

import java.io.IOException;

public class App 
{
    public static void main( String[] args )
    {
       DirectoryWalker walker=new DirectoryWalker();
       try {
        walker.walk();
    } catch (IOException e) {
       
        e.printStackTrace();
    }
    }
}
