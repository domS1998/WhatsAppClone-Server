package org.main.fileio;

import org.main.model.User;

import java.io.*;

public class FileIn {

    private FileInputStream   fileInputStream   = null;
    private ObjectInputStream objectInputStream = null;

    public FileIn (String fileName) throws FileNotFoundException {
        try { this.fileInputStream = new FileInputStream(fileName);
              this.objectInputStream = new ObjectInputStream(fileInputStream);}
        catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
        catch(IOException | SecurityException | NullPointerException e){
            System.out.println("FileIn: "+e.getMessage());
        }
    }

     Object readNextObj(){
        Object result = null;
        try {result = objectInputStream.readObject();}
        catch (EOFException e){
            System.out.println("FileIn: end of file, cant read more objects");
        }
        catch (ClassNotFoundException e){
            System.out.println("FileIn: class doesnt implement serializable interface");
        }
        catch (IOException|NullPointerException e){
            System.out.println(e);
        }
        return result;
     }

    public void close(){
        try {objectInputStream.close();fileInputStream.close();}
        catch(NullPointerException|IOException e){System.out.println(e);}
    }

    public Object readObjectFile() throws IOException, ClassNotFoundException {
        Object result = this.readNextObj();
        return result;
    }

//    public static void main(String[] args) throws IOException, ClassNotFoundException {
//
////        // save new file or overwrite
////        FileOut newFile = new FileOut("test.cache", false);
////        newFile.writeObj("empty cache file");
//
//        // read in newly written file
//        FileIn fileObj = new FileIn("user.cache");
//        User cacheFile = (User)fileObj.readNextObj();
//        System.out.println(cacheFile);
//
//    }

}
