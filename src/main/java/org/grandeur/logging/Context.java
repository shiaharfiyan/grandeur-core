package io.github.shiaharfiyan.logging;

import io.github.shiaharfiyan.logging.interfaces.Logger;
import io.github.shiaharfiyan.utils.helpers.StringHelper;

import java.util.UUID;

public class Context implements AutoCloseable {
    public final static Context NULL = new Context(0, "null");

    private String id;
    private String value;
    private Logger logger;

    private Context(int id, String value) {
        this.id = BuildId(id);
        this.value = value;
    }

    public Context(String value) {
        this(UUID.randomUUID().toString().hashCode(), value);
    }

    private String BuildId(int id) {
        return StringHelper.PadLeft(StringHelper.Limit(String.valueOf(Math.abs(id)), 15), 15, '0');
    }

    public void UseLogger(Class<?> clazz) {
        logger = LogManager.GetLogger(clazz);
    }

    public void UseLogger(String className) {
        logger = LogManager.GetLogger(className);
    }

    public Logger GetLogger() {
        return logger;
    }

    public String GetId() {
        return id;
    }

    public String GetValue() {
        return value;
    }

    @Override
    public void close() {
        try {
            DC.Pop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
