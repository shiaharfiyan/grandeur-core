package org.grandeur.logging;

import org.grandeur.logging.interfaces.Logger;

import java.util.concurrent.ConcurrentHashMap;

public class LogContext {
    private ConcurrentHashMap<String, Logger> loggers;

    LogContext() {
        loggers = new ConcurrentHashMap<>();
    }

    public boolean AddLogger(Logger logger) {
        if (loggers.containsKey(logger.GetName())) {
            return false;
        }

        loggers.put(logger.GetName(), logger);
        return true;
    }

    public Logger GetLogger(String key) {
        if (loggers.containsKey(key)) {
            return loggers.get(key);
        }

        return null;
    }

    public Logger GetLogger(Class<?> clazz) {
        if (loggers.containsKey(clazz.getName())) {
            return loggers.get(clazz.getName());
        }

        return null;
    }

    public Logger[] GetLoggers() {
        Logger[] loggers = new Logger[this.loggers.size()];
        return this.loggers.values().toArray(loggers);
    }
}
