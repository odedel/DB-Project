package main.util;

import java.io.*;

public class Utils {
    public static void reduceEntitiesByAttributeFromCollectionWithMatcher(String filePath, Callback... callbacks) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
        String line = reader.readLine();
        while (line != null) {
            String[] split = line.split("\t");
            Row row = new Row(split);
            for (Callback callback : callbacks) {
                if (callback.map(row)) {
                    callback.reduce(row);
                }
            }
            line = reader.readLine();
        }
    }

    public static float parseFloatFromString(String s) {
        return Float.parseFloat(s.substring(1, s.indexOf("^") - 1));
    }

    public static int parseIntFromString(String s) {
        return Integer.parseInt(s.substring(1, s.indexOf("^") - 1));
    }

    public static String parseNameFromPrefLabel(String s) {
        return s.substring(1, s.lastIndexOf("@") - 1);
    }
}
