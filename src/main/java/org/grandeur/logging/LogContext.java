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
