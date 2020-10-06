
package com.github.mikephil.chartingNIR.utilsNIR;

import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import com.github.mikephil.chartingNIR.dataNIR.BarEntryNIR;
import com.github.mikephil.chartingNIR.dataNIR.EntryNIR;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Utilities class for interacting with the assets and the devices storage to
 * load and save DataSet objects from and to .txt files.
 * 
 * @author Philipp Jahoda
 */
public class FileUtils {

    private static final String LOG = "MPChart-FileUtils";

    /**
     * Loads a an Array of Entries from a textfile from the sd-card.
     * 
     * @param path the name of the file on the sd-card (+ path if needed)
     * @return
     */
    public static List<EntryNIR> loadEntriesFromFile(String path) {

        File sdcard = Environment.getExternalStorageDirectory();

        // Get the text file
        File file = new File(sdcard, path);

        List<EntryNIR> entries = new ArrayList<EntryNIR>();

        try {
            @SuppressWarnings("resource")
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                String[] split = line.split("#");

                if (split.length <= 2) {
                    entries.add(new EntryNIR(Float.parseFloat(split[0]), Integer.parseInt(split[1])));
                } else {

                    float[] vals = new float[split.length - 1];

                    for (int i = 0; i < vals.length; i++) {
                        vals[i] = Float.parseFloat(split[i]);
                    }

                    entries.add(new BarEntryNIR(vals, Integer.parseInt(split[split.length - 1])));
                }
            }
        } catch (IOException e) {
            Log.e(LOG, e.toString());
        }

        return entries;

        // File sdcard = Environment.getExternalStorageDirectory();
        //
        // // Get the text file
        // File file = new File(sdcard, path);
        //
        // List<EntryNIR> entries = new ArrayList<EntryNIR>();
        // String label = "";
        //
        // try {
        // @SuppressWarnings("resource")
        // BufferedReader br = new BufferedReader(new FileReader(file));
        // String line = br.readLine();
        //
        // // firstline is the label
        // label = line;
        //
        // while ((line = br.readLine()) != null) {
        // String[] split = line.split("#");
        // entries.add(new EntryNIR(Float.parseFloat(split[0]),
        // Integer.parseInt(split[1])));
        // }
        // } catch (IOException e) {
        // Log.e(LOG, e.toString());
        // }
        //
        // DataSet ds = new DataSet(entries, label);
        // return ds;
    }

    /**
     * Loads an array of Entries from a textfile from the assets folder.
     * 
     * @param am
     * @param path the name of the file in the assets folder (+ path if needed)
     * @return
     */
    public static List<EntryNIR> loadEntriesFromAssets(AssetManager am, String path) {

        List<EntryNIR> entries = new ArrayList<EntryNIR>();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(am.open(path), "UTF-8"));

            String line = reader.readLine();

            while (line != null) {
                // process line
                String[] split = line.split("#");

                if (split.length <= 2) {
                    entries.add(new EntryNIR(Float.parseFloat(split[0]), Integer.parseInt(split[1])));
                } else {

                    float[] vals = new float[split.length - 1];

                    for (int i = 0; i < vals.length; i++) {
                        vals[i] = Float.parseFloat(split[i]);
                    }

                    entries.add(new BarEntryNIR(vals, Integer.parseInt(split[split.length - 1])));
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            Log.e(LOG, e.toString());

        } finally {

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(LOG, e.toString());
                }
            }
        }

        return entries;

        // String label = null;
        // List<EntryNIR> entries = new ArrayList<EntryNIR>();
        //
        // BufferedReader reader = null;
        // try {
        // reader = new BufferedReader(
        // new InputStreamReader(am.open(path), "UTF-8"));
        //
        // // do reading, usually loop until end of file reading
        // label = reader.readLine();
        // String line = reader.readLine();
        //
        // while (line != null) {
        // // process line
        // String[] split = line.split("#");
        // entries.add(new EntryNIR(Float.parseFloat(split[0]),
        // Integer.parseInt(split[1])));
        // line = reader.readLine();
        // }
        // } catch (IOException e) {
        // Log.e(LOG, e.toString());
        //
        // } finally {
        //
        // if (reader != null) {
        // try {
        // reader.close();
        // } catch (IOException e) {
        // Log.e(LOG, e.toString());
        // }
        // }
        // }
        //
        // DataSet ds = new DataSet(entries, label);
        // return ds;
    }

    /**
     * Saves an Array of Entries to the specified location on the sdcard
     * 
     * @param ds
     * @param path
     */
    public static void saveToSdCard(List<EntryNIR> entries, String path) {

        File sdcard = Environment.getExternalStorageDirectory();

        File saved = new File(sdcard, path);
        if (!saved.exists())
        {
            try
            {
                saved.createNewFile();
            } catch (IOException e)
            {
                Log.e(LOG, e.toString());
            }
        }
        try
        {
            // BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(saved, true));

            for (EntryNIR e : entries) {

                buf.append(e.getVal() + "#" + e.getXIndex());
                buf.newLine();
            }

            buf.close();
        } catch (IOException e)
        {
            Log.e(LOG, e.toString());
        }
    }

    public static List<BarEntryNIR> loadBarEntriesFromAssets(AssetManager am, String path) {

        List<BarEntryNIR> entries = new ArrayList<BarEntryNIR>();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(am.open(path), "UTF-8"));

            String line = reader.readLine();

            while (line != null) {
                // process line
                String[] split = line.split("#");

                entries.add(new BarEntryNIR(Float.parseFloat(split[0]), Integer.parseInt(split[1])));

                line = reader.readLine();
            }
        } catch (IOException e) {
            Log.e(LOG, e.toString());

        } finally {

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(LOG, e.toString());
                }
            }
        }

        return entries;

        // String label = null;
        // ArrayList<EntryNIR> entries = new ArrayList<EntryNIR>();
        //
        // BufferedReader reader = null;
        // try {
        // reader = new BufferedReader(
        // new InputStreamReader(am.open(path), "UTF-8"));
        //
        // // do reading, usually loop until end of file reading
        // label = reader.readLine();
        // String line = reader.readLine();
        //
        // while (line != null) {
        // // process line
        // String[] split = line.split("#");
        // entries.add(new EntryNIR(Float.parseFloat(split[0]),
        // Integer.parseInt(split[1])));
        // line = reader.readLine();
        // }
        // } catch (IOException e) {
        // Log.e(LOG, e.toString());
        //
        // } finally {
        //
        // if (reader != null) {
        // try {
        // reader.close();
        // } catch (IOException e) {
        // Log.e(LOG, e.toString());
        // }
        // }
        // }
        //
        // DataSet ds = new DataSet(entries, label);
        // return ds;
    }
}
