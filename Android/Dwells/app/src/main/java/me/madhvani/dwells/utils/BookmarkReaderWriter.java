package me.madhvani.dwells.utils;

import android.content.Context;
import android.util.Log;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import me.madhvani.dwells.model.Kot;

/**
 * Created by anthony on 15.19.5.
 */
public class BookmarkReaderWriter implements ObjectReaderWriter<Kot> {

    private Context context;
    private static String BOOKMARKS_FILENAME = "bookmarks";
    private static String TAG = "BookmarkReaderWriter";

    @Override
    public void writeItems(ArrayList<Kot> kots) throws IOException {
        FileOutputStream fos = context.openFileOutput(BOOKMARKS_FILENAME, Context.MODE_PRIVATE);
        ObjectOutputStream oos;

        Log.v(TAG, "Creating new bookmarks");
        oos = new ObjectOutputStream(fos);
        oos.writeObject((Serializable) kots);
    }

    @Override
    public void writeItem(Kot kot) throws IOException{
        Log.v(TAG, "Writing: " + kot.getUrl());
        ArrayList<Kot> kots = readItems();
        kots.add(kot);
        writeItems(kots);
    }

    @Override
    public ArrayList<Kot> readItems() throws IOException{
        ArrayList<Kot> kots = null;
        FileInputStream fis = context.getApplicationContext().openFileInput(BOOKMARKS_FILENAME);
        ObjectInputStream ois = null;

        try {
            ois = new ObjectInputStream(fis);
        } catch (EOFException e){
            Log.v(TAG, "EOF: " + e.getMessage());
            return new ArrayList<Kot>();
        }

        try {
            //http://stackoverflow.com/a/19213702
            kots = (ArrayList<Kot>) ois.readObject();
        } catch (EOFException e) {
            Log.v(TAG,"EOF was reached: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            Log.v(TAG, "Could not readObject -> Kots: " + e.getMessage());
        } finally {
            ois.close();
        }

        if (kots != null) {
            return kots;
        } else {
            return new ArrayList<Kot>();
        }
    }

    public BookmarkReaderWriter(Context context){
        this.context=context;
    }

}
