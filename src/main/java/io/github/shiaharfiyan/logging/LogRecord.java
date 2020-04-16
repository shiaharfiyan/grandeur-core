package io.github.shiaharfiyan.logging;

import io.github.shiaharfiyan.logging.interfaces.Logger;
import io.github.shiaharfiyan.utils.helpers.ArrayHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressWarnings("Duplicates")
public class LogRecord {
    private String value;
    private Date timestamp;
    private Level level;

    private Logger logger;

    public LogRecord(Logger logger, Date timestamp, Level printLevel, String value) {
        this.logger = logger;
        this.timestamp = timestamp;
        this.level = printLevel;
        this.value = value;
    }

    public void SetValue(String value) {
        this.value = value;
    }

    public Date GetTimestamp() {
        return timestamp;
    }

    public Level GetLevel() {
        return level;
    }

    public void SetPrintLevel(Level level) {
        this.level = level;
    }

    public String GetValue() {
        return value;
    }

    public void SetTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String[] Build() {
        List<String> multilineValues = new ArrayList<>();
        if (value != null) {
            String[] values = GetValue().split("\\r?\\n");
            for (String v : values) {
                if (!v.trim().equals("")) {
                    multilineValues.add(v);
                }
            }
        }
        return ArrayHelper.StringListToArray(multilineValues);
    }

    public Logger GetLogger() {
        return logger;
    }
}
