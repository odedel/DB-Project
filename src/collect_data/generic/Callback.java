package collect_data.generic;

import collect_data.util.Row;

public abstract class Callback {

    public abstract void reduce(Row row);

    public abstract boolean map(Row row);
}
