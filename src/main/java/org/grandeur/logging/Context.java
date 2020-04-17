package org.grandeur.logging;

import org.grandeur.logging.interfaces.Logger;
import org.grandeur.utils.helpers.DateTimeHelper;
import org.grandeur.utils.helpers.StringHelper;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Locale;
import java.util.UUID;

public class Context implements AutoCloseable {
    public final static Context NULL = new Context(0, "null");

    private String id;
    private String value;
    private Logger logger;

    private String startTime;
    private String endTime;

    private Context(int id, String value) {
        this.id = BuildId(id);
        this.value = value;
        this.startTime = DateTimeHelper.GetCompleteDateFormat(Instant.now());
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

    public String GetStartTime() {
        return startTime;
    }

    @Override
    public void close() {
        try {
            endTime = DateTimeHelper.GetCompleteDateFormat(Instant.now());
            Instant startInstant = DateTimeHelper.ToInstant(startTime, DateTimeHelper.DefaultFormat, Locale.getDefault(), ZoneId.systemDefault());
            Instant endInstant = DateTimeHelper.ToInstant(endTime, DateTimeHelper.DefaultFormat, Locale.getDefault(), ZoneId.systemDefault());
            Duration duration = Duration.between(startInstant, endInstant);
            GetLogger().Debug("Context removed with total duration = " + duration.getSeconds() + "." + duration.getNano() + "s");
            DC.Pop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
