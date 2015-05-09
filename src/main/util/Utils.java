package main.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Tomer on 09/05/2015.
 */
public class Utils {
    public static void reduceEntitiesByAttributeFromCollectionWithMatcher(String filePath, Callback callback) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
        String line = reader.readLine();
        while (line != null) {
            String[] split = line.split("\t");
            Row row = new Row(split);
            if (callback.map(row)) {
                callback.reduce(row);
            }
            line = reader.readLine();
        }
    }
}
