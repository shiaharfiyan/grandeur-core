package org.grandeur.logging;

import org.grandeur.Grandeur;
import org.grandeur.logging.appenders.FileLogAppender;
import org.grandeur.logging.interfaces.LogAppender;
import org.grandeur.logging.interfaces.Logger;
import org.grandeur.utils.helpers.ArrayHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

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
public class Log implements Logger {
    private HashMap<String, LogAppender> logAppenderList;
    private String name;

    Log(String name) {
        this.name = name;
        this.logAppenderList = new HashMap<>();
        this.SetLevel(Level.INFO);

        Grandeur.Instance.Monitor();
        LogConfiguration.Instance.Load(this);
    }

    @Override
    public String GetName() {
        return name;
    }

    public boolean AddAppender(LogAppender appender) {
        for (LogAppender currentAppender : logAppenderList.values()) {
            if (currentAppender instanceof FileLogAppender && appender instanceof FileLogAppender) {
                if (((FileLogAppender) currentAppender).GetFullPath().equals(((FileLogAppender) appender).GetFullPath())) {
                    return false;
                }
            } else {
                if (currentAppender.getClass().getName().equals(appender.getClass().getName())) {
                    return false;
                }
            }
        }

        logAppenderList.put(UUID.randomUUID().toString(), appender);

        return true;
    }

    public LogAppender[] GetLogAppenderList() {
        return ArrayHelper.ToArray(LogAppender.class, logAppenderList.values());
    }

    public void CleanAppenderList() {
        logAppenderList.clear();
    }

    @Override
    public void Error(String data) {
        logAppenderList.values().forEach((la) -> {
            la.Append(Level.ERROR, data);
        });
    }

    @Override
    public void Error(String prefix, String[] lines) {
        logAppenderList.values().forEach((la) -> {
            for (String line : lines)
                la.Append(Level.ERROR, prefix + line);
        });
    }

    @Override
    public void Warn(String data) {
        logAppenderList.values().forEach((la) -> {
            la.Append(Level.WARN, data);
        });
    }

    @Override
    public void Warn(String prefix, String[] lines) {
        logAppenderList.values().forEach((la) -> {
            for (String line : lines)
                la.Append(Level.WARN, prefix + line);
        });
    }

    @Override
    public void Info(String data) {
        logAppenderList.values().forEach((la) -> {
            la.Append(Level.INFO, data);
        });
    }

    public void Info(String prefix, String[] lines) {
        logAppenderList.values().forEach((la) -> {
            for (String line : lines)
                la.Append(Level.INFO, prefix + line);
        });
    }

    @Override
    public void Debug(String data) {
        logAppenderList.values().forEach((la) -> {
            la.Append(Level.DEBUG, data);
        });
    }

    @Override
    public void Debug(String prefix, String[] lines) {
        logAppenderList.values().forEach((la) -> {
            for (String line : lines)
                la.Append(Level.DEBUG, prefix + line);
        });
    }

    @Override
    public void Trace(String message) {
        logAppenderList.values().forEach((la) -> {
            StackTraceElement[] traceElements = Thread.currentThread().getStackTrace();
            for (StackTraceElement traceElement : traceElements) {
                la.Append(Level.DEBUG, traceElement.toString());
            }
        });
    }

    @Override
    public void Trace(String prefix, String[] lines) {
        logAppenderList.values().forEach((la) -> {
            for (String line : lines)
                la.Append(Level.TRACE, prefix + line);
        });
    }

    public <T> void Exception(T e) {
        if (e instanceof Exception) {
            Error("Message     ==> " + ((Exception) e).getMessage());
            Error("Stack Trace ==V ");
            StackTraceElement[] els = ((Exception) e).getStackTrace();
            for (StackTraceElement el : els) {
                Error(el.toString());
            }
        } else if (e instanceof Throwable) {
            Error("Message     ==> " + ((Throwable) e).getMessage());
            Error("Stack Trace ==V ");
            StackTraceElement[] els = ((Throwable) e).getStackTrace();
            for (StackTraceElement el : els) {
                Error(el.toString());
            }
        }
    }

    @Override
    public boolean HasAppender(LogAppender appender) {
        for (LogAppender myAppender : GetLogAppenderList()) {
            if (appender.getClass().getName().equals(myAppender.getClass().getName())) {
                if (appender instanceof FileLogAppender && myAppender instanceof FileLogAppender) {
                    if (((FileLogAppender) appender).GetFullPath().equals(((FileLogAppender) myAppender).GetFullPath()))
                        return true;
                } else {
                    return true;
                }
            }
        }

        return false;
    }

    public AppenderData GetAppender(LogAppender appender) {
        if (!HasAppender(appender))
            return null;

        for (String appenderKey : logAppenderList.keySet()) {
            LogAppender myAppender = logAppenderList.get(appenderKey);
            if (appender.getClass().getName().equals(myAppender.getClass().getName())) {
                if (appender instanceof FileLogAppender && myAppender instanceof FileLogAppender) {
                    if (((FileLogAppender) appender).GetFullPath().equals(((FileLogAppender) myAppender).GetFullPath()))
                        return new AppenderData(appenderKey, myAppender);
                } else {
                    return new AppenderData(appenderKey, myAppender);
                }
            }
        }

        return null;
    }

    @Override
    public void UpdateAppender(LogAppender appender, long timeMilli) {
        AppenderData appenderData;
        appender.SetLastModified(timeMilli);
        if ((appenderData = GetAppender(appender)) != null) {
            logAppenderList.remove(appenderData.GetKey());
            logAppenderList.put(appenderData.GetKey(), appender);
        } else {
            AddAppender(appender);
        }
    }

    @Override
    public void RemoveAppender(long timeMillis) {
        List<String> keyForDeletion = new ArrayList<>();
        for (String key : this.logAppenderList.keySet()) {
            LogAppender appender = logAppenderList.get(key);
            if (appender.GetLastModified() < timeMillis) {
                keyForDeletion.add(key);
            }
        }

        for (String key : keyForDeletion) {
            logAppenderList.remove(key);
        }
    }

    @Override
    public void SetLevel(Level level) {
        this.logAppenderList.values().forEach((la) -> {
            la.SetLevel(level);
        });
    }

    private static class AppenderData {
        private String key;
        private LogAppender appender;

        public AppenderData(String key, LogAppender appender) {
            this.key = key;
            this.appender = appender;
        }

        public String GetKey() {
            return key;
        }

        public LogAppender GetAppender() {
            return appender;
        }
    }
}
