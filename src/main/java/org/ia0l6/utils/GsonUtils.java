package org.ia0l6.utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/****
 *** author：lao
 *** package：org.ia0l6
 *** project：CrossFireMacro
 *** name：GsonUtils
 *** date：2023/12/4  14:06
 *** filename：GsonUtils
 *** desc：
 ***/

public class GsonUtils {
    private static final Gson gson = new GsonBuilder().create();

    public static void write(String filePath, Object object) throws IOException {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(filePath));
            gson.toJson(object, object.getClass(), writer);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    public static <T> T read(String filePath, Class<T> classOfT) throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filePath));
            return gson.fromJson(reader, classOfT);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
}
