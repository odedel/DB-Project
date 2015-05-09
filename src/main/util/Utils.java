package main.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

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
}
