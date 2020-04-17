package org.grandeur.logging;

import org.grandeur.logging.interfaces.Logger;

import java.util.concurrent.ConcurrentHashMap;

/**
 *     Grandeur - a tool for logging, create config file based on ini and
 *     utils
 *     Copyright (C) 2020 Harfiyan Shia.
 *
 *     This file is part of grandeur-core.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/.
 */
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
