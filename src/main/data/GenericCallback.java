package main.data;

import main.util.Callback;
import main.util.Row;

import java.lang.reflect.Field;
import java.util.Map;

import static main.util.Utils.parseName;
import static main.util.Utils.parseValue;

public class GenericCallback extends Callback {

    ValueType valueType;
    String relationType;
    String keyName;
    final Map<String, ? extends Entity> entities;
    Field field;

    public GenericCallback(final Map<String, ? extends Entity> entities, ValueType valueType, String relationType, String keyName) {
        this.entities = entities;
        this.valueType = valueType;
        this.relationType = relationType;
        this.keyName = keyName;
        Class<?> clazz = entities.values().iterator().next().getClass();
        try {
            field = clazz.getField(keyName);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void reduce(Row row) {
        try {
            field.set(entities.get(row.entity), parseValue(row.superEntity, valueType));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean map(Row row) {
        return row.relationType.equals(relationType) && entities.keySet().contains(row.entity);
    }
}
