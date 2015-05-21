package me.madhvani.dwells.utils;

import android.content.Context;
import android.util.Log;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import me.madhvani.dwells.model.Kot;

/**
 * Created by anthony on 15.19.5.
 */
public class BookmarkReaderWriter implements ObjectReaderWriter<Kot> {

    private final String city;
    private Context context;
    private static String BOOKMARKS_FILENAME = "bookmarks";
    private static String TAG = "BookmarkReaderWriter";
    private String fullName;

    @Override
    public void writeItems(ArrayList<Kot> kots) throws IOException {
        FileOutputStream fos = context.openFileOutput(fullName, Context.MODE_PRIVATE);
        ObjectOutputStream oos;

        Log.v(TAG, "Creating new bookmarks");
        oos = new ObjectOutputStream(fos);
        oos.writeObject((Serializable) removeDuplicates(kots));
    }

    @Override
    public void writeItem(Kot kot) throws IOException{
        Log.v(TAG, "Writing: " + kot.getUrl());
        ArrayList<Kot> kots = readItems();
        boolean duplicate = false;
        /*for (int i = 0; i < kots.size(); i++) {
            if(kots.get(i).getUrl() == kot.getUrl()){
                duplicate = true;
            }
        }*/
        //if(!duplicate){
            kots.add(kot);
            writeItems(kots);
        //}
    }

    @Override
    public ArrayList<Kot> removeDuplicates(ArrayList<Kot> kots) throws IOException {
        Set<Kot> setItems = new LinkedHashSet<>();
        setItems.addAll(kots);
        kots.clear();
        kots.addAll(setItems);
        return kots;
    }

    @Override
    public ArrayList<Kot> readItems() throws IOException{
        //return new ArrayList<Kot>();

        ArrayList<Kot> kots = null;
        FileInputStream fis = null;
        try {
            fis = context.getApplicationContext().openFileInput(fullName);
        } catch (FileNotFoundException e){
            return new ArrayList<Kot>();
        }

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

    public BookmarkReaderWriter(Context context, String city){
        this.context=context;
        this.city = city;
        this.fullName = BOOKMARKS_FILENAME + "_" + this.city;
    }

}
