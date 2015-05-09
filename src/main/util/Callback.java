package main.util;

public abstract class Callback {

    public abstract void reduce(Row row);

    public abstract boolean map(Row row);
}
