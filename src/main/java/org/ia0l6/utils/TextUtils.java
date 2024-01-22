package org.ia0l6.utils;

import java.io.*;

/****
 *** author：lao
 *** package：org.ia0l6
 *** project：CrossFireMacro
 *** name：TextUtils
 *** date：2023/12/4  13:46
 *** filename：TextUtils
 *** desc：文本读写工具类
 ***/

public class TextUtils {

    /**
     * 读取文本文件内容
     *
     * @param filePath 文件路径
     * @return 文件内容
     */
    public static String readTextFile(String filePath) {
        StringBuilder content = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
                content.append("\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    /**
     * 写入文本文件内容
     *
     * @param filePath  文件路径
     * @param content   文件内容
     * @param overwrite 是否覆盖已存在的文件，true表示覆盖，false表示追加
     */
    public static void writeTextFile(String filePath, String content, boolean overwrite) {
        try {
            if (overwrite) {
                FileWriter writer = new FileWriter(filePath);
                writer.write(content);
                writer.close();
            } else {
                BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true));
                writer.write(content);
                writer.newLine();
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
