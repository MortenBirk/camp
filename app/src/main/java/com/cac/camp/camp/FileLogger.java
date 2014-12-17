package com.cac.camp.camp;

import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.io.*;
import java.util.*;
import android.content.Context;
import android.util.Log;

public class FileLogger{

    public static void write(String s, String fileName, Context context) {

        FileOutputStream outputStream;

        s += "\n";

        try {
            outputStream = context.openFileOutput(fileName, Context.MODE_APPEND);
            outputStream.write(s.getBytes());
            outputStream.close();
            //Log.d("fileLog", "Logged HMM file");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void initFile(Context context, String fileName) {
        try {
            FileOutputStream outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write("".getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}