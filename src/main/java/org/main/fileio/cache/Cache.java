package org.main.fileio.cache;

import org.main.fileio.File;
import java.io.IOException;

abstract public class Cache extends File{

    public Cache (String file) throws IOException, ClassNotFoundException {
        super(file);
    }

    public boolean isEmpty() {
        if (this.content == null) {
            return true;
        }
        return false;
    }

    public void clear(){
        if ( this.isEmpty()) {return;}

        this.write(null);  // Inhalt überschreiben

        this.content = load(); // neuen Zustand laden
    }
}
