package org.grandeur.logging;

import org.grandeur.logging.interfaces.Logger;

import java.util.concurrent.ConcurrentHashMap;

public class LogManager {
    private static ConcurrentHashMap<Thread, LogContext> loggerContexts = new ConcurrentHashMap<>();

    public static Logger GetLogger(Class<?> type) {
        return GetLogger(type.getName());
    }

    public static Logger GetLogger(String className) {
        if (GetCurrent().GetLogger(className) == null) {
            Logger log = new Log(className);
            GetCurrent().AddLogger(log);
        }

        return GetCurrent().GetLogger(className);
    }

    static LogContext GetCurrent() {
        if (!loggerContexts.containsKey(Thread.currentThread())) {
            loggerContexts.put(Thread.currentThread(), new LogContext());
        }

        return loggerContexts.get(Thread.currentThread());
    }

    public static LogContext[] GetAllLogContext() {
        LogContext[] contexts = new LogContext[loggerContexts.size()];
        return loggerContexts.values().toArray(contexts);
    }

    static Thread[] GetAllThreadContext() {
        Thread[] contexts = new Thread[loggerContexts.size()];
        return loggerContexts.keySet().toArray(contexts);
    }

    public static void Clean() {
        for (Thread t : LogManager.GetAllThreadContext()) {
            if (!t.isAlive()) {
                loggerContexts.remove(t);
                LogManager.GetLogger(LogManager.class).Debug("Thread (" + t.getName() + ") removed!");
            }
        }
    }
}
