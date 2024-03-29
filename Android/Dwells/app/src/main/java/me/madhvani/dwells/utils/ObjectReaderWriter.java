package me.madhvani.dwells.utils;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by anthony on 15.19.5.
 */
public interface ObjectReaderWriter<T> {
    void            writeItems       (ArrayList<T> list      )   throws IOException;
    void            writeItem        (T item                 )   throws IOException;
    ArrayList<T>    removeDuplicates (ArrayList<T> list      )   throws IOException;
    ArrayList<T>    readItems        (                       )   throws IOException;
}
