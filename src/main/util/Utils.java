package main.util;

import java.io.*;
import java.util.Collections;
import java.util.List;

public class Utils {
    public static void reduceEntitiesByAttributeFromCollectionWithMatcher(String filePath, List<Callback> callbacks) throws IOException {
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

    public static void reduceEntitiesByAttributeFromCollectionWithMatcher(String filePath, Callback callback) throws IOException {
        reduceEntitiesByAttributeFromCollectionWithMatcher(filePath, Collections.singletonList(callback));
    }
}
