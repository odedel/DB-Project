package main.util;

/**
 * Created by Tomer on 09/05/2015.
 */
public abstract class Callback {

    public abstract void reduce(Row row);

    public abstract boolean map(Row row);
}
