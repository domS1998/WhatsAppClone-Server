package org.main.fileio;

import org.main.model.User;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;

// Klasse für objektorientierten Zufriff auf Dateien
public class File {

    protected String filePath;
    protected Object content;

    public Object getContent(){return this.content;}
    public String getFilePath(){return this.filePath;}

    public File (String filePath) throws IOException, ClassNotFoundException {
        this.filePath = filePath;
        this.content = load();

    }

    public Object load (){

        Object result = null;
        FileIn in = null;
        FileOut out = null;
        try {
            in = new FileIn(this.filePath);
            result = in.readObjectFile();

        } catch (FileNotFoundException e) {
            System.out.println("File: file "+this.filePath+" not found in working directory");
            System.out.println("File: creating new cache file "+this.filePath);
            out = new FileOut(this.filePath);
            // neue Datei erstellen

        }
        catch (EOFException e){
            System.out.println("EOF, file empty or all objects read");
        }
        catch (ClassNotFoundException e){
            System.out.println("ClassNotFoundException, object class in file doesnt implement serializable");
        }
        catch (IOException e){
            System.out.println(e);
        }
        if (in != null) {in.close();}
        if (out != null) {out.close();}

        return result;
    }

    public void write(Object obj){
        FileOut out = new FileOut(filePath);
        out.writeObj(obj);
        out.close();
        // update content
        this.content = obj;
    }

    public void print(){
        if (content == null){
            System.out.println("File '"+filePath+"' content: null");
            return;
        }
        System.out.println("File '"+filePath+"' content: "+content);
    }

//    public static void main(String[] args) throws IOException, ClassNotFoundException {
//
////        File file1 = new File ("user.cache");
////        file1.write(new User("dan", "dan123"));
//
////        FileIn in = new FileIn("user.cache");
////        System.out.println((User)in.readObjectFile());
//
//
//        File in = new File("user.cache");
//        in.print();
//
////        in.write(new User("dan", "dan123"));
//
////        in.print();
//
//
//
//
////        file1.print();
//
//
//
//
//
//    }

}
