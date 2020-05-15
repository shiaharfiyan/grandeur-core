package org.grandeur.logging;

import org.grandeur.logging.interfaces.Logger;
import org.grandeur.utils.helpers.DateTimeHelper;
import org.grandeur.utils.helpers.StringHelper;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;
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
public class Context implements AutoCloseable {
    private ConcurrentHashMap<LogContext, HashMap<String, String>> mdcContexts;
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
        this.mdcContexts = new ConcurrentHashMap<>();
    }

    public Context(String value) {
        this(UUID.randomUUID().toString().hashCode(), value);
    }

    private String BuildId(int id) {
        return StringHelper.PadLeft(StringHelper.Limit(String.valueOf(Math.abs(id)), 15), 15, '0');
    }

    public void UseLogger(Logger logger) {
        this.logger = logger;
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

    public LogContext GetLogContext() {
        return LogManager.GetCurrent();
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

    public HashMap<String, String> Maps() {
        if (!mdcContexts.containsKey(LogManager.GetCurrent()))
            mdcContexts.put(LogManager.GetCurrent(), new HashMap<>());

        return (HashMap<String, String>) mdcContexts.get(LogManager.GetCurrent()).clone();
    }

    public void Put(String key, String value) {
        if (!mdcContexts.containsKey(LogManager.GetCurrent()))
            mdcContexts.put(LogManager.GetCurrent(), new HashMap<>());

        mdcContexts.get(LogManager.GetCurrent())
                .put(key, value);
    }

    public void Remove(String key) {
        if (!mdcContexts.containsKey(LogManager.GetCurrent()))
            mdcContexts.put(LogManager.GetCurrent(), new HashMap<>());

        mdcContexts.get(LogManager.GetCurrent())
                .remove(key);
    }

    public String Get(String key) {
        if (mdcContexts.size() > 0) {
            if (!mdcContexts.containsKey(LogManager.GetCurrent()))
                mdcContexts.put(LogManager.GetCurrent(), new HashMap<>());

            if (mdcContexts.get(LogManager.GetCurrent()).containsKey(key)) {
                return mdcContexts.get(LogManager.GetCurrent())
                        .get(key);
            }
        }

        return "null";
    }

    @Override
    public void close() {
        try {
            endTime = DateTimeHelper.GetCompleteDateFormat(Instant.now());
            Instant startInstant = DateTimeHelper.ToInstant(startTime, DateTimeHelper.DefaultFormat, Locale.getDefault(), ZoneId.systemDefault());
            Instant endInstant = DateTimeHelper.ToInstant(endTime, DateTimeHelper.DefaultFormat, Locale.getDefault(), ZoneId.systemDefault());
            Duration duration = Duration.between(startInstant, endInstant);
            if (logger != null)
                GetLogger().Debug("Context removed with total duration = " + duration.getSeconds() + "." + duration.getNano() + "s");
            DC.Pop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
